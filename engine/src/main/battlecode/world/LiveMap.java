package battlecode.world;

import battlecode.common.*;
import battlecode.util.FlatHelpers;

import java.io.IOException;
import java.util.*;
import java.util.function.*;

import javax.management.RuntimeErrorException;

/**
 * The class represents the map in the game world on which
 * objects interact.
 *
 * This class is STATIC and immutable. It reflects the initial
 * condition of the map. All changes to the map are reflected in GameWorld.
 *
 * It is named LiveMap to distinguish it from a battlecode.schema.GameMap,
 * which represents a serialized LiveMap.
 */
public class LiveMap {
    
    /**
     * The width and height of the map.
     */
    private final int width, height;

    /**
     * The coordinates of the origin
     */
    private final MapLocation origin;

    /**
     * The symmetry of the map.
     */
    private final MapSymmetry symmetry;

    /**
     * Whether each square is a wall.
     */
    private boolean[] wallArray;

    /**
     * What kind of paint is on the square.
     */
    private byte[] paintArray;

    /**
     * Whether each square is a ruin.
     */
    private boolean[] ruinArray;

    /**
     * The patterns for resources and towers.
     */
    private int[] patternArray;

    /**
     * The random seed contained in the map file.
     */
    private final int seed;

    /**
     * The maximum number of rounds in the game.
     */
    private final int rounds;

    /**
     * The name of the map.
     */
    private final String mapName;

    /**
     * The bodies to spawn on the map; MapLocations are in world space -
     * i.e. in game correct MapLocations that need to have the origin
     * subtracted from them to be used to index into the map arrays.
     */
    private final RobotInfo[] initialBodies; // contains nothing

    

    public LiveMap(int width,
                   int height,
                   MapLocation origin,
                   int seed,
                   int rounds,
                   String mapName, 
                   RobotInfo[] initialBodies) {
        this.width = width;
        this.height = height;
        this.origin = origin;
        this.seed = seed;
        this.rounds = rounds;
        this.mapName = mapName;
        this.symmetry = MapSymmetry.ROTATIONAL;
        this.initialBodies = Arrays.copyOf(initialBodies, initialBodies.length);
        int numSquares = width * height;
        this.wallArray = new boolean[numSquares];
        this.paintArray = new byte[numSquares];
        this.ruinArray = new boolean[numSquares];
        this.patternArray = new int[4];

        // invariant: bodies is sorted by id
        Arrays.sort(this.initialBodies, (a, b) -> Integer.compare(a.getID(), b.getID()));
    }

    public LiveMap(int width,
                   int height,
                   MapLocation origin,
                   int seed,
                   int rounds,
                   String mapName,
                   MapSymmetry symmetry,
                   boolean[] wallArray,
                   byte[] paintArray,
                   boolean[] ruinArray,
                   int[] patternArray,
                   RobotInfo[] initialBodies) {
        this.width = width;
        this.height = height;
        this.origin = origin;
        this.seed = seed;
        this.rounds = rounds;
        this.mapName = mapName;
        this.symmetry = symmetry;
        this.initialBodies = Arrays.copyOf(initialBodies, initialBodies.length);
        this.wallArray = new boolean[wallArray.length];
        for (int i = 0; i < wallArray.length; i++) {
            this.wallArray[i] = wallArray[i];
            // this.wallArray[i] = Math.random() < 0.5;
        }
        this.paintArray = new byte[paintArray.length];
        for (int i = 0; i < paintArray.length; i++){
            this.paintArray[i] = paintArray[i];
            // this.paintArray[i] = (byte) Math.floor(Math.random() * 5);
        }
        this.ruinArray = new boolean[ruinArray.length];
        for (int i = 0; i < ruinArray.length; i++){
            this.ruinArray[i] = ruinArray[i];
            // this.ruinArray[i] = Math.random() < 0.5;
        }
        this.patternArray = new int[patternArray.length];
        for (int i = 0; i < patternArray.length; i++){
            this.patternArray[i] = patternArray[i];
        }
        // invariant: bodies is sorted by id
       Arrays.sort(this.initialBodies, (a, b) -> Integer.compare(a.getID(), b.getID()));
    }

    /**
     * Creates a deep copy of the input LiveMap, except initial bodies.
     *
     * @param gm the LiveMap to copy.
     */
    public LiveMap(LiveMap gm) {
        this(gm.width, gm.height, gm.origin, gm.seed, gm.rounds, gm.mapName, gm.symmetry,
         gm.wallArray, gm.paintArray, gm.ruinArray, gm.patternArray, gm.initialBodies);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LiveMap)) return false;
        return this.equals((LiveMap) o);
    }

    /**
     * Returns whether two GameMaps are equal.
     *
     * @param other the other map to compare to
     * @return whether the two maps are equivalent
     */
    public boolean equals(LiveMap other) {
        if (this.rounds != other.rounds) return false;
        if (this.width != other.width) return false;
        if (this.height != other.height) return false;
        if (this.seed != other.seed) return false;
        if (!this.mapName.equals(other.mapName)) return false;
        if (!this.origin.equals(other.origin)) return false;
        if (!Arrays.equals(this.wallArray, other.wallArray)) return false;
        if (!Arrays.equals(this.paintArray, other.paintArray)) return false;
        if (!Arrays.equals(this.ruinArray, other.ruinArray)) return false;
        if (!Arrays.equals(this.patternArray, other.patternArray)) return false;
        if (!Arrays.equals(this.initialBodies, other.initialBodies)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + origin.hashCode();
        result = 31 * result + seed;
        result = 31 * result + rounds;
        result = 31 * result + mapName.hashCode();
        result = 31 * result + Arrays.hashCode(wallArray);
        result = 31 * result + Arrays.hashCode(paintArray);
        result = 31 * result + Arrays.hashCode(ruinArray);
        result = 31 * result + Arrays.hashCode(patternArray);
        result = 31 * result + Arrays.hashCode(initialBodies);
        return result;
    }

    /**
     * Returns the width of this map.
     *
     * @return the width of this map.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the map.
     *
     * @return the height of the map
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the name of the map.
     *
     * @return the name of the map
     */
    public String getMapName() {
        return mapName;
    }

    /**
     * Returns the symmetry of the map.
     *
     * @return the symmetry of the map
     */
    public MapSymmetry getSymmetry() {
        return symmetry;
    }

    /**
     * Determines whether or not the location at the specified
     * coordinates is on the map. The coordinate should be a shifted one
     * (takes into account the origin). Assumes grid format (0 <= x < width).
     *
     * @param x the (shifted) x-coordinate of the location
     * @param y the (shifted) y-coordinate of the location
     * @return true if the given coordinates are on the map,
     *         false if they're not
     */
    private boolean onTheMap(int x, int y) {
        return (x >= origin.x && y >= origin.y && x < origin.x + width && y < origin.y + height);
    }

    /**
     * Determines whether or not the specified location is on the map.
     *
     * @param loc the MapLocation to test
     * @return true if the given location is on the map,
     *         false if it's not
     */
    public boolean onTheMap(MapLocation loc) {
        return onTheMap(loc.x, loc.y);
    }

    /**
     * Determines whether or not the specified circle is completely on the map.
     *
     * @param loc the center of the circle
     * @param radius the radius of the circle
     * @return true if the given circle is on the map,
     *         false if it's not
     */
    public boolean onTheMap(MapLocation loc, int radius) {
        return (onTheMap(loc.translate(-radius, 0)) &&
                onTheMap(loc.translate(radius, 0)) &&
                onTheMap(loc.translate(0, -radius)) &&
                onTheMap(loc.translate(0, radius)));
    }

    /**
     * Gets the maximum number of rounds for this game.
     *
     * @return the maximum number of rounds for this game
     */
    public int getRounds() {
        return rounds;
    }

    /**
     * @return the seed of this map
     */
    public int getSeed() {
        return seed;
    }

    /**
     * Gets the origin (i.e., upper left corner) of the map
     *
     * @return the origin of the map
     */
    public MapLocation getOrigin() {
        return origin;
    }

    /**
     * Get a list of the initial bodies on the map.
     *
     * @return the list of starting bodies on the map.
     *         MUST NOT BE MODIFIED.
     */
    public RobotInfo[] getInitialBodies() {
        return initialBodies;
    }

    /**
     * @return the wall array of the map
     */
    public boolean[] getWallArray() {
        return wallArray;
    }

    /**
     * @return the paint array of the map
     */
    public byte[] getPaintArray() {
        return paintArray;
    }

    /**
     * @return the ruin array of the map
     */
    public boolean[] getRuinArray(){
        return ruinArray;
    }

    /**
     * @return the pattern array of the map
     */
    public int[] getPatternArray(){
        return patternArray;
    }


    /**
     * Helper method that converts a location into an index.
     * 
     * @param loc the MapLocation
     */
    public int locationToIndex(MapLocation loc) {
        return loc.x - getOrigin().x + (loc.y - getOrigin().y) * getWidth();
    }

    /**
     * Helper method that converts an index into a location.
     * 
     * @param idx the index
     */
    public MapLocation indexToLocation(int idx) {
        return new MapLocation(idx % getWidth() + getOrigin().x,
                               idx / getWidth() + getOrigin().y);
    }

    public void assertIsValid() throws Exception{
        // if (this.width > GameConstants.MAP_MAX_WIDTH) {
        //     throw new RuntimeException("MAP WIDTH EXCEEDS GameConstants.MAP_MAX_WIDTH");
        // }
        // if (this.width < GameConstants.MAP_MIN_WIDTH) {
        //     throw new RuntimeException("MAP WIDTH BENEATH GameConstants.MAP_MIN_WIDTH");
        // }
        // if (this.height > GameConstants.MAP_MAX_HEIGHT) {
        //     throw new RuntimeException("MAP HEIGHT EXCEEDS GameConstants.MAP_MAX_HEIGHT");
        // }
        // if (this.height < GameConstants.MAP_MIN_HEIGHT) {
        //     throw new RuntimeException("MAP HEIGHT BENEATH GameConstants.MAP_MIN_HEIGHT");
        // }
        // int[] towerCountA = new int[3];
        // int[] towerCountB = new int[3];
        // int initialBodyCountTeamA = 0;
        // int initialBodyCountTeamB = 0;
        // for (RobotInfo initialBody : initialBodies){
        //     if (initialBody.team == Team.A){
        //         towerCountA[FlatHelpers.getRobotTypeFromUnitType(initialBody.type)-1] += 1;
        //         initialBodyCountTeamA++;
        //     }
        //     else if (initialBody.team == Team.B){
        //         towerCountB[FlatHelpers.getRobotTypeFromUnitType(initialBody.type)-1] += 1;
        //         initialBodyCountTeamB++;
        //     }
        //     else {
        //         throw new RuntimeException("Expected initial body team "  + initialBody.team + " to be team A or team B!");
        //     }
        // }
        // if (initialBodyCountTeamA != GameConstants.NUMBER_INITIAL_TOWERS) {
        //     throw new RuntimeException("Expected to have "  + GameConstants.NUMBER_INITIAL_TOWERS + " team A towers!");
        // }
        // if (initialBodyCountTeamB != GameConstants.NUMBER_INITIAL_TOWERS) {
        //     throw new RuntimeException("Expected to have "  + GameConstants.NUMBER_INITIAL_TOWERS + " team B towers!");
        // }
        // if (towerCountA[FlatHelpers.getRobotTypeFromUnitType(UnitType.LEVEL_ONE_PAINT_TOWER) - 1] != GameConstants.NUMBER_INITIAL_PAINT_TOWERS){
        //     throw new RuntimeException("Expected to have "  + GameConstants.NUMBER_INITIAL_PAINT_TOWERS + " paint towers!");
        // }  
        // if (towerCountA[FlatHelpers.getRobotTypeFromUnitType(UnitType.LEVEL_ONE_MONEY_TOWER) - 1] != GameConstants.NUMBER_INITIAL_MONEY_TOWERS){
        //     throw new RuntimeException("Expected to have "  + GameConstants.NUMBER_INITIAL_MONEY_TOWERS + " money towers!");
        // }
        // if (towerCountA[FlatHelpers.getRobotTypeFromUnitType(UnitType.LEVEL_ONE_DEFENSE_TOWER) - 1] != GameConstants.NUMBER_INITIAL_DEFENSE_TOWERS){
        //     throw new RuntimeException("Expected to have "  + GameConstants.NUMBER_INITIAL_DEFENSE_TOWERS + " defense towers!");
        // }
        // for (int i = 0; i < towerCountA.length; i++){
        //     if (towerCountA[i] != towerCountB[i]){
        //         throw new RuntimeException("Expected both teams to have the same number of towers of type " + FlatHelpers.getUnitTypeFromRobotType((byte)(i+1)));
        //     }
        // }

        // ArrayList<MapLocation> ruinLocs = new ArrayList<>();
        // int numWalls = 0;
        // for (int i = 0; i < this.width*this.height; i++){
        //     if (this.wallArray[i] && this.ruinArray[i]){
        //         throw new RuntimeException("Walls can't be on the same square as ruins");
        //     }
        //     if (this.ruinArray[i])
        //         ruinLocs.add(indexToLocation(i));
        //     if (this.wallArray[i])
        //         numWalls += 1;
        // }
        // if (numWalls * 100 >= this.width * this.height * GameConstants.MAX_WALL_PERCENTAGE){
        //     throw new RuntimeException("Too much of the area of the map is composed of walls!");
        // }

        // for (int i = 0; i < ruinLocs.size(); i++){
        //     MapLocation curRuin = ruinLocs.get(i);
        //     for (int j = i + 1; j < ruinLocs.size(); j++){
        //         MapLocation otherRuin = ruinLocs.get(j);
        //         if (curRuin.distanceSquaredTo(otherRuin) < GameConstants.MIN_RUIN_SPACING_SQUARED)
        //             throw new RuntimeException("Ruins at location " + curRuin.toString() + " and location " + otherRuin.toString() + " are too close to each other!");
        //     }
        // }
        // for (int i = 0; i < this.width * this.height; i++){
        //     if (this.wallArray[i]){
        //         for (MapLocation ruin : ruinLocs){
        //             if (ruin.distanceSquaredTo(indexToLocation(i)) <= 8) // 2^2 + 2^2 
        //                 throw new RuntimeException("Wall appears at location " + indexToLocation(i).toString() + " which is too close to ruin " + ruin.toString());
        //         }
        //     }
        // }
    }

    private boolean isTeamNumber(int team) {
        return team == 1 || team == 2;
    }

    private int getOpposingTeamNumber(int team) {
        switch (team) {
            case 1:
                return 2;
            case 2:
                return 1;
            default:
                throw new RuntimeException("Argument of LiveMap.getOpposingTeamNumber must be a valid team number, was " + team + ".");
        }
    }

    /**
     * Performs a flood fill algorithm to check if a predicate is true for any squares
     * that can be reached from a given location (horizontal, vertical, and diagonal steps allowed).
     * 
     * @param startLoc the starting location
     * @param checkForBad the predicate to check for each reachable square
     * @param checkForWall a predicate that checks if the given square has a wall
     * @param alreadyChecked an array indexed by map location indices which has "true" at
     * every location reachable from a spawn zone that has already been checked
     * (WARNING: this array gets updated by floodFillMap)
     * @return if checkForBad returns true for any reachable squares
     */
    private boolean floodFillMap(MapLocation startLoc, Predicate<MapLocation> checkForBad, Predicate<MapLocation> checkForWall, boolean[] alreadyChecked) {
        Queue<MapLocation> queue = new LinkedList<MapLocation>(); // stores map locations by index

        if (!onTheMap(startLoc)) {
            throw new RuntimeException("Cannot call floodFillMap with startLocation off the map.");
        }

        queue.add(startLoc);

        while (!queue.isEmpty()) {
            MapLocation loc = queue.remove();
            int idx = locationToIndex(loc);

            if (alreadyChecked[idx]) {
                continue;
            }

            alreadyChecked[idx] = true;

            if (!checkForWall.test(loc)) {
                if (checkForBad.test(loc)) {
                    return true;
                }

                for (Direction dir : Direction.allDirections()) {
                    if (dir != Direction.CENTER) {
                        MapLocation newLoc = loc.add(dir);

                        if (onTheMap(newLoc)) {
                            int newIdx = locationToIndex(newLoc);

                            if (!(alreadyChecked[newIdx] || checkForWall.test(newLoc))) {
                                queue.add(newLoc);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    @Override
    public String toString() {
        if (wallArray.length == 0) {
            return "LiveMap{" +
                    "width=" + width +
                    ", height=" + height +
                    ", origin=" + origin +
                    ", seed=" + seed +
                    ", rounds=" + rounds +
                    ", mapName='" + mapName + '\'' +
                    ", len=" + Integer.toString(wallArray.length) +
                    ", initialBodies=" + Arrays.toString(initialBodies) +
                    "}";
        } else {
            return "LiveMap{" +
                    "width=" + width +
                    ", height=" + height +
                    ", origin=" + origin +
                    ", seed=" + seed +
                    ", rounds=" + rounds +
                    ", mapName='" + mapName + '\'' +
                    ", paintArray=" + Arrays.toString(paintArray) + 
                    ", wallArray=" + Arrays.toString(wallArray) +
                    ", ruinArray=" + Arrays.toString(ruinArray) + 
                    ", patternArray=" + Arrays.toString(patternArray) + 
                    ", initialBodies=" + Arrays.toString(initialBodies) + 
                    "}";
        }
    }
}
