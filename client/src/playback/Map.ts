import { flatbuffers, schema } from 'battlecode-schema'
import assert from 'assert'
import { Vector } from './Vector'
import Match from './Match'
import { MapEditorBrush, Symmetry } from '../components/sidebar/map-editor/MapEditorBrush'
import { packVecTable, parseVecTable } from './SchemaHelpers'
import { RuinsBrush, WallsBrush, PaintBrush, MarkerBrush } from './Brushes'
import { TEAM_COLOR_NAMES } from '../constants'
import * as renderUtils from '../util/RenderUtil'
import { getImageIfLoaded } from '../util/ImageLoader'
import { ClientConfig } from '../client-config'
import { Colors, currentColors, getPaintColors, getTeamColors } from '../colors'
import Round from './Round'
import { GameConfig } from '../app-context'

export type Dimension = {
    minCorner: Vector
    maxCorner: Vector
    width: number
    height: number
}

type SchemaPacket = {
    wallsOffset: number
    paintOffset: number
    ruinsOffset: number
}

type ResourcePatternData = {
    teamId: number
    center: Vector
    createRound: number
}

export class CurrentMap {
    public readonly staticMap: StaticMap
    public readonly paint: Int8Array
    public readonly markers: [Int8Array, Int8Array] // Each team has markers
    public readonly resourcePatterns: ResourcePatternData[]

    get width(): number {
        return this.dimension.width
    }
    get height(): number {
        return this.dimension.height
    }
    get dimension(): Dimension {
        return this.staticMap.dimension
    }

    constructor(from: StaticMap | CurrentMap) {
        //this.flagData = new Map()
        if (from instanceof StaticMap) {
            // Create current map from static map

            this.staticMap = from
            this.paint = new Int8Array(from.initialPaint)
            this.markers = [new Int8Array(this.width * this.height), new Int8Array(this.width * this.height)]
            this.resourcePatterns = []
        } else {
            // Create current map from current map (copy)

            this.staticMap = from.staticMap
            this.paint = new Int8Array(from.paint)
            this.markers = [new Int8Array(from.markers[0]), new Int8Array(from.markers[1])]

            // Assumes ResourcePatternData is immutable
            this.resourcePatterns = [...from.resourcePatterns]
        }
    }

    indexToLocation(index: number): { x: number; y: number } {
        return this.staticMap.indexToLocation(index)
    }

    locationToIndex(x: number, y: number): number {
        return this.staticMap.locationToIndex(x, y)
    }

    locationToIndexUnchecked(x: number, y: number): number {
        return this.staticMap.locationToIndexUnchecked(x, y)
    }

    applySymmetry(point: Vector): Vector {
        return this.staticMap.applySymmetry(point)
    }

    copy(): CurrentMap {
        return new CurrentMap(this)
    }

    /**
     * Mutates this currentMap to reflect the given round.
     */
    applyRoundDelta(round: Round, delta: schema.Round | null): void {
        // Update resource patterns and remove if they have been broken
        const patternMask = 28873275
        for (let i = 0; i < this.resourcePatterns.length; i++) {
            const srp = this.resourcePatterns[i]
            let patternIdx = 0
            let patternFailed = false
            for (let y = srp.center.y + 2; y >= srp.center.y - 2; y--) {
                for (let x = srp.center.x - 2; x <= srp.center.x + 2; x++) {
                    const idx = this.locationToIndex(x, y)
                    const expectedPaint = ((patternMask >> patternIdx) & 1) + (srp.teamId - 1) * 2 + 1
                    const actualPaint = this.paint[idx]
                    if (actualPaint !== expectedPaint) {
                        this.resourcePatterns[i] = this.resourcePatterns[this.resourcePatterns.length - 1]
                        this.resourcePatterns.pop()
                        i--
                        patternFailed = true
                        break
                    }
                    patternIdx++
                }

                if (patternFailed) break
            }
        }
    }

    draw(
        match: Match,
        ctx: CanvasRenderingContext2D,
        config: ClientConfig,
        selectedBodyID?: number,
        hoveredTile?: Vector
    ) {
        const dimension = this.dimension
        const paintColors = getPaintColors()
        const teamColors = getTeamColors()
        for (let i = 0; i < dimension.width; i++) {
            for (let j = 0; j < dimension.height; j++) {
                const schemaIdx = this.locationToIndexUnchecked(i, j)
                const coords = renderUtils.getRenderCoords(i, j, dimension)

                // Render rounded (clipped) paint
                const paint = this.paint[schemaIdx]
                if (paint) {
                    ctx.fillStyle = paintColors[paint]
                    if (config.enableCircles) {
                        ctx.beginPath()
                        if (config.pisonFip) {
                            ctx.ellipse(coords.x + 0.5, coords.y + 0.5 + Math.sin(coords.x + performance.now() / 1000), 0.5 + Math.cos(coords.y + performance.now() / 1200) / 8, 0.5 + Math.sin(coords.y + performance.now() / 1200) / 8, 0, 0, Math.PI * 2)
                        } else {
                            ctx.ellipse(coords.x + 0.5, coords.y + 0.5, 0.5, 0.5, 0, 0, Math.PI * 2)
                        }
                        ctx.fill()
                    } else {
                        if (config.pisonFip) {
                            const w = 1 + Math.cos(coords.y + performance.now() / 1200) / 8
                            const h = 1 + Math.sin(coords.y + performance.now() / 1200) / 8
                            ctx.fillRect(coords.x - w / 2 + 0.5, coords.y - h / 2 + 0.5 + Math.sin(coords.x + performance.now() / 1000), w, h)
                        } else {
                            if (!config.pixelSimulator && config.enableFancyPaint) {
                                renderUtils.renderRounded(
                                    ctx,
                                    i,
                                    j,
                                    this,
                                    this.paint,
                                    () => ctx.fillRect(coords.x, coords.y, 1.0, 1.0),
                                    { x: true, y: false }
                                )
                            } else {
                                ctx.fillRect(coords.x, coords.y, 1.0, 1.0)
                            }
                        }
                    }
                }
            }
        }
        for (let i = 0; i < dimension.width; i++) {
            for (let j = 0; j < dimension.height; j++) {
                const schemaIdx = this.locationToIndexUnchecked(i, j)
                const coords = renderUtils.getRenderCoords(i, j, dimension)
                // make sure to preserve dev spaghetti buhs
                if (config.showPaintMarkers) {
                    // Scale text by 0.5 because sometimes 0.5px text does not work

                    const markerA = this.markers[0][schemaIdx]
                    if (markerA) {
                        ctx.fillStyle = teamColors[0]
                        const label = markerA === 1 ? '1' : '2' // Primary/secondary
                        if (config.largeText)
                            ctx.font = '10px monospace'
                        else
                            ctx.font = '1px monospace'
                        ctx.shadowColor = 'black'
                        ctx.shadowBlur = 4
                        ctx.scale(0.5, 0.5)
                        ctx.fillText(label, (coords.x + 0.05) * 2, (coords.y + 0.95) * 2)
                        ctx.scale(2, 2)
                        ctx.shadowColor = ''
                        ctx.shadowBlur = 0
                    }

                    const markerB = this.markers[1][schemaIdx]
                    if (markerB) {
                        ctx.fillStyle = teamColors[1]
                        const label = markerB === 3 ? '1' : '2' // Primary/secondary
                        if (config.largeText)
                            ctx.font = '10px monospace'
                        else
                            ctx.font = '1px monospace'
                        ctx.shadowColor = 'black'
                        ctx.shadowBlur = 4
                        ctx.scale(0.5, 0.5)
                        ctx.fillText(label, (coords.x + 0.65) * 2, (coords.y + 0.95) * 2)
                        ctx.scale(2, 2)
                        ctx.shadowColor = ''
                        ctx.shadowBlur = 0
                    }
                }
            }
        }

        if (config.showSRPOutlines || config.showSRPText) {
            ctx.globalAlpha = 1
            ctx.lineWidth = 0.1
            this.resourcePatterns.forEach((srp) => {
                const topLeftCoords = renderUtils.getRenderCoords(srp.center.x - 2, srp.center.y + 2, this.dimension)
                const roundsRemaining = Math.max(srp.createRound + 50 - match.currentRound.roundNumber, -1)
                if (roundsRemaining >= 0 && config.showSRPText) {
                    const label = roundsRemaining.toString()
                    ctx.fillStyle = 'white'
                    ctx.textAlign = 'right'
                    if (config.largeText)
                        ctx.font = '10px monospace'
                    else
                        ctx.font = '1px monospace'
                    ctx.shadowColor = 'black'
                    ctx.shadowBlur = 8
                    ctx.scale(0.4, 0.4)
                    ctx.fillText(label, (topLeftCoords.x + 3) * 2.5, (topLeftCoords.y + 2.5) * 2.5)
                    ctx.scale(2.5, 2.5)
                    ctx.shadowColor = ''
                    ctx.shadowBlur = 0
                    ctx.textAlign = 'start'
                } else if (roundsRemaining === -1 && config.showSRPOutlines) {
                    if (config.enableObviousSrp) {
                        ctx.fillStyle = teamColors[srp.teamId - 1]
                        ctx.fillRect(topLeftCoords.x, topLeftCoords.y, 5, 5)
                    } else {
                        ctx.strokeStyle = teamColors[srp.teamId - 1]
                        ctx.strokeRect(topLeftCoords.x, topLeftCoords.y, 5, 5)
                    }
                }
            })
        }
    }

    getTooltipInfo(square: Vector, match: Match): string[] {
        // Bounds check
        if (square.x >= this.width || square.y >= this.height) return []

        const schemaIdx = this.locationToIndex(square.x, square.y)

        const paint = this.paint[schemaIdx]
        const wall = this.staticMap.walls[schemaIdx]
        const ruin = this.staticMap.ruins.find((r) => r.x === square.x && r.y === square.y)
        const srp = this.resourcePatterns.find((r) => r.center.x === square.x && r.center.y === square.y)
        const markerA = this.markers[0][schemaIdx]
        const markerB = this.markers[1][schemaIdx]

        const info: string[] = []
        for (let i = 0; i < match.game.teams.length; i++) {
            if (paint === i * 2 + 1) {
                info.push(`${TEAM_COLOR_NAMES[i]} Paint (Primary)`)
            } else if (paint === i * 2 + 2) {
                info.push(`${TEAM_COLOR_NAMES[i]} Paint (Secondary)`)
            }
        }
        if (markerA) {
            info.push(`Silver Marker (${markerA === 1 ? 'Primary' : 'Secondary'})`)
        }
        if (markerB) {
            info.push(`Gold Marker (${markerB === 3 ? 'Primary' : 'Secondary'})`)
        }
        if (wall) {
            info.push('Wall')
        }
        if (ruin) {
            info.push('Ruin')
        }
        if (srp) {
            const roundsRemaining = Math.max(srp.createRound + 50 - match.currentRound.roundNumber, 0)
            if (roundsRemaining === 0) {
                info.push(`${TEAM_COLOR_NAMES[srp.teamId - 1]} SRP Center (Active)`)
            } else {
                info.push(`${TEAM_COLOR_NAMES[srp.teamId - 1]} SRP Center (${roundsRemaining} Rounds Left)`)
            }
        }

        return info
    }

    getEditorBrushes(round: Round) {
        const brushes: MapEditorBrush[] = [new PaintBrush(round), new MarkerBrush(round), new RuinsBrush(round), new WallsBrush(round)]
        return brushes.concat(this.staticMap.getEditorBrushes())
    }

    isEmpty(): boolean {
        return this.paint.every((x) => x == 0) && this.staticMap.isEmpty()
    }

    /**
     * Creates a packet of flatbuffers data which will later be inserted
     * This and the next function are seperated due to how flatbuffers works
     */
    getSchemaPacket(builder: flatbuffers.Builder): SchemaPacket {
        const wallsOffset = schema.GameMap.createWallsVector(
            builder,
            Array.from(this.staticMap.walls).map((x) => !!x)
        )
        const paintOffset = schema.GameMap.createPaintVector(builder, this.staticMap.initialPaint)
        const ruinsOffset = packVecTable(builder, this.staticMap.ruins)

        return {
            wallsOffset,
            paintOffset,
            ruinsOffset
        }
    }

    /**
     * Inserts an existing packet of flatbuffers data into the given builder
     * This and the previous function are seperated due to how flatbuffers works
     */
    insertSchemaPacket(builder: flatbuffers.Builder, packet: SchemaPacket) {
        schema.GameMap.addWalls(builder, packet.wallsOffset)
        schema.GameMap.addPaint(builder, packet.paintOffset)
        schema.GameMap.addRuins(builder, packet.ruinsOffset)
    }
}

export class StaticMap {
    constructor(
        public name: string,
        public readonly randomSeed: number, // I dont know what this is for
        public readonly symmetry: number,
        public readonly dimension: Dimension,
        public readonly walls: Int8Array,
        public readonly ruins: Vector[],
        public readonly initialPaint: Int8Array
    ) {
        if (symmetry < 0 || symmetry > 2 || !Number.isInteger(symmetry)) {
            throw new Error(`Invalid symmetry ${symmetry}`)
        }

        if (walls.length != dimension.width * dimension.height) {
            throw new Error('Invalid walls length')
        }
        if (initialPaint.length != dimension.width * dimension.height) {
            throw new Error('Invalid paint length')
        }

        if (walls.some((x) => x !== 0 && x !== 1)) {
            throw new Error('Invalid walls value')
        }
        if (initialPaint.some((x) => x < 0 || x > 4)) {
            throw new Error('Invalid paint value')
        }
    }

    static fromSchema(schemaMap: schema.GameMap) {
        const name = schemaMap.name() as string
        const randomSeed = schemaMap.randomSeed()
        const symmetry = schemaMap.symmetry()

        const size = schemaMap.size() ?? assert.fail('Map size() is missing')
        const minCorner = { x: 0, y: 0 }
        const maxCorner = { x: size.x(), y: size.y() }
        const dimension = {
            minCorner,
            maxCorner,
            width: maxCorner.x - minCorner.x,
            height: maxCorner.y - minCorner.y
        }

        const walls = schemaMap.wallsArray() ?? assert.fail('wallsArray() is null')
        const ruins = parseVecTable(schemaMap.ruins() ?? assert.fail('ruins() is null'))
        const initialPaint = schemaMap.paintArray() ?? assert.fail('paintArray() is null')
        return new StaticMap(name, randomSeed, symmetry, dimension, walls, ruins, initialPaint)
    }

    static fromParams(width: number, height: number, symmetry: Symmetry) {
        const name = 'Custom Map'
        const randomSeed = 0

        const minCorner = { x: 0, y: 0 }
        const maxCorner = { x: width, y: height }
        const dimension = {
            minCorner,
            maxCorner,
            width: maxCorner.x - minCorner.x,
            height: maxCorner.y - minCorner.y
        }

        const walls = new Int8Array(width * height)
        const ruins: Vector[] = []
        const initialPaint = new Int8Array(width * height)
        return new StaticMap(name, randomSeed, symmetry, dimension, walls, ruins, initialPaint)
    }

    get width(): number {
        return this.dimension.width
    }
    get height(): number {
        return this.dimension.height
    }

    indexToLocation(index: number): { x: number; y: number } {
        const x = index % this.width
        const y = (index - x) / this.width
        assert(x >= 0 && x < this.width, `x=${x} out of bounds for indexToLocation`)
        assert(y >= 0 && y < this.height, `y=${y} out of bounds for indexToLocation`)
        return { x, y }
    }

    locationToIndex(x: number, y: number): number {
        assert(x >= 0 && x < this.width, `x ${x} out of bounds`)
        assert(y >= 0 && y < this.height, `y ${y} out of bounds`)
        return Math.floor(y) * this.width + Math.floor(x)
    }

    locationToIndexUnchecked(x: number, y: number): number {
        return y * this.width + x
    }

    /**
     * Returns a point representing the reflection of the given point following the map's symmetry.
     */
    applySymmetry(point: Vector): Vector {
        switch (this.symmetry) {
            case Symmetry.VERTICAL:
                return { x: this.width - point.x - 1, y: point.y }
            case Symmetry.HORIZONTAL:
                return { x: point.x, y: this.height - point.y - 1 }
            case Symmetry.ROTATIONAL:
                return { x: this.width - point.x - 1, y: this.height - point.y - 1 }
            default:
                throw new Error(`Invalid symmetry ${this.symmetry}`)
        }
    }

    draw(ctx: CanvasRenderingContext2D) {
        // Fill background
        ctx.fillStyle = currentColors[Colors.TILE_COLOR]
        ctx.fillRect(
            this.dimension.minCorner.x,
            this.dimension.minCorner.y,
            this.dimension.width,
            this.dimension.height
        )

        // const dirtImg = getImageIfLoaded('dirty.png')
        // if (dirtImg) {
        //     ctx.drawImage(
        //         dirtImg,
        //         this.dimension.minCorner.x,
        //         this.dimension.minCorner.y,
        //         this.dimension.width,
        //         this.dimension.height
        //     )
        // }
        if (GameConfig.config.theBlob) {
            const blob = getImageIfLoaded('the blob.png')
            if (blob) {
                ctx.globalAlpha = 0.2
                ctx.drawImage(
                    blob,
                    this.dimension.minCorner.x,
                    this.dimension.minCorner.y,
                    this.dimension.width,
                    this.dimension.height
                )
                ctx.globalAlpha = 1
            }
        }

        for (let i = 0; i < this.dimension.width; i++) {
            for (let j = 0; j < this.dimension.height; j++) {
                const schemaIdx = this.locationToIndexUnchecked(i, j)
                const coords = renderUtils.getRenderCoords(i, j, this.dimension)

                // Render rounded (clipped) wall
                if (this.walls[schemaIdx]) {
                    ctx.fillStyle = currentColors[Colors.WALLS_COLOR]
                    if (GameConfig.config.enableCircles) {
                        ctx.beginPath()
                        ctx.arc(coords.x + 0.5, coords.y + 0.5, 0.5, 0, Math.PI * 2);
                        ctx.fill();
                    } else if (!GameConfig.config.pixelSimulator) {
                        renderUtils.renderRounded(ctx, i, j, this, this.walls, () => {
                            ctx.fillRect(coords.x, coords.y, 1.0, 1.0)
                        })
                    } else {
                        ctx.fillRect(coords.x, coords.y, 1.0, 1.0)
                    }
                }

                // Draw grid
                const showGrid = true
                if (showGrid) {
                    const thickness = 0.02
                    renderUtils.applyStyles(
                        ctx,
                        { strokeStyle: 'black', lineWidth: thickness, globalAlpha: 0.1 },
                        () => {
                            ctx.strokeRect(
                                coords.x + thickness / 2,
                                coords.y + thickness / 2,
                                1 - thickness,
                                1 - thickness
                            )
                        }
                    )
                }
            }
        }

        // Render ruins
        this.ruins.forEach(({ x, y }) => {
            const coords = renderUtils.getRenderCoords(x, y, this.dimension)

            const imgPath = `ruins/silver_64x64.png`
            const ruinImage = getImageIfLoaded(imgPath)
            renderUtils.renderCenteredImageOrLoadingIndicator(ctx, ruinImage, coords, 1.0)
        })
    }

    isEmpty(): boolean {
        return this.walls.every((x) => x == 0) && this.ruins.length == 0
    }

    getEditorBrushes(): MapEditorBrush[] {
        return []
    }
}
