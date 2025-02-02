// automatically generated by the FlatBuffers compiler, do not modify

/* eslint-disable @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any, @typescript-eslint/no-non-null-assertion */

import * as flatbuffers from 'flatbuffers';

/**
 * These tables are set-up so that they match closely with speedscope's file format documented at
 * https://github.com/jlfwong/speedscope/wiki/Importing-from-custom-sources.
 * The client uses speedscope to show the recorded data in an interactive interface.
 * A single event in a profile. Represents either an open event (meaning a
 * method has been entered) or a close event (meaning the method was exited).
 */
export class ProfilerEvent {
  bb: flatbuffers.ByteBuffer|null = null;
  bb_pos = 0;
  __init(i:number, bb:flatbuffers.ByteBuffer):ProfilerEvent {
  this.bb_pos = i;
  this.bb = bb;
  return this;
}

static getRootAsProfilerEvent(bb:flatbuffers.ByteBuffer, obj?:ProfilerEvent):ProfilerEvent {
  return (obj || new ProfilerEvent()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
}

static getSizePrefixedRootAsProfilerEvent(bb:flatbuffers.ByteBuffer, obj?:ProfilerEvent):ProfilerEvent {
  bb.setPosition(bb.position() + flatbuffers.SIZE_PREFIX_LENGTH);
  return (obj || new ProfilerEvent()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
}

/**
 * Whether this is an open event (true) or a close event (false).
 */
isOpen():boolean {
  const offset = this.bb!.__offset(this.bb_pos, 4);
  return offset ? !!this.bb!.readInt8(this.bb_pos + offset) : false;
}

/**
 * The bytecode counter at the time the event occurred.
 */
at():number {
  const offset = this.bb!.__offset(this.bb_pos, 6);
  return offset ? this.bb!.readInt32(this.bb_pos + offset) : 0;
}

/**
 * The index of the method name in the ProfilerFile.frames array.
 */
frame():number {
  const offset = this.bb!.__offset(this.bb_pos, 8);
  return offset ? this.bb!.readInt32(this.bb_pos + offset) : 0;
}

static startProfilerEvent(builder:flatbuffers.Builder) {
  builder.startObject(3);
}

static addIsOpen(builder:flatbuffers.Builder, isOpen:boolean) {
  builder.addFieldInt8(0, +isOpen, +false);
}

static addAt(builder:flatbuffers.Builder, at:number) {
  builder.addFieldInt32(1, at, 0);
}

static addFrame(builder:flatbuffers.Builder, frame:number) {
  builder.addFieldInt32(2, frame, 0);
}

static endProfilerEvent(builder:flatbuffers.Builder):flatbuffers.Offset {
  const offset = builder.endObject();
  return offset;
}

static createProfilerEvent(builder:flatbuffers.Builder, isOpen:boolean, at:number, frame:number):flatbuffers.Offset {
  ProfilerEvent.startProfilerEvent(builder);
  ProfilerEvent.addIsOpen(builder, isOpen);
  ProfilerEvent.addAt(builder, at);
  ProfilerEvent.addFrame(builder, frame);
  return ProfilerEvent.endProfilerEvent(builder);
}
}
