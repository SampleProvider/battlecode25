import React, { useEffect } from 'react'
import * as ControlIcons from '../../icons/controls'
import { ControlsBarButton } from './controls-bar-button'
import { useAppContext } from '../../app-context'
import { useKeyboard } from '../../util/keyboard'
import { ControlsBarTimeline } from './controls-bar-timeline'
import { MAX_SIMULATION_STEPS } from '../../playback/Match'
import { EventType, useListenEvent } from '../../app-events'
import { useForceUpdate } from '../../util/react-util'
import Tooltip from '../tooltip'

const SIMULATION_UPDATE_INTERVAL_MS = 17 // About 60 fps

export const ControlsBar: React.FC = () => {
    const { state: appState, setState: setAppState } = useAppContext()
    const [minimized, setMinimized] = React.useState(false)
    const keyboard = useKeyboard()

    const matchLoaded = appState.activeGame && appState.activeGame.currentMatch

    const changePaused = (paused: boolean) => {
        if (!matchLoaded) return
        setAppState({
            ...appState,
            paused: paused,
            updatesPerSecond: appState.updatesPerSecond == 0 && !paused ? 1 : appState.updatesPerSecond
        })
    }

    const multiplyUpdatesPerSecond = (multiplier: number) => {
        if (!matchLoaded) return
        setAppState((old) => {
            const u = old.updatesPerSecond
            const sign = Math.sign(u * multiplier)
            const newMag = Math.max(1 / 4, Math.min(64, Math.abs(u * multiplier)))
            return { ...old, updatesPerSecond: sign * newMag }
        })
    }

    const stepTurn = (delta: number) => {
        if (!matchLoaded) return
        appState.activeGame!.currentMatch!.stepTurn(delta)
        appState.activeGame!.currentMatch!.roundSimulation()
    }

    const jumpToTurn = (turn: number) => {
        if (!matchLoaded) return
        appState.activeGame!.currentMatch!.jumpToTurn(turn)
        appState.activeGame!.currentMatch!.roundSimulation()
    }

    const jumpToEnd = () => {
        if (!matchLoaded) return
        appState.activeGame!.currentMatch!.jumpToEnd()
        appState.activeGame!.currentMatch!.roundSimulation()
    }

    const nextMatch = () => {
        if (!matchLoaded) return
        const game = appState.activeGame!
        const prevMatch = game.currentMatch!
        const prevMatchIndex = game.matches.indexOf(prevMatch)
        if (prevMatchIndex + 1 == game.matches.length) {
            closeGame()
            return
        }

        game.currentMatch = game.matches[prevMatchIndex + 1]
        setAppState({
            ...appState,
            activeGame: game,
            activeMatch: game.currentMatch
        })
    }

    const hasNextMatch =
        appState.activeGame &&
        appState.activeGame!.currentMatch &&
        appState.activeGame!.matches.indexOf(appState.activeGame!.currentMatch!) + 1 <
            appState.activeGame!.matches.length

    const closeGame = () => {
        setAppState({
            ...appState,
            activeGame: undefined,
            activeMatch: undefined
        })
    }

    const currentUPSBuffer = React.useRef<number[]>([])

    React.useEffect(() => {
        if (!matchLoaded) return
        if (appState.paused) {
            // Snap bots to their actual position when paused by rounding simulation
            // to the true turn
            appState.activeGame!.currentMatch!.roundSimulation()
            appState.activeGame!.currentMatch!.rerender()
            return
        }

        const msPerUpdate = 1000 / appState.updatesPerSecond
        const updatesPerInterval = SIMULATION_UPDATE_INTERVAL_MS / msPerUpdate
        const simStepsPerInterval = updatesPerInterval * MAX_SIMULATION_STEPS
        const stepInterval = setInterval(() => {
            const prevTurn = appState.activeGame!.currentMatch!.currentTurn.turnNumber
            appState.activeGame!.currentMatch!.stepSimulation(simStepsPerInterval)

            if (prevTurn != appState.activeGame!.currentMatch!.currentTurn.turnNumber) {
                currentUPSBuffer.current.push(Date.now())
                while (currentUPSBuffer.current.length > 0 && currentUPSBuffer.current[0] < Date.now() - 1000)
                    currentUPSBuffer.current.shift()
            }
        }, SIMULATION_UPDATE_INTERVAL_MS)

        return () => {
            clearInterval(stepInterval)
        }
    }, [appState.updatesPerSecond, appState.activeGame, appState.activeGame?.currentMatch, appState.paused])

    useEffect(() => {
        // If the competitor had manually pressed one of the buttons on the
        // control bar before using a shortcut, unselect it; Most browsers have
        // specific accessibility features that mess with these shortcuts.
        if (keyboard.targetElem instanceof HTMLButtonElement) keyboard.targetElem.blur()

        if (keyboard.keyCode === 'Space') changePaused(!appState.paused)

        if (keyboard.keyCode === 'KeyC') setMinimized(!minimized)

        if (appState.paused) {
            // Paused
            if (keyboard.keyCode === 'ArrowRight') stepTurn(1)
            if (keyboard.keyCode === 'ArrowLeft') stepTurn(-1)
        } else {
            // Unpaused
            if (keyboard.keyCode === 'ArrowRight') multiplyUpdatesPerSecond(2)
            if (keyboard.keyCode === 'ArrowLeft') multiplyUpdatesPerSecond(0.5)
        }

        if (keyboard.keyCode === 'Comma') jumpToTurn(0)
        if (keyboard.keyCode === 'Period') jumpToEnd()
    }, [keyboard.keyCode])

    const forceUpdate = useForceUpdate()
    useListenEvent(EventType.TURN_PROGRESS, forceUpdate)

    if (!appState.activeGame || !appState.activeGame.playable) return null

    const match = appState.activeGame!.currentMatch!
    const atStart = match.currentTurn.turnNumber == 0
    const atEnd = match.currentTurn.turnNumber == match.maxTurn

    return (
        <div className="flex absolute bottom-0 rounded-t-md z-10">
            <Tooltip text={minimized ? 'Open Controls (c)' : 'Close Controls (c)'}>
                <button
                    className={
                        (minimized ? 'text-darkHighlight opacity-90' : 'ml-[1px] text-white') +
                        ' z-20 absolute left-0 top-0 rounded-md text-[10px] aspect-[1] w-[15px] flex justify-center font-bold'
                    }
                    onClick={() => setMinimized(!minimized)}
                >
                    {minimized ? '+' : '-'}
                </button>
            </Tooltip>
            <div
                className={
                    (minimized ? 'opacity-10 pointer-events-none' : 'opacity-90') +
                    ' flex bg-darkHighlight text-white p-1.5 rounded-t-md z-10 gap-1.5 relative'
                }
            >
                <ControlsBarTimeline currentUPS={currentUPSBuffer.current.length} />
                <ControlsBarButton
                    icon={<ControlIcons.ReverseIcon />}
                    tooltip="Reverse"
                    onClick={() => multiplyUpdatesPerSecond(-1)}
                />
                <ControlsBarButton
                    icon={<ControlIcons.SkipBackwardsIcon />}
                    tooltip={'Decrease Speed'}
                    onClick={() => multiplyUpdatesPerSecond(0.5)}
                    disabled={Math.abs(appState.updatesPerSecond) <= 0.25}
                />
                <ControlsBarButton
                    icon={<ControlIcons.GoPreviousIcon />}
                    tooltip="Step Backwards"
                    onClick={() => stepTurn(-1)}
                    disabled={atStart}
                />
                {appState.paused ? (
                    <ControlsBarButton
                        icon={<ControlIcons.PlaybackPlayIcon />}
                        tooltip="Play"
                        onClick={() => {
                            changePaused(false)
                        }}
                    />
                ) : (
                    <ControlsBarButton
                        icon={<ControlIcons.PlaybackPauseIcon />}
                        tooltip="Pause"
                        onClick={() => {
                            changePaused(true)
                        }}
                    />
                )}
                <ControlsBarButton
                    icon={<ControlIcons.GoNextIcon />}
                    tooltip="Next Turn"
                    onClick={() => stepTurn(1)}
                    disabled={atEnd}
                />
                <ControlsBarButton
                    icon={<ControlIcons.SkipForwardsIcon />}
                    tooltip={'Increase Speed'}
                    onClick={() => multiplyUpdatesPerSecond(2)}
                    disabled={Math.abs(appState.updatesPerSecond) >= 64}
                />
                <ControlsBarButton
                    icon={<ControlIcons.PlaybackStopIcon />}
                    tooltip="Jump To Start"
                    onClick={() => jumpToTurn(0)}
                    disabled={atStart}
                />
                <ControlsBarButton
                    icon={<ControlIcons.GoEndIcon />}
                    tooltip="Jump To End"
                    onClick={jumpToEnd}
                    disabled={atEnd}
                />
                {appState.tournament && (
                    <>
                        <ControlsBarButton
                            icon={<ControlIcons.NextRound />}
                            tooltip="Next Round"
                            onClick={nextMatch}
                            disabled={!hasNextMatch}
                        />
                        <ControlsBarButton icon={<ControlIcons.CloseGame />} tooltip="Close Game" onClick={closeGame} />
                    </>
                )}
            </div>
        </div>
    )
}
