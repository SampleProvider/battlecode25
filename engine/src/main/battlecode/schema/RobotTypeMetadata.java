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
public final class RobotTypeMetadata extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_23_5_26(); }
  public static RobotTypeMetadata getRootAsRobotTypeMetadata(ByteBuffer _bb) { return getRootAsRobotTypeMetadata(_bb, new RobotTypeMetadata()); }
  public static RobotTypeMetadata getRootAsRobotTypeMetadata(ByteBuffer _bb, RobotTypeMetadata obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public RobotTypeMetadata __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public byte type() { int o = __offset(4); return o != 0 ? bb.get(o + bb_pos) : 0; }
  public int actionCooldown() { int o = __offset(6); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int movementCooldown() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int baseHealth() { int o = __offset(10); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int basePaint() { int o = __offset(12); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int maxPaint() { int o = __offset(14); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int actionRadiusSquared() { int o = __offset(16); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int visionRadiusSquared() { int o = __offset(18); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public int bytecodeLimit() { int o = __offset(20); return o != 0 ? bb.getInt(o + bb_pos) : 0; }

  public static int createRobotTypeMetadata(FlatBufferBuilder builder,
      byte type,
      int actionCooldown,
      int movementCooldown,
      int baseHealth,
      int basePaint,
      int maxPaint,
      int actionRadiusSquared,
      int visionRadiusSquared,
      int bytecodeLimit) {
    builder.startTable(9);
    RobotTypeMetadata.addBytecodeLimit(builder, bytecodeLimit);
    RobotTypeMetadata.addVisionRadiusSquared(builder, visionRadiusSquared);
    RobotTypeMetadata.addActionRadiusSquared(builder, actionRadiusSquared);
    RobotTypeMetadata.addMaxPaint(builder, maxPaint);
    RobotTypeMetadata.addBasePaint(builder, basePaint);
    RobotTypeMetadata.addBaseHealth(builder, baseHealth);
    RobotTypeMetadata.addMovementCooldown(builder, movementCooldown);
    RobotTypeMetadata.addActionCooldown(builder, actionCooldown);
    RobotTypeMetadata.addType(builder, type);
    return RobotTypeMetadata.endRobotTypeMetadata(builder);
  }

  public static void startRobotTypeMetadata(FlatBufferBuilder builder) { builder.startTable(9); }
  public static void addType(FlatBufferBuilder builder, byte type) { builder.addByte(0, type, 0); }
  public static void addActionCooldown(FlatBufferBuilder builder, int actionCooldown) { builder.addInt(1, actionCooldown, 0); }
  public static void addMovementCooldown(FlatBufferBuilder builder, int movementCooldown) { builder.addInt(2, movementCooldown, 0); }
  public static void addBaseHealth(FlatBufferBuilder builder, int baseHealth) { builder.addInt(3, baseHealth, 0); }
  public static void addBasePaint(FlatBufferBuilder builder, int basePaint) { builder.addInt(4, basePaint, 0); }
  public static void addMaxPaint(FlatBufferBuilder builder, int maxPaint) { builder.addInt(5, maxPaint, 0); }
  public static void addActionRadiusSquared(FlatBufferBuilder builder, int actionRadiusSquared) { builder.addInt(6, actionRadiusSquared, 0); }
  public static void addVisionRadiusSquared(FlatBufferBuilder builder, int visionRadiusSquared) { builder.addInt(7, visionRadiusSquared, 0); }
  public static void addBytecodeLimit(FlatBufferBuilder builder, int bytecodeLimit) { builder.addInt(8, bytecodeLimit, 0); }
  public static int endRobotTypeMetadata(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public RobotTypeMetadata get(int j) { return get(new RobotTypeMetadata(), j); }
    public RobotTypeMetadata get(RobotTypeMetadata obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}