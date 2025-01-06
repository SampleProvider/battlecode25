// automatically generated by the FlatBuffers compiler, do not modify

package battlecode.schema;

import com.google.flatbuffers.BaseVector;
import com.google.flatbuffers.BooleanVector;
import com.google.flatbuffers.ByteVector;
import com.google.flatbuffers.Constants;
import com.google.flatbuffers.DoubleVector;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.flatbuffers.FloatVector;
import com.google.flatbuffers.IntVector;
import com.google.flatbuffers.LongVector;
import com.google.flatbuffers.ShortVector;
import com.google.flatbuffers.StringVector;
import com.google.flatbuffers.Struct;
import com.google.flatbuffers.Table;
import com.google.flatbuffers.UnionVector;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Markers for events during the game indicated by the user
 */
@SuppressWarnings("unused")
public final class TimelineMarker extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_24_3_25(); }
  public static TimelineMarker getRootAsTimelineMarker(ByteBuffer _bb) { return getRootAsTimelineMarker(_bb, new TimelineMarker()); }
  public static TimelineMarker getRootAsTimelineMarker(ByteBuffer _bb, TimelineMarker obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public TimelineMarker __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public byte team() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public int round() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int colorHex() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public String label() { int o = __offset(10); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer labelAsByteBuffer() { return __vector_as_bytebuffer(10, 1); }
  public ByteBuffer labelInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 10, 1); }

  public static int createTimelineMarker(FlatBufferBuilder builder,
      byte team,
      int round,
      int colorHex,
      int labelOffset) {
    builder.startTable(4);
    TimelineMarker.addLabel(builder, labelOffset);
    TimelineMarker.addColorHex(builder, colorHex);
    TimelineMarker.addRound(builder, round);
    TimelineMarker.addTeam(builder, team);
    return TimelineMarker.endTimelineMarker(builder);
  }

  public static void startTimelineMarker(FlatBufferBuilder builder) { builder.startTable(4); }
  public static void addTeam(FlatBufferBuilder builder, byte team) { builder.addByte(0, team, 0); }
  public static void addRound(FlatBufferBuilder builder, int round) { builder.addInt(1, round, 0); }
  public static void addColorHex(FlatBufferBuilder builder, int colorHex) { builder.addInt(2, colorHex, 0); }
  public static void addLabel(FlatBufferBuilder builder, int labelOffset) { builder.addOffset(3, labelOffset, 0); }
  public static int endTimelineMarker(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public TimelineMarker get(int j) { return get(new TimelineMarker(), j); }
    public TimelineMarker get(TimelineMarker obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

