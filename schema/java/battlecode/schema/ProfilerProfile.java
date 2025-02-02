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
 * A profile contains all events and is labeled with a name.
 */
@SuppressWarnings("unused")
public final class ProfilerProfile extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_24_3_25(); }
  public static ProfilerProfile getRootAsProfilerProfile(ByteBuffer _bb) { return getRootAsProfilerProfile(_bb, new ProfilerProfile()); }
  public static ProfilerProfile getRootAsProfilerProfile(ByteBuffer _bb, ProfilerProfile obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public ProfilerProfile __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  /**
   * The display-friendly name of the profile.
   */
  public String name() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer nameAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer nameInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  /**
   * The events that occurred in the profile.
   */
  public battlecode.schema.ProfilerEvent events(int j) { return events(new battlecode.schema.ProfilerEvent(), j); }
  public battlecode.schema.ProfilerEvent events(battlecode.schema.ProfilerEvent obj, int j) { int o = __offset(6); return o != 0 ? obj.__assign(__indirect(__vector(o) + j * 4), bb) : null; }
  public int eventsLength() { int o = __offset(6); return o != 0 ? __vector_len(o) : 0; }
  public battlecode.schema.ProfilerEvent.Vector eventsVector() { return eventsVector(new battlecode.schema.ProfilerEvent.Vector()); }
  public battlecode.schema.ProfilerEvent.Vector eventsVector(battlecode.schema.ProfilerEvent.Vector obj) { int o = __offset(6); return o != 0 ? obj.__assign(__vector(o), 4, bb) : null; }

  public static int createProfilerProfile(FlatBufferBuilder builder,
      int nameOffset,
      int eventsOffset) {
    builder.startTable(2);
    ProfilerProfile.addEvents(builder, eventsOffset);
    ProfilerProfile.addName(builder, nameOffset);
    return ProfilerProfile.endProfilerProfile(builder);
  }

  public static void startProfilerProfile(FlatBufferBuilder builder) { builder.startTable(2); }
  public static void addName(FlatBufferBuilder builder, int nameOffset) { builder.addOffset(0, nameOffset, 0); }
  public static void addEvents(FlatBufferBuilder builder, int eventsOffset) { builder.addOffset(1, eventsOffset, 0); }
  public static int createEventsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startEventsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endProfilerProfile(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public ProfilerProfile get(int j) { return get(new ProfilerProfile(), j); }
    public ProfilerProfile get(ProfilerProfile obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

