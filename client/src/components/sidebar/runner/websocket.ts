import { schema, flatbuffers } from 'battlecode-schema'
import Game from '../../../playback/Game'
import Match from '../../../playback/Match'
import assert from 'assert'
import { EventType, publishEvent } from '../../../app-events'

export type FakeGameWrapper = {
    events: (index: number, unusedEventSlot: any) => schema.EventWrapper | null
    eventsLength: () => number
}

export default class WebSocketListener {
    url: string = 'ws://localhost:6175'
    pollEvery: number = 500
    activeGame: Game | null = null
    stream: boolean = false
    lastSetTurn: number = 0
    constructor(
        private shouldStream: boolean,
        readonly onGameCreated: (game: Game) => void,
        readonly onMatchCreated: (match: Match) => void,
        readonly onGameComplete: (game: Game) => void
    ) {
        this.poll()
    }

    public setShouldStream(stream: boolean) {
        this.shouldStream = stream
    }

    private reset() {
        this.activeGame = null
        this.lastSetTurn = 0
    }

    private poll() {
        const ws = new WebSocket(this.url)
        ws.binaryType = 'arraybuffer'
        ws.onopen = (event) => {
            console.log(`Connected to ${this.url}`)
        }
        ws.onmessage = (event) => {
            this.handleEvent(<ArrayBuffer>event.data)
        }
        ws.onerror = (event) => {
            this.reset()
        }
        ws.onclose = (event) => {
            this.reset()
            window.setTimeout(() => {
                this.poll()
            }, this.pollEvery)
        }
    }

    private visualUpdate() {
        if (!this.activeGame) return

        const match = this.activeGame.matches[this.activeGame.matches.length - 1]
        if (match) {
            // Auto progress the turn if the user hasn't done it themselves
            if (match.currentTurn.turnNumber == this.lastSetTurn) {
                match.jumpToEnd(true)
                this.lastSetTurn = match.currentTurn.turnNumber
            } else {
                // Publish anyways so the control bar updates
                publishEvent(EventType.TURN_PROGRESS, {})
            }
        }

        window.requestAnimationFrame(() => this.visualUpdate())
    }

    private handleEvent(data: ArrayBuffer) {
        const event = schema.EventWrapper.getRootAsEventWrapper(new flatbuffers.ByteBuffer(new Uint8Array(data)))
        const eventType = event.eType()

        if (this.activeGame === null) {
            assert(eventType === schema.Event.GameHeader, 'First event must be GameHeader')

            const fakeGameWrapper: FakeGameWrapper = {
                events: () => event,
                eventsLength: () => 1
            }

            this.stream = this.shouldStream
            this.activeGame = new Game(fakeGameWrapper)

            if (this.stream) {
                this.onGameCreated(this.activeGame)
                window.requestAnimationFrame(() => this.visualUpdate())
            }

            return
        }

        this.activeGame.addEvent(event)

        switch (eventType) {
            case schema.Event.MatchHeader: {
                if (!this.stream) break

                const match = this.activeGame.matches[this.activeGame.matches.length - 1]
                this.onMatchCreated(match)

                break
            }
            case schema.Event.GameFooter: {
                publishEvent(EventType.TURN_PROGRESS, {})
                this.onGameComplete(this.activeGame!)
                this.reset()

                break
            }
            default:
                break
        }
    }
}
