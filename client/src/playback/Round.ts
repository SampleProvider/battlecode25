import { schema } from 'battlecode-schema'
import Actions from './Actions'
import Bodies from './Bodies'
import { Team } from './Game'
import { CurrentMap } from './Map'
import Match from './Match'
import RoundStat from './RoundStat'

export default class Round {
    get teams(): Team[] {
        return this.match.game.teams
    }
    constructor(
        public readonly match: Match,
        public roundNumber: number = 0,
        public map: CurrentMap,
        public bodies: Bodies,
        public actions: Actions,
        public stat: RoundStat
    ) {}

    /**
     * Mutates this round to reflect the given delta.
     */
    public applyDelta(delta: schema.Round, nextDelta: schema.Round | null): void {
        this.roundNumber += 1

        const firstTimeComputingStat = this.match.stats.length <= this.roundNumber
        if (firstTimeComputingStat)
            this.stat.completed = false // mark that stat should be computed by bodies and actions below
        else this.stat = this.match.stats[this.roundNumber].copy()

        /*
            The ordering here is important. Actions needs to be before map because it reads from the map's traps and 
            they would be removed if map was before it. Bodies needs to come before maps so that actions have access
            to spawned bodies
        */
        this.actions.applyDelta(this, delta)
        this.bodies.applyDelta(this, delta, nextDelta)
        this.map.applyDelta(delta)

        if (firstTimeComputingStat) {
            // finish computing stat and save to match
            this.stat.applyDelta(this, delta)
            this.match.stats[this.roundNumber] = this.stat.copy()
        }
    }

    public copy(): Round {
        return new Round(
            this.match,
            this.roundNumber,
            this.map.copy(),
            this.bodies.copy(),
            this.actions.copy(),
            this.stat.copy()
        )
    }

    public isStart() {
        return this.roundNumber === 0
    }

    public isEnd() {
        return this.roundNumber === this.match.maxRound
    }
}