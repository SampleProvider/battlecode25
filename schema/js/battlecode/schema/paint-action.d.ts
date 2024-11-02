import * as flatbuffers from 'flatbuffers';
export declare class PaintAction {
    bb: flatbuffers.ByteBuffer | null;
    bb_pos: number;
    __init(i: number, bb: flatbuffers.ByteBuffer): PaintAction;
    loc(): number;
    static sizeOf(): number;
    static createPaintAction(builder: flatbuffers.Builder, loc: number): flatbuffers.Offset;
}