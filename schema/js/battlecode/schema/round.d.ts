import * as flatbuffers from 'flatbuffers';
import { Turn } from '../../battlecode/schema/turn';
/**
 * A single time-step in a Game, which contains a list of robot turns
 */
export declare class Round {
    bb: flatbuffers.ByteBuffer | null;
    bb_pos: number;
    __init(i: number, bb: flatbuffers.ByteBuffer): Round;
    static getRootAsRound(bb: flatbuffers.ByteBuffer, obj?: Round): Round;
    static getSizePrefixedRootAsRound(bb: flatbuffers.ByteBuffer, obj?: Round): Round;
    /**
     * The IDs of teams in the Game.
     */
    teamIds(index: number): number | null;
    teamIdsLength(): number;
    teamIdsArray(): Int32Array | null;
    /**
     * The total amount of resource this round per team
     */
    teamResourceAmounts(index: number): number | null;
    teamResourceAmountsLength(): number;
    teamResourceAmountsArray(): Int32Array | null;
    /**
     * The total paint coverage percent per team, mult by 10 (i.e. 70.5% is 705)
     */
    teamCoverageAmounts(index: number): number | null;
    teamCoverageAmountsLength(): number;
    teamCoverageAmountsArray(): Int32Array | null;
    /**
     * The total number of active resource patterns per team
     */
    teamResourcePatternAmounts(index: number): number | null;
    teamResourcePatternAmountsLength(): number;
    teamResourcePatternAmountsArray(): Int32Array | null;
    /**
     * Ordered turn data for each robot during the round
     */
    turns(index: number, obj?: Turn): Turn | null;
    turnsLength(): number;
    /**
     * The IDs of bodies that died at the end of the round, with no attributable cause.
     */
    diedIds(index: number): number | null;
    diedIdsLength(): number;
    diedIdsArray(): Int32Array | null;
    /**
     * The first sent Round in a match should have index 1. (The starting state,
     * created by the MatchHeader, can be thought to have index 0.)
     * It should increase by one for each following round.
     */
    roundId(): number;
    static startRound(builder: flatbuffers.Builder): void;
    static addTeamIds(builder: flatbuffers.Builder, teamIdsOffset: flatbuffers.Offset): void;
    static createTeamIdsVector(builder: flatbuffers.Builder, data: number[] | Int32Array): flatbuffers.Offset;
    /**
     * @deprecated This Uint8Array overload will be removed in the future.
     */
    static createTeamIdsVector(builder: flatbuffers.Builder, data: number[] | Uint8Array): flatbuffers.Offset;
    static startTeamIdsVector(builder: flatbuffers.Builder, numElems: number): void;
    static addTeamResourceAmounts(builder: flatbuffers.Builder, teamResourceAmountsOffset: flatbuffers.Offset): void;
    static createTeamResourceAmountsVector(builder: flatbuffers.Builder, data: number[] | Int32Array): flatbuffers.Offset;
    /**
     * @deprecated This Uint8Array overload will be removed in the future.
     */
    static createTeamResourceAmountsVector(builder: flatbuffers.Builder, data: number[] | Uint8Array): flatbuffers.Offset;
    static startTeamResourceAmountsVector(builder: flatbuffers.Builder, numElems: number): void;
    static addTeamCoverageAmounts(builder: flatbuffers.Builder, teamCoverageAmountsOffset: flatbuffers.Offset): void;
    static createTeamCoverageAmountsVector(builder: flatbuffers.Builder, data: number[] | Int32Array): flatbuffers.Offset;
    /**
     * @deprecated This Uint8Array overload will be removed in the future.
     */
    static createTeamCoverageAmountsVector(builder: flatbuffers.Builder, data: number[] | Uint8Array): flatbuffers.Offset;
    static startTeamCoverageAmountsVector(builder: flatbuffers.Builder, numElems: number): void;
    static addTeamResourcePatternAmounts(builder: flatbuffers.Builder, teamResourcePatternAmountsOffset: flatbuffers.Offset): void;
    static createTeamResourcePatternAmountsVector(builder: flatbuffers.Builder, data: number[] | Int32Array): flatbuffers.Offset;
    /**
     * @deprecated This Uint8Array overload will be removed in the future.
     */
    static createTeamResourcePatternAmountsVector(builder: flatbuffers.Builder, data: number[] | Uint8Array): flatbuffers.Offset;
    static startTeamResourcePatternAmountsVector(builder: flatbuffers.Builder, numElems: number): void;
    static addTurns(builder: flatbuffers.Builder, turnsOffset: flatbuffers.Offset): void;
    static createTurnsVector(builder: flatbuffers.Builder, data: flatbuffers.Offset[]): flatbuffers.Offset;
    static startTurnsVector(builder: flatbuffers.Builder, numElems: number): void;
    static addDiedIds(builder: flatbuffers.Builder, diedIdsOffset: flatbuffers.Offset): void;
    static createDiedIdsVector(builder: flatbuffers.Builder, data: number[] | Int32Array): flatbuffers.Offset;
    /**
     * @deprecated This Uint8Array overload will be removed in the future.
     */
    static createDiedIdsVector(builder: flatbuffers.Builder, data: number[] | Uint8Array): flatbuffers.Offset;
    static startDiedIdsVector(builder: flatbuffers.Builder, numElems: number): void;
    static addRoundId(builder: flatbuffers.Builder, roundId: number): void;
    static endRound(builder: flatbuffers.Builder): flatbuffers.Offset;
    static createRound(builder: flatbuffers.Builder, teamIdsOffset: flatbuffers.Offset, teamResourceAmountsOffset: flatbuffers.Offset, teamCoverageAmountsOffset: flatbuffers.Offset, teamResourcePatternAmountsOffset: flatbuffers.Offset, turnsOffset: flatbuffers.Offset, diedIdsOffset: flatbuffers.Offset, roundId: number): flatbuffers.Offset;
}
