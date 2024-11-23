import React from 'react'
import Game from './Game'
import Match from './Match'
import Round from './Round'
import { GameRenderer } from './GameRenderer'

const SIMULATION_UPDATE_INTERVAL_MS = 17 // About 60 fps

class gameRunnerClass {
    targetUPS: number = 1
    currentUPSBuffer: number[] = []
    paused: boolean = true

    game: Game | undefined = undefined
    get match(): Match | undefined {
        return this.game?.currentMatch
    }

    _controlListeners: (() => void)[] = []
    _gameListeners: (() => void)[] = []
    _matchListeners: (() => void)[] = []
    _roundListeners: (() => void)[] = []

    eventLoop: NodeJS.Timeout | undefined = undefined

    private startEventLoop(): void {
        if (this.eventLoop) return

        this.eventLoop = setInterval(() => {
            if (!this.match || this.paused) {
                this.stopEventLoop()
                return
            }

            const prevRound = this.match!.currentRound.roundNumber

            const msPerUpdate = 1000 / this.targetUPS
            const updatesPerInterval = SIMULATION_UPDATE_INTERVAL_MS / msPerUpdate

            const roundChanged = this.match!._stepSimulation(updatesPerInterval)

            // Always rerender, so this assumes the simulation pauses when the simulation
            // is over
            GameRenderer.render()

            if (roundChanged) {
                this._trigger(this._roundListeners)
            }

            if (prevRound != this.match.currentRound.roundNumber) {
                this.currentUPSBuffer.push(Date.now())
                while (this.currentUPSBuffer.length > 0 && this.currentUPSBuffer[0] < Date.now() - 1000)
                    this.currentUPSBuffer.shift()
            }

            if (this.match.currentRound.isEnd() && this.targetUPS > 0) {
                this.setPaused(true)
            } else if (this.match.currentRound.isStart() && this.targetUPS < 0) {
                this.setPaused(true)
            }
        }, SIMULATION_UPDATE_INTERVAL_MS)
    }

    private stopEventLoop(): void {
        if (!this.eventLoop) return

        // Snap bots to their actual position when paused by rounding simulation to the true round
        if (this.match) {
            this.match._roundSimulation()
            GameRenderer.render()
        }
        clearInterval(this.eventLoop)
        this.eventLoop = undefined
    }

    private updateEventLoop(): void {
        if (!this.match || this.paused) {
            this.stopEventLoop()
        } else {
            this.startEventLoop()
        }
    }

    setGame(game: Game | undefined): void {
        if (this.game == game) return
        this.game = game
        this._trigger(this._gameListeners)
    }

    _trigger(listeners: (() => void)[]): void {
        setTimeout(() => {
            listeners.forEach((l) => l())
        })
    }

    setMatch(match: Match | undefined): void {
        this._trigger(this._matchListeners)
        if (this.match == match) return
        if (match) {
            match.game.currentMatch = match
            this.setGame(match.game)
            match._jumpToRound(0)
            match._roundSimulation()
            GameRenderer.render()
        }
        this.setPaused(true)
        GameRenderer.onMatchChange()
    }

    multiplyUpdatesPerSecond(multiplier: number) {
        if (!this.match) return
        const scaled = this.targetUPS * multiplier
        const newMag = Math.max(1 / 4, Math.min(64, Math.abs(scaled)))
        this.targetUPS = Math.sign(scaled) * newMag
        this._trigger(this._controlListeners)
    }

    setPaused(paused: boolean): void {
        if (!this.match) return
        this.paused = paused
        if (!paused && this.targetUPS == 0) this.targetUPS = 1
        this.updateEventLoop()
        this._trigger(this._controlListeners)
    }

    stepRound(delta: number) {
        if (!this.match) return
        // explicit rerender at the end so a render doesnt occur between these two steps
        this.match._stepRound(delta)
        this.match._roundSimulation()
        GameRenderer.render()
        this._trigger(this._roundListeners)
    }

    jumpToRound(round: number) {
        if (!this.match) return
        // explicit rerender at the end so a render doesnt occur between these two steps
        this.match._jumpToRound(round)
        this.match._roundSimulation()
        GameRenderer.render()
        this._trigger(this._roundListeners)
    }

    jumpToEnd() {
        if (!this.match) return
        // explicit rerender at the end so a render doesnt occur between these two steps
        this.match._jumpToEnd()
        this.match._roundSimulation()
        GameRenderer.render()
        this._trigger(this._roundListeners)
    }

    nextMatch() {
        if (!this.match || !this.game) return
        const prevMatchIndex = this.game.matches.indexOf(this.match)
        if (prevMatchIndex + 1 == this.game.matches.length) {
            this.setGame(undefined)
        } else {
            this.setMatch(this.game.matches[prevMatchIndex + 1])
        }
    }
}

const gameRunner = new gameRunnerClass()

export function useGame(): Game | undefined {
    const [game, setGame] = React.useState(gameRunner.game)
    React.useEffect(() => {
        const listener = () => setGame(gameRunner.game)
        gameRunner._gameListeners.push(listener)
        return () => {
            gameRunner._gameListeners = gameRunner._gameListeners.filter((l) => l !== listener)
        }
    }, [])
    return game
}

export function useMatch(): Match | undefined {
    const game = useGame()
    const [match, setMatch] = React.useState(game?.currentMatch)
    // Update on match properties as well
    const [maxRound, setMaxRound] = React.useState(game?.currentMatch?.maxRound)
    const [winner, setWinner] = React.useState(game?.currentMatch?.winner)
    React.useEffect(() => {
        const listener = () => {
            setMatch(game?.currentMatch)
            setMaxRound(game?.currentMatch?.maxRound)
            setWinner(game?.currentMatch?.winner)
        }
        gameRunner._matchListeners.push(listener)
        return () => {
            gameRunner._matchListeners = gameRunner._matchListeners.filter((l) => l !== listener)
        }
    }, [game])
    return game?.currentMatch
}

export function useRound(): Round | undefined {
    const match = useMatch()
    const [round, setRound] = React.useState(match?.currentRound)
    // Update on round properties as well
    const [roundNumber, setRoundNumber] = React.useState(match?.currentRound?.roundNumber)
    React.useEffect(() => {
        const listener = () => {
            setRound(match?.currentRound)
            setRoundNumber(match?.currentRound?.roundNumber)
        }
        gameRunner._roundListeners.push(listener)
        return () => {
            gameRunner._roundListeners = gameRunner._roundListeners.filter((l) => l !== listener)
        }
    }, [match])
    return match?.currentRound
}

export function useControls(): {
    targetUPS: number
    paused: boolean
} {
    const [targetUPS, setTargetUPS] = React.useState(gameRunner.targetUPS)
    const [paused, setPaused] = React.useState(gameRunner.paused)
    React.useEffect(() => {
        const listener = () => {
            setTargetUPS(gameRunner.targetUPS)
            setPaused(gameRunner.paused)
        }
        gameRunner._controlListeners.push(listener)
        return () => {
            gameRunner._controlListeners = gameRunner._controlListeners.filter((l) => l !== listener)
        }
    }, [])
    return { targetUPS, paused }
}

export function useCurrentUPS(): number {
    const [currentUPS, setCurrentUPS] = React.useState(gameRunner.currentUPSBuffer.length)
    React.useEffect(() => {
        const listener = () => setCurrentUPS(gameRunner.currentUPSBuffer.length)
        gameRunner._controlListeners.push(listener)
        gameRunner._roundListeners.push(listener)
        return () => {
            gameRunner._controlListeners = gameRunner._controlListeners.filter((l) => l !== listener)
            gameRunner._roundListeners = gameRunner._roundListeners.filter((l) => l !== listener)
        }
    }, [])
    return currentUPS
}

export default gameRunner