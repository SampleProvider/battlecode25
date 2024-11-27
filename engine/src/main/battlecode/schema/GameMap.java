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
public final class GameMap extends Table {
  public static void ValidateVersion() { Constants.FLATBUFFERS_23_5_26(); }
  public static GameMap getRootAsGameMap(ByteBuffer _bb) { return getRootAsGameMap(_bb, new GameMap()); }
  public static GameMap getRootAsGameMap(ByteBuffer _bb, GameMap obj) { _bb.order(ByteOrder.LITTLE_ENDIAN); return (obj.__assign(_bb.getInt(_bb.position()) + _bb.position(), _bb)); }
  public void __init(int _i, ByteBuffer _bb) { __reset(_i, _bb); }
  public GameMap __assign(int _i, ByteBuffer _bb) { __init(_i, _bb); return this; }

  public String name() { int o = __offset(4); return o != 0 ? __string(o + bb_pos) : null; }
  public ByteBuffer nameAsByteBuffer() { return __vector_as_bytebuffer(4, 1); }
  public ByteBuffer nameInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 4, 1); }
  public battlecode.schema.Vec size() { return size(new battlecode.schema.Vec()); }
  public battlecode.schema.Vec size(battlecode.schema.Vec obj) { int o = __offset(6); return o != 0 ? obj.__assign(o + bb_pos, bb) : null; }
  public int symmetry() { int o = __offset(8); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public battlecode.schema.InitialBodyTable initialBodies() { return initialBodies(new battlecode.schema.InitialBodyTable()); }
  public battlecode.schema.InitialBodyTable initialBodies(battlecode.schema.InitialBodyTable obj) { int o = __offset(10); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public int randomSeed() { int o = __offset(12); return o != 0 ? bb.getInt(o + bb_pos) : 0; }
  public boolean walls(int j) { int o = __offset(14); return o != 0 ? 0!=bb.get(__vector(o) + j * 1) : false; }
  public int wallsLength() { int o = __offset(14); return o != 0 ? __vector_len(o) : 0; }
  public BooleanVector wallsVector() { return wallsVector(new BooleanVector()); }
  public BooleanVector wallsVector(BooleanVector obj) { int o = __offset(14); return o != 0 ? obj.__assign(__vector(o), bb) : null; }
  public ByteBuffer wallsAsByteBuffer() { return __vector_as_bytebuffer(14, 1); }
  public ByteBuffer wallsInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 14, 1); }
  public int paint(int j) { int o = __offset(16); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int paintLength() { int o = __offset(16); return o != 0 ? __vector_len(o) : 0; }
  public IntVector paintVector() { return paintVector(new IntVector()); }
  public IntVector paintVector(IntVector obj) { int o = __offset(16); return o != 0 ? obj.__assign(__vector(o), bb) : null; }
  public ByteBuffer paintAsByteBuffer() { return __vector_as_bytebuffer(16, 4); }
  public ByteBuffer paintInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 16, 4); }
  public battlecode.schema.VecTable ruins() { return ruins(new battlecode.schema.VecTable()); }
  public battlecode.schema.VecTable ruins(battlecode.schema.VecTable obj) { int o = __offset(18); return o != 0 ? obj.__assign(__indirect(o + bb_pos), bb) : null; }
  public int paintPatterns(int j) { int o = __offset(20); return o != 0 ? bb.getInt(__vector(o) + j * 4) : 0; }
  public int paintPatternsLength() { int o = __offset(20); return o != 0 ? __vector_len(o) : 0; }
  public IntVector paintPatternsVector() { return paintPatternsVector(new IntVector()); }
  public IntVector paintPatternsVector(IntVector obj) { int o = __offset(20); return o != 0 ? obj.__assign(__vector(o), bb) : null; }
  public ByteBuffer paintPatternsAsByteBuffer() { return __vector_as_bytebuffer(20, 4); }
  public ByteBuffer paintPatternsInByteBuffer(ByteBuffer _bb) { return __vector_in_bytebuffer(_bb, 20, 4); }

  public static void startGameMap(FlatBufferBuilder builder) { builder.startTable(9); }
  public static void addName(FlatBufferBuilder builder, int nameOffset) { builder.addOffset(0, nameOffset, 0); }
  public static void addSize(FlatBufferBuilder builder, int sizeOffset) { builder.addStruct(1, sizeOffset, 0); }
  public static void addSymmetry(FlatBufferBuilder builder, int symmetry) { builder.addInt(2, symmetry, 0); }
  public static void addInitialBodies(FlatBufferBuilder builder, int initialBodiesOffset) { builder.addOffset(3, initialBodiesOffset, 0); }
  public static void addRandomSeed(FlatBufferBuilder builder, int randomSeed) { builder.addInt(4, randomSeed, 0); }
  public static void addWalls(FlatBufferBuilder builder, int wallsOffset) { builder.addOffset(5, wallsOffset, 0); }
  public static int createWallsVector(FlatBufferBuilder builder, boolean[] data) { builder.startVector(1, data.length, 1); for (int i = data.length - 1; i >= 0; i--) builder.addBoolean(data[i]); return builder.endVector(); }
  public static void startWallsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(1, numElems, 1); }
  public static void addPaint(FlatBufferBuilder builder, int paintOffset) { builder.addOffset(6, paintOffset, 0); }
  public static int createPaintVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startPaintVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static void addRuins(FlatBufferBuilder builder, int ruinsOffset) { builder.addOffset(7, ruinsOffset, 0); }
  public static void addPaintPatterns(FlatBufferBuilder builder, int paintPatternsOffset) { builder.addOffset(8, paintPatternsOffset, 0); }
  public static int createPaintPatternsVector(FlatBufferBuilder builder, int[] data) { builder.startVector(4, data.length, 4); for (int i = data.length - 1; i >= 0; i--) builder.addInt(data[i]); return builder.endVector(); }
  public static void startPaintPatternsVector(FlatBufferBuilder builder, int numElems) { builder.startVector(4, numElems, 4); }
  public static int endGameMap(FlatBufferBuilder builder) {
    int o = builder.endTable();
    return o;
  }

  public static final class Vector extends BaseVector {
    public Vector __assign(int _vector, int _element_size, ByteBuffer _bb) { __reset(_vector, _element_size, _bb); return this; }

    public GameMap get(int j) { return get(new GameMap(), j); }
    public GameMap get(GameMap obj, int j) {  return obj.__assign(__indirect(__element(j), bb), bb); }
  }
}

