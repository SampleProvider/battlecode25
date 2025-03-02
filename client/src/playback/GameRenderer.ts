import React from 'react'
import GameRunner from './GameRunner'
import { TEAM_COLOR_NAMES, TILE_RESOLUTION } from '../constants'
import { Vector } from './Vector'
import assert from 'assert'
import { GameConfig } from '../app-context'
import { loadImage } from '../util/ImageLoader'
import { Team } from './Game'

export enum CanvasLayers {
    Background,
    Dynamic,
    Overlay
}

// dont mind the spaghetti
const audioCtx = new AudioContext();
export const globalGain = audioCtx.createGain();
globalGain.connect(audioCtx.destination);
globalGain.gain.value = 1;

var noSound = false;
export function setNoSound(s: boolean) {
    noSound = s;
}

const buffers: any = {};
export function playSound(name: any, gain: any, randomDetune?: any) {
    if (noSound) {
        return;
    }
    var buffer = buffers[name];
    if (Array.isArray(buffers[name])) {
        buffer = buffers[name][Math.floor(Math.random() * buffers[name].length)];
    }
    if (!(buffer instanceof AudioBuffer)) {
        return false;
    }
    var detune = 0;
    if (randomDetune != null) {
        detune = Math.random() * randomDetune * 2 - randomDetune;
        detune = 0;
    }
    if (GameConfig.config.excessiveDetune) {
        detune = (Math.random() * 5000 + 1000) * (Math.random() < 0.5 ? -1 : 1)
    }
    const sampleSource = new AudioBufferSourceNode(audioCtx, {
        buffer: buffer,
        playbackRate: 1,
        detune: detune,
    });
    var gainNode = audioCtx.createGain();
    sampleSource.connect(gainNode);
    gainNode.gain.value = gain;
    gainNode.connect(globalGain);
    sampleSource.start(0);
    return sampleSource;
}

async function getFile(filepath: any) {
    const response = await fetch(filepath);
    const arrayBuffer = await response.arrayBuffer();
    const audioBuffer = await audioCtx.decodeAudioData(arrayBuffer);
    return audioBuffer;
}
function addFile(name: string, filepath: string) {
    getFile(filepath).then((result) => {
        buffers[name] = result;
    });
}
function addMultipleFiles(name: string, filepath: string, extension: string, number: number) {
    buffers[name] = [];
    for (var i = 1; i <= number; i++) {
        getFile(filepath + i + extension).then((result) => {
            buffers[name].push(result);
        });
    }
}
addFile("buh", "static/img/audio/buh.wav");
addFile("villager", "static/img/audio/villager.ogg");
addFile("battle noble advanced", "static/img/audio/battle noble advanced.wav");
addFile("chess battle advanced", "static/img/audio/chess battle advanced.ogg");
addFile("code battle advanced", "static/img/audio/code battle advanced.ogg");
// addMultipleFiles("paint", "static/img/audio/slime", ".ogg", 3);
addMultipleFiles("paint", "static/img/audio/honey", ".ogg", 5);
addMultipleFiles("attack", "static/img/audio/Strong_attack", ".ogg.mp3", 6);
addMultipleFiles("sweep", "static/img/audio/Sweep_attack", ".ogg.mp3", 7);
addMultipleFiles("mop", "static/img/audio/Weak_attack", ".ogg.mp3", 4);
addMultipleFiles("splash", "static/img/audio/water", ".ogg", 3);
addMultipleFiles("disintegrate", "static/img/audio/Explosion", ".ogg", 4);
addMultipleFiles("spawn", "static/img/audio/Smithing_Table", ".ogg.mp3", 3);
addFile("death", "static/img/audio/Hurt_Old.ogg");
addFile("build", "static/img/audio/anvil use.ogg");
addFile("upgrade", "static/img/audio/Random_levelup.ogg");
addFile("revive", "static/img/audio/Totem_of_Undying.ogg");
addFile("hurt", "static/img/audio/Player_hurt1.ogg.mp3");
addFile("win", "static/img/audio/Challenge_complete.ogg");
// addFile("death", "static/img/audio/Random_break.ogg");
addFile("bytecode", "static/img/audio/erro.mp3");

export const clogWillSmogGrid: boolean[][] = [];

class GameRendererClass {
    private canvases: Record<CanvasLayers, HTMLCanvasElement>
    private mouseTile?: Vector = undefined
    private mouseDownStartPos?: Vector = undefined
    private mouseDown: boolean = false
    private mouseDownRight: boolean = false
    private selectedBodyID?: number = undefined
    private selectedTile?: Vector = undefined

    private _canvasHoverListeners: (() => void)[] = []
    private _canvasClickListeners: (() => void)[] = []

    constructor() {
        this.canvases = {} as Record<CanvasLayers, HTMLCanvasElement>
        for (const layer of Object.values(CanvasLayers).filter((value) => typeof value === 'number')) {
            const canvas = document.createElement('canvas')
            canvas.style.position = 'absolute'
            canvas.style.top = '50%'
            canvas.style.left = '50%'
            canvas.style.maxWidth = '100%'
            canvas.style.maxHeight = '100%'
            canvas.style.transform = 'translate(-50%, -50%)'
            canvas.style.zIndex = (Object.values(this.canvases).length + 1).toString()
            // oh noes
            this.canvases[layer as CanvasLayers] = canvas
        }

        const topCanvas = Object.values(this.canvases)[Object.values(this.canvases).length - 1]
        topCanvas.onmousedown = (e) => this.canvasMouseDown(e)
        topCanvas.onmouseup = (e) => this.canvasMouseUp(e)
        topCanvas.onmousemove = (e) => this.canvasMouseMove(e)
        topCanvas.onmouseleave = (e) => this.canvasMouseLeave(e)
        topCanvas.onmouseenter = (e) => this.canvasMouseEnter(e)
        topCanvas.onclick = (e) => this.canvasClick(e)
        topCanvas.oncontextmenu = (e) => e.preventDefault()

        // Preload all game images
        loadImage('icons/gears_64x64.png')
        loadImage('icons/hammer_64x64.png')
        loadImage('icons/mop_64x64.png')
        loadImage('ruins/silver.png')
        for (const color of TEAM_COLOR_NAMES) {
            loadImage(`robots/${color.toLowerCase()}/defense_tower_64x64.png`)
            loadImage(`robots/${color.toLowerCase()}/money_tower_64x64.png`)
            loadImage(`robots/${color.toLowerCase()}/paint_tower_64x64.png`)
            loadImage(`robots/${color.toLowerCase()}/soldier_64x64.png`)
            loadImage(`robots/${color.toLowerCase()}/splasher_64x64.png`)
            loadImage(`robots/${color.toLowerCase()}/mopper_64x64.png`)
        }
        loadImage('the blob.png')
        loadImage('1-4 2lights.png')
        loadImage('2525.png')
        loadImage('apple.png')
        loadImage('apple2.png')
        loadImage('rickastley.png')
    }

    clearSelected() {
        this.mouseTile = undefined
        this.selectedTile = undefined
        this.selectedBodyID = undefined
        this.render()
        this._canvasClickListeners.forEach((listener) => listener())
        this._canvasHoverListeners.forEach((listener) => listener())
    }

    setSelectedRobot(id: number | undefined) {
        if (id === this.selectedBodyID) return

        this.selectedBodyID = id
        this.render()
        this._trigger(this._canvasClickListeners)
    }

    addCanvasesToDOM(elem: HTMLDivElement | null) {
        if (!elem) return
        for (const canvas of Object.values(this.canvases)) {
            elem.appendChild(canvas)
        }
    }

    canvas(layer: CanvasLayers): HTMLCanvasElement {
        return this.canvases[layer]
    }

    ctx(layer: CanvasLayers): CanvasRenderingContext2D | null {
        return this.canvas(layer).getContext('2d')
    }

    renderOverlay() {
        const ctx = this.ctx(CanvasLayers.Overlay)
        const match = GameRunner.match
        if (!match || !ctx) return

        const currentRound = match.currentRound

        ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
        currentRound.bodies.draw(match, null, ctx, GameConfig.config, this.selectedBodyID, this.mouseTile)
    }

    render() {
        const ctx = this.ctx(CanvasLayers.Dynamic)
        const overlayCtx = this.ctx(CanvasLayers.Overlay)
        const match = GameRunner.match
        if (!match || !ctx || !overlayCtx) return

        const currentRound = match.currentRound

        // pixel simulator
        if (GameConfig.config.pixelSimulator) {
            const grid: number[][] = [];
            const nextGrid: number[][] = [];
            const width = currentRound.map.width;
            const height = currentRound.map.height;
            for (var y = 0; y < height; y++) {
                var y2 = GameConfig.config.rotalumisLexip ? height - y - 1 : y
                grid[y2] = [];
                nextGrid[y2] = [];
                for (var x = 0; x < width; x++) {
                    grid[y2][x] = 0;
                    nextGrid[y2][x] = -1;
                    if (currentRound.map.paint[y * width + x] == 3) {
                        grid[y2][x] = 1; // gold primary, sand
                    }
                    if (currentRound.map.paint[y * width + x] == 4) {
                        grid[y2][x] = 3; // gold secondary, lava
                    }
                    if (currentRound.map.paint[y * width + x] == 1) {
                        grid[y2][x] = 4; // silver primary, concrete powder
                    }
                    if (currentRound.map.paint[y * width + x] == 2) {
                        grid[y2][x] = 2; // silver secondary, water
                    }
                    if (currentRound.map.staticMap.walls[y * width + x] == 1) {
                        grid[y2][x] = 5; // concrete
                    }
                }
            }
            function isTouching(x: any, y: any, array: any) {
                if (x > 0) {
                    for (let i in array) {
                        if (grid[y][x - 1] == array[i]) {
                            return true;
                        }
                    }
                }
                if (x < width - 1) {
                    for (let i in array) {
                        if (grid[y][x + 1] == array[i]) {
                            return true;
                        }
                    }
                }
                if (y > 0) {
                    for (let i in array) {
                        if (grid[y - 1][x] == array[i]) {
                            return true;
                        }
                    }
                }
                if (y < height - 1) {
                    for (let i in array) {
                        if (grid[y + 1][x] == array[i]) {
                            return true;
                        }
                    }
                }
                return false;
            };
            function forTouching(x: any, y: any, action: any) {
                if (x > 0) {
                    action(x - 1, y);
                }
                if (x < width - 1) {
                    action(x + 1, y);
                }
                if (y > 0) {
                    action(x, y - 1);
                }
                if (y < height - 1) {
                    action(x, y + 1);
                }
            };
            function move(x1: number, y1: number, x2: number, y2: number) {
                nextGrid[y1][x1] = grid[y2][x2];
                nextGrid[y2][x2] = grid[y1][x1];
            }
            function flowSearch(x: any, y: any, distance: any, h: any, isPassable: any, isMoveable: any) {
                if (y < h) {
                    return false;
                }
                let left = 0;
                let right = 0;
                for (let i = 1; i <= distance; i++) {
                    if (left < 0) {

                    }
                    else if (!isMoveable(x - i, y)) {
                        left = -i;
                        if (isPassable(x - i + 1, y - 1) && !isPassable(x - i, y)) {
                            let air = true;
                            for (let j = 1; j <= h; j++) {
                                if (!isMoveable(x - i, y - j)) {
                                    air = false;
                                    break;
                                }
                            }
                            if (air) {
                                left = 1;
                            }
                        }
                    }
                    else {
                        let air = true;
                        for (let j = 0; j <= h; j++) {
                            if (!isMoveable(x - i, y - j)) {
                                air = false;
                                break;
                            }
                        }
                        if (air) {
                            left = 1;
                        }
                    }
                    if (right < 0) {

                    }
                    else if (!isMoveable(x + i, y)) {
                        right = -i;
                        if (isPassable(x + i - 1, y - 1) && !isPassable(x + i, y)) {
                            let air = true;
                            for (let j = 1; j <= h; j++) {
                                if (!isMoveable(x + i, y - j)) {
                                    air = false;
                                    break;
                                }
                            }
                            if (air) {
                                right = 1;
                            }
                        }
                    }
                    else {
                        let air = true;
                        for (let j = 0; j <= h; j++) {
                            if (!isMoveable(x + i, y - j)) {
                                air = false;
                                break;
                            }
                        }
                        if (air) {
                            right = 1;
                        }
                    }
                    if (left == 1 || right == 1) {
                        if (left == 1 && right == 1) {
                            if (Math.random() < 0.5) {
                                return -i;
                            }
                            else {
                                return i;
                            }
                        }
                        else if (left == 1) {
                            return -i;
                        }
                        else if (right == 1) {
                            return i;
                        }
                    }
                    if (left < 0 && right < 0) {
                        // if (!isPassable(x, y - 1)) {
                        //     let leftAir = 0;
                        //     let rightAir = 0;
                        //     for (let j = i; j <= distance; j++) {
                        //         if (leftAir == 0 && !isPassable(x - j, y)) {
                        //             leftAir = j;
                        //         }
                        //         if (rightAir == 0 && !isPassable(x + j, y)) {
                        //             rightAir = j;
                        //         }
                        //         if (leftAir != 0 || rightAir != 0) {
                        //             if (leftAir != 0) {
                        //                 if (isMoveable(x - 1, y)) {
                        //                     return -i;
                        //                 }
                        //             }
                        //             else if (rightAir != 0) {
                        //                 if (isMoveable(x + 1, y)) {
                        //                     return i;
                        //                 }
                        //             }
                        //             break;
                        //         }
                        //     }
                        //     // if (left < right) {
                        //     //     if (isMoveable(x + 1, y)) {
                        //     //         return i;
                        //     //     }
                        //     //     // if (isPassable(x + 1, y) || isId(x + 1, y, WATER)) {
                        //     //     //     return -i;
                        //     //     // }
                        //     // }
                        //     // else if (right < left) {
                        //     //     if (isMoveable(x - 1, y)) {
                        //     //         return -i;
                        //     //     }
                        //     //     // if (isPassable(x - 1, y) || isId(x - 1, y, WATER)) {
                        //     //     //     return i;
                        //     //     // }
                        //     // }
                        // }
                        if (left == -1 && right == -1) {
                            return false;
                        }
                        return 0;
                    }
                }
                return 0;
            };
            function flow(x: any, y: any, distance: any, h: any, isPassable: any, isMoveable: any) {
                if (isMoveable(x, y - 1)) {
                    move(x, y, x, y - 1);
                    return;
                }
                let direction = flowSearch(x, y, distance, h, isPassable, isMoveable);
                if (direction === false) {
                }
                else if (direction == 0) {
                }
                else if (Math.abs(direction) == 1) {
                    move(x, y, x + direction, y - 1);
                }
                else {
                    move(x, y, x + Math.sign(direction), y);
                }
            };
            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    if (nextGrid[y][x] != -1) {
                        continue;
                    }
                    switch (grid[y][x]) {
                        case 1:
                            flow(x, y, 1, 1, function (x1: any, y1: any) {
                                return (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) && (grid[y1][x1] == 0 || grid[y1][x1] == 2 || grid[y1][x1] == 3);
                            }, function (x1: any, y1: any) {
                                return (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) && (grid[y1][x1] == 0 || grid[y1][x1] == 2 || grid[y1][x1] == 3) && nextGrid[y1][x1] == -1;
                            });
                            break;
                        case 2:
                            let changed = false;
                            forTouching(x, y, (x1: any, y1: any) => {
                                if (grid[y1][x1] != 3) {
                                    return;
                                }
                                nextGrid[y1][x1] = 5;
                                changed = true;
                            });

                            if (changed) {
                                nextGrid[y][x] = 0;
                                break;
                            }
                            flow(x, y, width, 1, function (x1: any, y1: any) {
                                return (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) && (grid[y1][x1] == 0 || grid[y1][x1] == 2);
                            }, function (x1: any, y1: any) {
                                return (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) && grid[y1][x1] == 0 && nextGrid[y1][x1] == -1;
                            });
                            break;
                        case 3:
                            if (Math.random() < 0.5) {
                                flow(x, y, width, 1, function (x1: any, y1: any) {
                                    return (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) && (grid[y1][x1] == 0 || grid[y1][x1] == 3);
                                }, function (x1: any, y1: any) {
                                    return (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) && grid[y1][x1] == 0 && nextGrid[y1][x1] == -1;
                                });
                            }
                            break;
                        case 4:
                            if (isTouching(x, y, [2])) {
                                nextGrid[y][x] = 5;
                                break;
                            }
                            flow(x, y, 1, 2, function (x1: any, y1: any) {
                                return (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) && (grid[y1][x1] == 0 || grid[y1][x1] == 2 || grid[y1][x1] == 3);
                            }, function (x1: any, y1: any) {
                                return (x1 >= 0 && x1 < width && y1 >= 0 && y1 < height) && (grid[y1][x1] == 0 || grid[y1][x1] == 2 || grid[y1][x1] == 3) && nextGrid[y1][x1] == -1;
                            });
                            break;
                    }
                }
            }
            for (var y = 0; y < height; y++) {
                var y2 = GameConfig.config.rotalumisLexip ? height - y - 1 : y
                for (var x = 0; x < width; x++) {
                    switch (nextGrid[y2][x]) {
                        case 0:
                            currentRound.map.staticMap.walls[y * width + x] = 0;
                            currentRound.map.paint[y * width + x] = 0;
                            currentRound.map.staticMap.initialPaint[y * width + x] = 0;
                            break;
                        case 1:
                            currentRound.map.staticMap.walls[y * width + x] = 0;
                            currentRound.map.paint[y * width + x] = 3;
                            currentRound.map.staticMap.initialPaint[y * width + x] = 3;
                            break;
                        case 2:
                            currentRound.map.staticMap.walls[y * width + x] = 0;
                            currentRound.map.paint[y * width + x] = 2;
                            currentRound.map.staticMap.initialPaint[y * width + x] = 2;
                            break;
                        case 3:
                            currentRound.map.staticMap.walls[y * width + x] = 0;
                            currentRound.map.paint[y * width + x] = 4;
                            currentRound.map.staticMap.initialPaint[y * width + x] = 4;
                            break;
                        case 4:
                            currentRound.map.staticMap.walls[y * width + x] = 0;
                            currentRound.map.paint[y * width + x] = 1;
                            currentRound.map.staticMap.initialPaint[y * width + x] = 1;
                            break;
                        case 5:
                            currentRound.map.staticMap.walls[y * width + x] = 1;
                            currentRound.map.paint[y * width + x] = 0;
                            currentRound.map.staticMap.initialPaint[y * width + x] = 0;
                            break;
                    }
                }
            }
        }

        if (GameConfig.config.clogWillSmog) {
            clogWillSmogGrid.length = 0;
            const width = currentRound.map.width;
            const height = currentRound.map.height;
            for (var y = 0; y < height; y++) {
                clogWillSmogGrid[y] = [];
                for (var x = 0; x < width; x++) {
                    clogWillSmogGrid[y][x] = false;
                }
            }

            for (var [key, value] of currentRound.bodies.bodies) {
                if (value.team.id == 1) {
                    if (GameConfig.config.clogWillSmogA) {
                        var radius = 20;
                        const ceiledRadius = Math.ceil(Math.sqrt(radius)) + 1
                        const minX = Math.max(value.pos.x - ceiledRadius, 0)
                        const minY = Math.max(value.pos.y - ceiledRadius, 0)
                        const maxX = Math.min(value.pos.x + ceiledRadius, width - 1)
                        const maxY = Math.min(value.pos.y + ceiledRadius, height - 1)
                
                        for (let x = minX; x <= maxX; x++) {
                            for (let y = minY; y <= maxY; y++) {
                                const dx = x - value.pos.x;
                                const dy = y - value.pos.y;
                                if (dx * dx + dy * dy <= radius) {
                                    clogWillSmogGrid[y][x] = true;
                                }
                            }
                        }
                    }
                }
                else {
                    if (GameConfig.config.clogWillSmogB) {
                        var radius = 20;
                        const ceiledRadius = Math.ceil(Math.sqrt(radius)) + 1
                        const minX = Math.max(value.pos.x - ceiledRadius, 0)
                        const minY = Math.max(value.pos.y - ceiledRadius, 0)
                        const maxX = Math.min(value.pos.x + ceiledRadius, width - 1)
                        const maxY = Math.min(value.pos.y + ceiledRadius, height - 1)
                
                        for (let x = minX; x <= maxX; x++) {
                            for (let y = minY; y <= maxY; y++) {
                                const dx = x - value.pos.x;
                                const dy = y - value.pos.y;
                                if (dx * dx + dy * dy <= radius) {
                                    clogWillSmogGrid[y][x] = true;
                                }
                            }
                        }
                    }
                }
            }
        }


        ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height)
        overlayCtx.clearRect(0, 0, overlayCtx.canvas.width, overlayCtx.canvas.height)
        currentRound.map.draw(match, ctx, GameConfig.config, this.selectedBodyID, this.mouseTile)
        currentRound.bodies.draw(match, ctx, overlayCtx, GameConfig.config, this.selectedBodyID, this.mouseTile)
        currentRound.actions.draw(match, ctx)
    }

    fullRender() {
        const ctx = this.ctx(CanvasLayers.Background)
        const match = GameRunner.match
        if (!match || !ctx) return
        match.currentRound.map.staticMap.draw(ctx)
        if (GameConfig.config.enableOhNoesCanvas) {
            this.canvases[CanvasLayers.Background].style.filter = 'blur(50px) brightness(10) contrast(10) saturate(10) hue-rotate(90deg)'
            this.canvases[CanvasLayers.Dynamic].style.filter = 'blur(10px) saturate(1000)'
            this.canvases[CanvasLayers.Overlay].style.transform = 'translate(-50%, -50%) skew(5deg, -30deg) rotateY(120deg)'
        } else if (GameConfig.config.enableMiscCursedRendering) {
            this.canvases[CanvasLayers.Background].style.filter = 'blur(1px) hue-rotate(5deg)'
            if (GameConfig.config.highContrastMode)
                this.canvases[CanvasLayers.Dynamic].style.filter = 'contrast(2)'
            else
                this.canvases[CanvasLayers.Dynamic].style.filter = ''
            this.canvases[CanvasLayers.Overlay].style.transform = 'translate(-50%, -50%) skew(5deg, -2deg)'
        } else {
            this.canvases[CanvasLayers.Background].style.filter = ''
            if (GameConfig.config.highContrastMode)
                this.canvases[CanvasLayers.Dynamic].style.filter = 'contrast(2)'
            else
                this.canvases[CanvasLayers.Dynamic].style.filter = ''
            this.canvases[CanvasLayers.Overlay].style.transform = 'translate(-50%, -50%)'
        }
        this.render()
    }

    onMatchChange() {
        const match = GameRunner.match
        if (!match) return
        const { width, height } = match.currentRound.map
        this.updateCanvasDimensions({ x: width, y: height })
        this.selectedTile = undefined
        this.mouseTile = undefined
        this.selectedBodyID = undefined
        this.fullRender()
    }

    private updateCanvasDimensions(dims: Vector) {
        for (const canvas of Object.values(this.canvases)) {
            // perf issues, prefer config.renderscale
            const dpi = 1 //window.devicePixelRatio ?? 1

            const resolution = TILE_RESOLUTION * dpi * (GameConfig.config.resolutionScale / 100)
            canvas.width = dims.x * resolution
            canvas.height = dims.y * resolution
            canvas.getContext('2d')?.scale(resolution, resolution)
        }
    }

    private canvasMouseDown(e: MouseEvent) {
        this.mouseDown = true
        this.mouseDownStartPos = { x: e.x, y: e.y }
        if (e.button === 2) this.mouseDownRight = true
        this._trigger(this._canvasClickListeners)
    }

    private canvasMouseUp(e: MouseEvent) {
        this.mouseDown = false
        if (e.button === 2) this.mouseDownRight = false
        this._trigger(this._canvasClickListeners)
    }

    private canvasMouseMove(e: MouseEvent) {
        const newTile = eventToPoint(e)
        if (!newTile) return
        if (newTile.x !== this.mouseTile?.x || newTile.y !== this.mouseTile?.y) {
            this.mouseTile = newTile
            this.renderOverlay()
            this._trigger(this._canvasHoverListeners)
        }
    }

    private canvasMouseLeave(e: MouseEvent) {
        // Only trigger if the mouse actually left the canvas, not just lost focus
        const rect = this.canvases[0].getBoundingClientRect()
        if (e.x <= rect.right && e.x >= rect.left && e.y <= rect.bottom && e.y >= rect.top) {
            return
        }

        this.mouseDown = false
        this.mouseDownRight = false
        this.mouseTile = undefined
        this._trigger(this._canvasHoverListeners)
    }

    private canvasMouseEnter(e: MouseEvent) {
        const point = eventToPoint(e)
        if (!point) return
        this.mouseTile = point
        this.mouseDown = e.buttons > 0
        if (e.buttons === 2) this.mouseDownRight = true
        this._trigger(this._canvasHoverListeners)
    }

    private canvasClick(e: MouseEvent) {
        // Don't trigger the click if it moved too far away from the origin
        const maxDist = 25
        if (
            this.mouseDownStartPos &&
            (Math.abs(this.mouseDownStartPos.x - e.x) > maxDist || Math.abs(this.mouseDownStartPos.y - e.y) > maxDist)
        )
            return

        this.selectedTile = eventToPoint(e)

        if (!this.selectedTile) return

        const newSelectedBody = GameRunner.match?.currentRound.bodies.getBodyAtLocation(
            this.selectedTile.x,
            this.selectedTile.y
        )?.id

        this.setSelectedRobot(newSelectedBody)

        // Trigger anyways since clicking should always trigger
        this._trigger(this._canvasClickListeners)
    }

    private _trigger(listeners: (() => void)[]) {
        setTimeout(() => listeners.forEach((l) => l()))
    }

    useCanvasHoverEvents = () => {
        const [hoveredTile, setHoveredTile] = React.useState<Vector | undefined>(this.mouseTile)
        React.useEffect(() => {
            const listener = () => {
                setHoveredTile(this.mouseTile)
            }
            this._canvasHoverListeners.push(listener)
            return () => {
                this._canvasHoverListeners = this._canvasHoverListeners.filter((l) => l !== listener)
            }
        }, [])

        return { hoveredTile }
    }

    useCanvasClickEvents = () => {
        const [canvasMouseDown, setCanvasMouseDown] = React.useState<boolean>(this.mouseDown)
        const [canvasRightClick, setCanvasRightClick] = React.useState<boolean>(this.mouseDownRight)
        const [selectedTile, setSelectedTile] = React.useState<Vector | undefined>(this.selectedTile)
        const [selectedBodyID, setSelectedBodyID] = React.useState<number | undefined>(this.selectedBodyID)
        React.useEffect(() => {
            const listener = () => {
                setCanvasMouseDown(this.mouseDown)
                setCanvasRightClick(this.mouseDownRight)
                setSelectedTile(this.selectedTile)
                setSelectedBodyID(this.selectedBodyID)
            }
            this._canvasClickListeners.push(listener)
            return () => {
                this._canvasClickListeners = this._canvasClickListeners.filter((l) => l !== listener)
            }
        }, [])

        return { canvasMouseDown, canvasRightClick, selectedTile, selectedBodyID }
    }
}

const eventToPoint = (e: MouseEvent): Vector | undefined => {
    const canvas = e.target as HTMLCanvasElement
    const rect = canvas.getBoundingClientRect()
    const map = GameRunner.match?.map
    if (!map) return undefined
    let x = Math.floor(((e.clientX - rect.left) / rect.width) * map.width)
    let y = Math.floor((1 - (e.clientY - rect.top) / rect.height) * map.height)
    x = Math.max(0, Math.min(x, map.width - 1))
    y = Math.max(0, Math.min(y, map.height - 1))
    return { x, y }
}

export const GameRenderer = new GameRendererClass()
