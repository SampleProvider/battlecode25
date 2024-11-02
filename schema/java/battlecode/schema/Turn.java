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

@SuppressWarnings("unused")
public final class Turn extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_23_5_26(); }
  public static Turn getRootAsTurn(ByteBuffer _bb) { return getRootAsTurn(_bb, new Turn()); }
  public static Turn getRootAsTurn(ByteBuffer _bb, Turn obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public Turn __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public int robotId() { int o = __offset(4); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int health() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int paint() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int moveCooldown() { int o = __offset(10); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int actionCooldown() { int o = __offset(12); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int x() { int o = __offset(14); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public int y() { int o = __offset(16); return o != 0 ? bb.get(o + bb_pos) & 0xFF : 0; }
  public byte actionsType(int j) { int o = __offset(18); return o != 0 ? bb.get(__vector(o) + j * 1) : 0; }
  public int actionsTypeLength() { int o = __offset(18); return o != 0 ? __vector_len(o) : 0; }
  public ByteVector actionsTypeVector() { return actionsTypeVector(new ByteVector()); }
  public ByteVector actionsTypeVector(ByteVector obj) { int o = __offset(18); return o != 0 ? obj.__assign(__vector(o), bb) : null; }
  public ByteBuffer actionsTypeAsByteBuffer() { return __vector_as_bytebuffer(18, 1); }
  public ByteBuffer actionsTypeInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 18, 1); }
  public Table actions(Table obj, int j) { int o = __offset(20); return o != 0 ? __union(obj, __vector(o) + j * 4) : null; }
  public int actionsLength() { int o = __offset(20); return o != 0 ? __vector_len(o) : 0; }
  public UnionVector actionsVector() { return actionsVector(new UnionVector()); }
  public UnionVector actionsVector(UnionVector obj) { int o = __offset(20); return o != 0 ? obj.__assign(__vector(o), 4, bb) : null; }

  public static int createTurn(FlatBufferBuilder builder,
      int robotId,
      int health,
      int paint,
      int moveCooldown,
      int actionCooldown,
      int x,
      int y,
      int actionsTypeOffset,
      int actionsOffset) {
    builder.startTable(9);
    Turn.addActions(builder, actionsOffset);
    Turn.addActionsType(builder, actionsTypeOffset);
    Turn.addActionCooldown(builder, actionCooldown);
    Turn.addMoveCooldown(builder, moveCooldown);
    Turn.addPaint(builder, paint);
    Turn.addHealth(builder, health);
    Turn.addRobotId(builder, robotId);
    Turn.addY(builder, y);
    Turn.addX(builder, x);
    return Turn.endTurn(builder);
  }

  public static void startTurn(FlatBufferBuilder builder) { builder.startTable(9); }
  public static void addRobotId(FlatBufferBuilder builder, int robotId) { builder.addInt(0, robotId, 0); }
  public static void addHealth(FlatBufferBuilder builder, int health) { builder.addInt(1, health, 0); }
  public static void addPaint(FlatBufferBuilder builder, int paint) { builder.addInt(2, paint, 0); }
  public static void addMoveCooldown(FlatBufferBuilder builder, int moveCooldown) { builder.addInt(3, moveCooldown, 0); }
  public static void addActionCooldown(FlatBufferBuilder builder, int actionCooldown) { builder.addInt(4, actionCooldown, 0); }
  public static void addX(FlatBufferBuilder builder, int x) { builder.addByte(5, (byte) x, (byte) 0); }
  public static void addY(FlatBufferBuilder builder, int y) { builder.addByte(6, (byte) y, (byte) 0); }
  public static void addActionsType(FlatBufferBuilder builder, int actionsTypeOffset) { builder.addOffset(7, actionsTypeOffset, 0); }
  public static int createActionsTypeVector(FlatBufferBuilder builder, byte[] data) { builder.startVector(1, data.length, 1); for (int i = data.length - 1; i >= 0; i--) builder.addByte(data[i]); return builder.endVector(); }
  public static void startActionsTypeVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static void addActions(FlatBufferBuilder builder, int actionsOffset) { builder.addOffset(8, actionsOffset, 0); }
  public static int createActionsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addOffset(data[i]); return builder.endVector(); }
  public static void startActionsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endTurn(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public Turn get(int j) { return get(new Turn(), j); }
    public Turn get(Turn obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

