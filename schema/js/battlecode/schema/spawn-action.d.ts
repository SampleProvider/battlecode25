import * as flatbuffers from 'flatbuffers';
import { RobotType } from '../../battlecode/schema/robot-type';
/**
 * Indicate that this robot was spawned on this turn
 */
export declare class SpawnAction {
    bb: flatbuffers.ByteBuffer | null;
    bb_pos: number;
    __init(i: number, bb: flatbuffers.ByteBuffer): SpawnAction;
    /**
     * Id of the bot that spawned
     */
    id(): number;
    x(): number;
    y(): number;
    team(): number;
    robotType(): RobotType;
    static sizeOf(): number;
    static createSpawnAction(builder: flatbuffers.Builder, id: number, x: number, y: number, team: number, robotType: RobotType): flatbuffers.Offset;
}
