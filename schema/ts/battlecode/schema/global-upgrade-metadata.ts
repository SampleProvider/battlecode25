// automatically generated by the FlatBuffers compiler, do not modify

import * as flatbuffers from 'flatbuffers';

import { GlobalUpgradeType } from '../../battlecode/schema/global-upgrade-type';


export class GlobalUpgradeMetadata {
  bb: flatbuffers.ByteBuffer|null = null;
  bb_pos = 0;
  __init(i:number, bb:flatbuffers.ByteBuffer):GlobalUpgradeMetadata {
  this.bb_pos = i;
  this.bb = bb;
  return this;
}

static getRootAsGlobalUpgradeMetadata(bb:flatbuffers.ByteBuffer, obj?:GlobalUpgradeMetadata):GlobalUpgradeMetadata {
  return (obj || new GlobalUpgradeMetadata()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
}

static getSizePrefixedRootAsGlobalUpgradeMetadata(bb:flatbuffers.ByteBuffer, obj?:GlobalUpgradeMetadata):GlobalUpgradeMetadata {
  bb.setPosition(bb.position() + flatbuffers.SIZE_PREFIX_LENGTH);
  return (obj || new GlobalUpgradeMetadata()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
}

type():GlobalUpgradeType {
  const offset = this.bb!.__offset(this.bb_pos, 4);
  return offset ? this.bb!.readInt8(this.bb_pos + offset) : GlobalUpgradeType.ACTION_UPGRADE;
}

upgradeAmount():number {
  const offset = this.bb!.__offset(this.bb_pos, 6);
  return offset ? this.bb!.readInt32(this.bb_pos + offset) : 0;
}

static startGlobalUpgradeMetadata(builder:flatbuffers.Builder) {
  builder.startObject(2);
}

static addType(builder:flatbuffers.Builder, type:GlobalUpgradeType) {
  builder.addFieldInt8(0, type, GlobalUpgradeType.ACTION_UPGRADE);
}

static addUpgradeAmount(builder:flatbuffers.Builder, upgradeAmount:number) {
  builder.addFieldInt32(1, upgradeAmount, 0);
}

static endGlobalUpgradeMetadata(builder:flatbuffers.Builder):flatbuffers.Offset {
  const offset = builder.endObject();
  return offset;
}

static createGlobalUpgradeMetadata(builder:flatbuffers.Builder, type:GlobalUpgradeType, upgradeAmount:number):flatbuffers.Offset {
  GlobalUpgradeMetadata.startGlobalUpgradeMetadata(builder);
  GlobalUpgradeMetadata.addType(builder, type);
  GlobalUpgradeMetadata.addUpgradeAmount(builder, upgradeAmount);
  return GlobalUpgradeMetadata.endGlobalUpgradeMetadata(builder);
}
}