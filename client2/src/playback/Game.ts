import Match from './Match'
import { flatbuffers, schema } from 'battlecode-schema'
import { ungzip } from 'pako'
import assert from 'assert'
import { SPEC_VERSION } from '../constants'

let nextID = 0

export default class Game {
    public readonly matches: Match[] = []
    public currentMatch: Match | undefined = undefined
    public readonly teams: [Team, Team]
    public readonly winner: Team

    //metadata
    private readonly specVersion: string
    private readonly constants: schema.Constants
    public readonly typeMetadata: schema.BodyTypeMetadata[] = []

    /**
     * Whether this game is playable (not currently being made in the map editor)
     */
    public readonly playable: boolean

    //shared slots for efficiency??
    public _bodiesSlot: schema.SpawnedBodyTable = new schema.SpawnedBodyTable()
    public _vecTableSlot1: schema.VecTable = new schema.VecTable()

    /**
     * The ID of this game. This is used to uniquely identify games in the UI, and is just based on uploaded order
     */
    public readonly id: number

    constructor(wrapper?: schema.GameWrapper) {
        this.playable = !!wrapper

        if (!wrapper) {
            //bare minimum setup for map editor
            this.teams = [
                new Team('Red', { wins: 0, elo: 0 }, 1, 'map_editor_red', 'red'),
                new Team('Blue', { wins: 0, elo: 0 }, 2, 'map_editor_blue', 'blue')
            ]
            this.winner = this.teams[0]
            this.specVersion = SPEC_VERSION
            this.constants = new schema.Constants()
            this.id = nextID++
            this.playable = false
            return
        }

        const eventCount = wrapper.eventsLength()
        if (eventCount < 5) throw new Error(`Too few events for well-formed game: ${eventCount}`)

        const eventSlot = new schema.EventWrapper() // not sure what this is for, probably better performance (this is how it was done in the old client)

        // load header and metadata =============================================================================
        const gameHeaderEvent = wrapper.events(0, eventSlot) ?? assert.fail('Event was null')
        assert(gameHeaderEvent.eType() === schema.Event.GameHeader, 'First event must be GameHeader')
        const gameHeader = gameHeaderEvent.e(new schema.GameHeader()) as schema.GameHeader
        this.specVersion = (gameHeader.specVersion() as string) || assert.fail('Unknown spec version')
        this.teams = [
            Team.fromSchema(gameHeader.teams(0) ?? assert.fail('Team 0 was null')),
            Team.fromSchema(gameHeader.teams(1) ?? assert.fail('Team 1 was null'))
        ]

        const bodyCount = gameHeader.bodyTypeMetadataLength()
        for (let i = 0; i < bodyCount; i++) {
            const bodyData = gameHeader.bodyTypeMetadata(i) ?? assert.fail('BodyTypeMetadata was null')
            this.typeMetadata[bodyData.type()] = bodyData
        }
        this.constants = gameHeader.constants() ?? assert.fail('Constants was null')

        // load matches ==========================================================================================
        for (let i = 1; i < eventCount - 1; i++) {
            const matchHeaderEvent = wrapper.events(i, eventSlot) ?? assert.fail('Event was null')
            assert(matchHeaderEvent.eType() === schema.Event.MatchHeader, 'Event must be MatchHeader')
            const matchHeader = matchHeaderEvent.e(new schema.MatchHeader()) as schema.MatchHeader

            i++
            let event
            let matches: schema.Round[] = []
            while (
                (event = wrapper.events(i, eventSlot) ?? assert.fail('Event was null')).eType() !==
                schema.Event.MatchFooter
            ) {
                assert(event.eType() === schema.Event.Round, 'Event must be Round')
                matches.push(event.e(new schema.Round()) as schema.Round)
                i++
            }

            assert(event.eType() === schema.Event.MatchFooter, 'Event must be MatchFooter')
            const matchFooter = event.e(new schema.MatchFooter()) as schema.MatchFooter

            this.matches.push(Match.fromSchema(this, matchHeader, matches, matchFooter))
        }

        if (!this.currentMatch && this.matches.length > 0) this.currentMatch = this.matches[0]

        // load footer ==========================================================================================
        const event = wrapper.events(eventCount - 1, eventSlot) ?? assert.fail('Event was null')
        assert(event.eType() === schema.Event.GameFooter, 'Last event must be GameFooter')
        const gameFooter = event.e(new schema.GameFooter()) as schema.GameFooter
        this.winner = this.teams[gameFooter.winner()]

        this.id = nextID++

        console.log(this)
    }

    public getTeamByID(id: number): Team {
        for (const team of this.teams) if (team.id === id) return team
        throw new Error('Team not found')
    }

    /**
     * Load a full game from a gzipped ArrayBuffer containing a GameWrapper.
     *
     * Do not mutate `data` after calling this function!
     */
    public static loadFullGameRaw(data: ArrayBuffer): Game {
        const ungzipped = ungzip(new Uint8Array(data))
        console.log('Game un-gzipped!')
        const wrapper = schema.GameWrapper.getRootAsGameWrapper(new flatbuffers.ByteBuffer(ungzipped))
        return new Game(wrapper)
    }
}

export class Team {
    constructor(
        public readonly name: string,
        public stats: TeamStat,
        public readonly id: number,
        public readonly packageName: string,
        public readonly color: string
    ) {}

    static fromSchema(team: schema.TeamData) {
        const name = team.name() ?? assert.fail('Team name is missing')
        const stats = {
            wins: 0,
            elo: 0
        }
        const id = team.teamId() ?? assert.fail('Team id is missing')
        const packageName = team.packageName() ?? assert.fail('Team package name is missing')
        const color = id === 1 ? 'red' : 'blue'
        return new Team(name, stats, id, packageName, color)
    }
}

export type TeamStat = {
    wins: number
    elo: number
}