/*
import React from 'react'
import { CanvasHistogram } from './quick-histogram'
import { SPECIALTY_COLORS, TEAM_COLORS } from '../../../constants'
import { currentColors } from '../../../colors'
import { useRound } from '../../../playback/GameRunner'
import Round from '../../../playback/Round'

function getChartData(round: Round): number[][][] {
    const emptyHist = Array(7).fill(0)
    const totals = [
        [[...emptyHist], [...emptyHist], [...emptyHist]],
        [[...emptyHist], [...emptyHist], [...emptyHist]]
    ]
    for (const [id, body] of round.bodies.bodies) {
        const teamIdx = body.team.id - 1
        totals[teamIdx][0][body.attackLevel] += 1
        totals[teamIdx][1][body.buildLevel] += 1
        totals[teamIdx][2][body.healLevel] += 1
    }

    return totals
}

interface SpecialtyHistogramProps {
    active: boolean
}

export const SpecialtyHistogram: React.FC<SpecialtyHistogramProps> = (props) => {
    const round = useRound()
    const data = props.active && round ? getChartData(round) : []

    const getData = (team: number, specialty: number) => {
        return data.length === 0 ? [] : data[team][specialty]
    }

    return (
        <div className="mt-2 px-2 w-full d-flex flex-column">
            <h2 className="mx-auto text-center">Specialty breakdown</h2>
            {[0, 1].map((team) => (
                <div className="flex flex-row" key={team}>
                    <div className="w-4 mr-2 mb-3" style={{ backgroundColor: currentColors[TEAM_COLORS[team]] }}></div>
                    {[0, 1, 2].map((specialty) => (
                        <CanvasHistogram
                            key={specialty}
                            data={getData(team, specialty)}
                            width={110}
                            height={100}
                            margin={{ top: 10, right: 10, bottom: 20, left: 20 }}
                            color={currentColors[SPECIALTY_COLORS[specialty]]}
                            resolution={1.5}
                        />
                    ))}
                </div>
            ))}
        </div>
    )
}
*/
