// automatically generated by the FlatBuffers compiler, do not modify

import * as flatbuffers from 'flatbuffers';

export class TransferAction {
  bb: flatbuffers.ByteBuffer|null = null;
  bb_pos = 0;
  __init(i:number, bb:flatbuffers.ByteBuffer):TransferAction {
  this.bb_pos = i;
  this.bb = bb;
  return this;
}

id():number {
  return this.bb!.readUint16(this.bb_pos);
}

static sizeOf():number {
  return 2;
}

static createTransferAction(builder:flatbuffers.Builder, id: number):flatbuffers.Offset {
  builder.prep(2, 2);
  builder.writeInt16(id);
  return builder.offset();
}

}
