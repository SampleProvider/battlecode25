// automatically generated by the FlatBuffers compiler, do not modify

/* eslint-disable @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any, @typescript-eslint/no-non-null-assertion */

import * as flatbuffers from 'flatbuffers';

import { ProfilerEvent } from '../../battlecode/schema/profiler-event';


/**
 * A profile contains all events and is labeled with a name.
 */
export class ProfilerProfile {
  bb: flatbuffers.ByteBuffer|null = null;
  bb_pos = 0;
  __init(i:number, bb:flatbuffers.ByteBuffer):ProfilerProfile {
  this.bb_pos = i;
  this.bb = bb;
  return this;
}

static getRootAsProfilerProfile(bb:flatbuffers.ByteBuffer, obj?:ProfilerProfile):ProfilerProfile {
  return (obj || new ProfilerProfile()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
}

static getSizePrefixedRootAsProfilerProfile(bb:flatbuffers.ByteBuffer, obj?:ProfilerProfile):ProfilerProfile {
  bb.setPosition(bb.position() + flatbuffers.SIZE_PREFIX_LENGTH);
  return (obj || new ProfilerProfile()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
}

/**
 * The display-friendly name of the profile.
 */
name():string|null
name(optionalEncoding:flatbuffers.Encoding):string|Uint8Array|null
name(optionalEncoding?:any):string|Uint8Array|null {
  const offset = this.bb!.__offset(this.bb_pos, 4);
  return offset ? this.bb!.__string(this.bb_pos + offset, optionalEncoding) : null;
}

/**
 * The events that occurred in the profile.
 */
events(index: number, obj?:ProfilerEvent):ProfilerEvent|null {
  const offset = this.bb!.__offset(this.bb_pos, 6);
  return offset ? (obj || new ProfilerEvent()).__init(this.bb!.__indirect(this.bb!.__vector(this.bb_pos + offset) + index * 4), this.bb!) : null;
}

eventsLength():number {
  const offset = this.bb!.__offset(this.bb_pos, 6);
  return offset ? this.bb!.__vector_len(this.bb_pos + offset) : 0;
}

static startProfilerProfile(builder:flatbuffers.Builder) {
  builder.startObject(2);
}

static addName(builder:flatbuffers.Builder, nameOffset:flatbuffers.Offset) {
  builder.addFieldOffset(0, nameOffset, 0);
}

static addEvents(builder:flatbuffers.Builder, eventsOffset:flatbuffers.Offset) {
  builder.addFieldOffset(1, eventsOffset, 0);
}

static createEventsVector(builder:flatbuffers.Builder, data:flatbuffers.Offset[]):flatbuffers.Offset {
  builder.startVector(4, data.length, 4);
  for (let i = data.length - 1; i >= 0; i--) {
    builder.addOffset(data[i]!);
  }
  return builder.endVector();
}

static startEventsVector(builder:flatbuffers.Builder, numElems:number) {
  builder.startVector(4, numElems, 4);
}

static endProfilerProfile(builder:flatbuffers.Builder):flatbuffers.Offset {
  const offset = builder.endObject();
  return offset;
}

static createProfilerProfile(builder:flatbuffers.Builder, nameOffset:flatbuffers.Offset, eventsOffset:flatbuffers.Offset):flatbuffers.Offset {
  ProfilerProfile.startProfilerProfile(builder);
  ProfilerProfile.addName(builder, nameOffset);
  ProfilerProfile.addEvents(builder, eventsOffset);
  return ProfilerProfile.endProfilerProfile(builder);
}
}
