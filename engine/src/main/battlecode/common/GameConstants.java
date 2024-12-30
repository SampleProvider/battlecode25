package battlecode.common;

/**
 * GameConstants defines constants that affect gameplay.
 */
@SuppressWarnings("unused")
public class GameConstants {

    /**
     * The current spec version the server compiles with.
     */
    public static final String SPEC_VERSION = "3.0.6";

    // *********************************
    // ****** MAP CONSTANTS ************
    // *********************************

    /** The minimum possible map height. */
    public static final int MAP_MIN_HEIGHT = 20;

    /** The maximum possible map height. */
    public static final int MAP_MAX_HEIGHT = 60;

    /** The minimum possible map width. */
    public static final int MAP_MIN_WIDTH = 20;

    /** The maximum possible map width. */
    public static final int MAP_MAX_WIDTH = 60;

    /** The minimum distance between ruins on the map */
    public static final int MIN_RUIN_SPACING_SQUARED = 25;

    /** The maximum percentage of the map that can be walls */
    public static final int MAX_WALL_PERCENTAGE = 20;

    // *********************************
    // ****** GAME PARAMETERS **********
    // *********************************

    /** The default game seed. **/
    public static final int GAME_DEFAULT_SEED = 6370;

    /** The maximum number of rounds in a game. **/
    public static final int GAME_MAX_NUMBER_OF_ROUNDS = 2000;

    /** The maximum number of bytecodes a bot is allow to use in one turn */
    public static final int BYTECODE_LIMIT = 25000;

    /**
     * The maximum length of indicator strings that a player can associate with a
     * robot.
     */
    public static final int INDICATOR_STRING_MAX_LENGTH = 64;

    /** The maximum length of a label to add to the timeline. */
    public static final int TIMELINE_LABEL_MAX_LENGTH = 64;

    /** The bytecode penalty that is imposed each time an exception is thrown. */
    public static final int EXCEPTION_BYTECODE_PENALTY = 500;

    /** Paint penalty for ending a turn on enemy territory */
    public static final int PENALTY_ENEMY_TERRITORY = 2;

    /** Paint penalty for ending a turn on neutral territory */
    public static final int PENALTY_NEUTRAL_TERRITORY = 1;

    /** The total number of robots a team has (both despawned or spawned). */
    public static final int ROBOT_CAPACITY = 50;

    /** Paint capacity for soldier robots */
    public static final int PAINT_CAPACITY_SOLDIER = 200;

    /** Paint capacity for splasher robots */
    public static final int PAINT_CAPACITY_SPLASHER = 300;

    /** Paint capacity for mopper robots */
    public static final int PAINT_CAPACITY_MOPPER = 100;

    /** The amount of a paint a paint tower starts with. */
    public static final int INITIAL_PAINT_TOWER_PAINT = 500;

    /** The amount of money each team starts with. */
    public static final int INITIAL_TEAM_MONEY = 1000;

    /** The percent of the map which a team needs to paint to win. */
    public static final int PAINT_PERCENT_TO_WIN = 70;

     /** The maximum number of towers that a team can have. */
     public static final int MAX_NUMBER_OF_TOWERS = 25;

    // *********************************
    // ****** GAME MECHANICS ***********
    // *********************************

    /** The number of towers a player starts with. */
    public static final int NUMBER_INITIAL_TOWERS = 2;

    /** The number of paint towers a player starts with */
    public static final int NUMBER_INITIAL_PAINT_TOWERS = 1;

    /** The number of money towers a player starts with */
    public static final int NUMBER_INITIAL_MONEY_TOWERS = 1;

    /** The number of defense towers a player starts with */
    public static final int NUMBER_INITIAL_DEFENSE_TOWERS = 0;

    /** The width and height of the patterns that robots can draw */
    public static final int PATTERN_SIZE = 5;

    /** The paint cost of marking a resource or tower pattern */
    public static final int MARK_PATTERN_PAINT_COST = 25;

    /** Maximum amount of turns a robot can go at 0 paint without dying */
    public static final int MAX_TURNS_WITHOUT_PAINT = 10;

    /** Maximum percent amount of paint to start cooldown */
    public static final int DECREASED_MOVEMENT_THRESHOLD = 50;

    /** Intercept in the formula for the movement cooldown */
    public static final int MOVEMENT_COOLDOWN_INTERCEPT = 100;

    /** Slope of paint in the formula for the movement cooldown */
    public static final int MOVEMENT_COOLDOWN_SLOPE = -2;

    /** Multiplier for paint penalties moppers face for ending on non-ally territory. */
    public static final int MOPPER_PAINT_PENALTY_MULTIPLIER = 2;

    /** The maximum distance from a robot where information can be sensed */
    public static final int VISION_RADIUS_SQUARED = 20;

    /** The maximum distance for marking a map location or removing a marker */
    public static final int MARK_RADIUS_SQUARED = 2;

    /** The maximum distance for transferring paint from/to an ally robot or tower */
    public static final int PAINT_TRANSFER_RADIUS_SQUARED = 2;

    /** The maximum distance from a tower for building robots */
    public static final int BUILD_ROBOT_RADIUS_SQUARED = 4;

    /** The maximum distance from a robot for building towers */
    public static final int BUILD_TOWER_RADIUS_SQUARED = 2;

    /** The maximum distance from a robot for completing special resource patterns */
    // this is 8 so that the robot can complete the pattern anywhere on the 5x5 grid
    public static final int RESOURCE_PATTERN_RADIUS_SQUARED = 8;

    /** The amount of paint depleted from enemy in a regular mopper attack */
    public static final int MOPPER_ATTACK_PAINT_DEPLETION = 10;

    /** The amount of paint added to self in a regular mopper attack */
    public static final int MOPPER_ATTACK_PAINT_ADDITION = 5;

    /** The amount of paint depleted from enemies in a swing mopper attack */
    public static final int MOPPER_SWING_PAINT_DEPLETION = 5;

    // *********************************
    // ****** COOLDOWNS ****************
    // *********************************

    /** If the amount of cooldown is at least this value, a robot cannot act. */
    public static final int COOLDOWN_LIMIT = 10;

    /** The number of cooldown turns reduced per turn. */
    public static final int COOLDOWNS_PER_TURN = 10;

    /**
     * The amount added to the movement cooldown counter when moving
     */
     public static final int MOVEMENT_COOLDOWN = 10;

    /**
     * The amount added to the action cooldown counter after a tower builds a robot
     */
     public static final int BUILD_ROBOT_COOLDOWN = 10;

    /** The amount added to the action cooldown counter after attacking (as a mopper for the swing attack) */
    public static final int ATTACK_MOPPER_SWING_COOLDOWN = 40;

    /** THe amount added to the action cooldown counter after transferring paint */
    public static final int PAINT_TRANSFER_COOLDOWN = 10;

    /** The maximum amount of bytes that can be encoded in a message */
    public static final int MAX_MESSAGE_BYTES = 4;

    /** The maximum squared radius a robot can send a message to */
    public static final int MESSAGE_RADIUS_SQUARED = 20;

    /** The maximum number of rounds a message will exist for */
    public static final int MESSAGE_ROUND_DURATION = 5;

    /** The maximum number of messages a robot can send per turn */
    public static final int MAX_MESSAGES_SENT_ROBOT = 1;

    /** The maximum number of messages a tower can send per turn */
    public static final int MAX_MESSAGES_SENT_TOWER = 20;

}
