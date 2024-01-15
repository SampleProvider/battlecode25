import React from 'react'
import { TeamTable } from './team-table'
import { ResourceGraph } from './resource-graph'
import { SpecialtyHistogram } from './histogram'
import { useSearchParamBool } from '../../../app-search-params'
import { useAppContext } from '../../../app-context'
import { SectionHeader } from '../../section-header'
import { Crown } from '../../../icons/crown'
import { EventType, useListenEvent } from '../../../app-events'
import Tooltip from '../../tooltip'
import { useForceUpdate } from '../../../util/react-util'

const NO_GAME_TEAM_NAME = '?????'

interface Props {
    open: boolean
}

const CrownElement = () => {
    return (
        <Tooltip text={'Majority match winner'} location="bottom">
            <Crown className="ml-2 mt-1" />
        </Tooltip>
    )
}

export const GamePage: React.FC<Props> = React.memo((props) => {
    const context = useAppContext()
    const activeGame = context.state.activeGame

    const [showStats, setShowStats] = useSearchParamBool('showStats', true)

    const forceUpdate = useForceUpdate()
    useListenEvent(EventType.TURN_PROGRESS, forceUpdate)

    const showWinner =
        !context.state.tournament || (context.state.activeMatch && context.state.activeMatch.currentTurn.isEnd())

    if (!props.open) return null

    const teamBoxClasses = 'w-full py-2 flex items-center text-center justify-center'
    return (
        <div className="flex flex-col overflow-x-hidden">
            <div className={teamBoxClasses + ' bg-team0'}>
                <div className="flex">
                    {activeGame?.teams[0].name ?? NO_GAME_TEAM_NAME}
                    {activeGame && activeGame.winner === activeGame.teams[0] && showWinner && <CrownElement />}
                </div>
            </div>
            <TeamTable teamIdx={0} />

            <div className="h-[15px]" />

            <div className={teamBoxClasses + ' bg-team1'}>
                <div className="flex">
                    {activeGame?.teams[1].name ?? NO_GAME_TEAM_NAME}
                    {activeGame && activeGame.winner === activeGame.teams[1] && showWinner && <CrownElement />}
                </div>
            </div>
            <TeamTable teamIdx={1} />

            <SectionHeader
                title="Stats"
                open={showStats}
                onClick={() => setShowStats(!showStats)}
                containerClassName="mt-2"
                titleClassName="py-2"
            >
                {activeGame ? (
                    <>
                        {/* Note: to keep animation smooth, we should still keep the elements rendered, but we pass showStats into
                            them so that they don't render any data (since we're likely hiding stats to prevent lag) */}
                        <SpecialtyHistogram active={showStats} />
                        <br />
                        <ResourceGraph active={showStats} property="resourceAmount" propertyDisplayName="Crumbs" />
                    </>
                ) : (
                    <div>Select a game to see stats</div>
                )}
            </SectionHeader>
        </div>
    )
})
