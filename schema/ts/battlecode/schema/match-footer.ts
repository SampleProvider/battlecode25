// automatically generated by the FlatBuffers compiler, do not modify

/* eslint-disable @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any, @typescript-eslint/no-non-null-assertion */

import * as flatbuffers from 'flatbuffers';

import { ProfilerFile } from '../../battlecode/schema/profiler-file';
import { TimelineMarker } from '../../battlecode/schema/timeline-marker';
import { WinType } from '../../battlecode/schema/win-type';


/**
 * Sent to end a match.
 */
export class MatchFooter {
  bb: flatbuffers.ByteBuffer|null = null;
  bb_pos = 0;
  __init(i:number, bb:flatbuffers.ByteBuffer):MatchFooter {
  this.bb_pos = i;
  this.bb = bb;
  return this;
}

static getRootAsMatchFooter(bb:flatbuffers.ByteBuffer, obj?:MatchFooter):MatchFooter {
  return (obj || new MatchFooter()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
}

static getSizePrefixedRootAsMatchFooter(bb:flatbuffers.ByteBuffer, obj?:MatchFooter):MatchFooter {
  bb.setPosition(bb.position() + flatbuffers.SIZE_PREFIX_LENGTH);
  return (obj || new MatchFooter()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
}

/**
 * The ID of the winning team.
 */
winner():number {
  const offset = this.bb!.__offset(this.bb_pos, 4);
  return offset ? this.bb!.readInt8(this.bb_pos + offset) : 0;
}

/**
 * The reason for winning
 */
winType():WinType {
  const offset = this.bb!.__offset(this.bb_pos, 6);
  return offset ? this.bb!.readInt8(this.bb_pos + offset) : WinType.RESIGNATION;
}

/**
 * The number of rounds played.
 */
totalRounds():number {
  const offset = this.bb!.__offset(this.bb_pos, 8);
  return offset ? this.bb!.readInt32(this.bb_pos + offset) : 0;
}

/**
 * Markers for this match.
 */
timelineMarkers(index: number, obj?:TimelineMarker):TimelineMarker|null {
  const offset = this.bb!.__offset(this.bb_pos, 10);
  return offset ? (obj || new TimelineMarker()).__init(this.bb!.__indirect(this.bb!.__vector(this.bb_pos + offset) + index * 4), this.bb!) : null;
}

timelineMarkersLength():number {
  const offset = this.bb!.__offset(this.bb_pos, 10);
  return offset ? this.bb!.__vector_len(this.bb_pos + offset) : 0;
}

/**
 * Profiler data for team A and B if profiling is enabled.
 */
profilerFiles(index: number, obj?:ProfilerFile):ProfilerFile|null {
  const offset = this.bb!.__offset(this.bb_pos, 12);
  return offset ? (obj || new ProfilerFile()).__init(this.bb!.__indirect(this.bb!.__vector(this.bb_pos + offset) + index * 4), this.bb!) : null;
}

profilerFilesLength():number {
  const offset = this.bb!.__offset(this.bb_pos, 12);
  return offset ? this.bb!.__vector_len(this.bb_pos + offset) : 0;
}

static startMatchFooter(builder:flatbuffers.Builder) {
  builder.startObject(5);
}

static addWinner(builder:flatbuffers.Builder, winner:number) {
  builder.addFieldInt8(0, winner, 0);
}

static addWinType(builder:flatbuffers.Builder, winType:WinType) {
  builder.addFieldInt8(1, winType, WinType.RESIGNATION);
}

static addTotalRounds(builder:flatbuffers.Builder, totalRounds:number) {
  builder.addFieldInt32(2, totalRounds, 0);
}

static addTimelineMarkers(builder:flatbuffers.Builder, timelineMarkersOffset:flatbuffers.Offset) {
  builder.addFieldOffset(3, timelineMarkersOffset, 0);
}

static createTimelineMarkersVector(builder:flatbuffers.Builder, data:flatbuffers.Offset[]):flatbuffers.Offset {
  builder.startVector(4, data.length, 4);
  for (let i = data.length - 1; i >= 0; i--) {
    builder.addOffset(data[i]!);
  }
  return builder.endVector();
}

static startTimelineMarkersVector(builder:flatbuffers.Builder, numElems:number) {
  builder.startVector(4, numElems, 4);
}

static addProfilerFiles(builder:flatbuffers.Builder, profilerFilesOffset:flatbuffers.Offset) {
  builder.addFieldOffset(4, profilerFilesOffset, 0);
}

static createProfilerFilesVector(builder:flatbuffers.Builder, data:flatbuffers.Offset[]):flatbuffers.Offset {
  builder.startVector(4, data.length, 4);
  for (let i = data.length - 1; i >= 0; i--) {
    builder.addOffset(data[i]!);
  }
  return builder.endVector();
}

static startProfilerFilesVector(builder:flatbuffers.Builder, numElems:number) {
  builder.startVector(4, numElems, 4);
}

static endMatchFooter(builder:flatbuffers.Builder):flatbuffers.Offset {
  const offset = builder.endObject();
  return offset;
}

static createMatchFooter(builder:flatbuffers.Builder, winner:number, winType:WinType, totalRounds:number, timelineMarkersOffset:flatbuffers.Offset, profilerFilesOffset:flatbuffers.Offset):flatbuffers.Offset {
  MatchFooter.startMatchFooter(builder);
  MatchFooter.addWinner(builder, winner);
  MatchFooter.addWinType(builder, winType);
  MatchFooter.addTotalRounds(builder, totalRounds);
  MatchFooter.addTimelineMarkers(builder, timelineMarkersOffset);
  MatchFooter.addProfilerFiles(builder, profilerFilesOffset);
  return MatchFooter.endMatchFooter(builder);
}
}
