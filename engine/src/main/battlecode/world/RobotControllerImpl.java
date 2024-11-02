package battlecode.world;

import battlecode.common.*;

import static battlecode.common.GameActionExceptionType.*;
import battlecode.schema.Action;
import battlecode.util.FlatHelpers;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;

/**
 * The actual implementation of RobotController. Its methods *must* be called
 * from a player thread.
 *
 * It is theoretically possible to have multiple for a single InternalRobot, but
 * that may cause problems in practice, and anyway why would you want to?
 *
 * All overriden methods should assertNotNull() all of their (Object) arguments,
 * if those objects are not explicitly stated to be nullable.
 */
public final strictfp class RobotControllerImpl implements RobotController {

    /**
     * The world the robot controlled by this controller inhabits.
     */
    private final GameWorld gameWorld;

    /**
     * The robot this controller controls.
     */
    private final InternalRobot robot;

    /**
     * Create a new RobotControllerImpl
     * 
     * @param gameWorld the relevant world
     * @param robot     the relevant robot
     */
    public RobotControllerImpl(GameWorld gameWorld, InternalRobot robot) {
        this.gameWorld = gameWorld;
        this.robot = robot;
    }

    // *********************************
    // ******** INTERNAL METHODS *******
    // *********************************

    /**
     * Throw a null pointer exception if an object is null.
     *
     * @param o the object to test
     */
    private static void assertNotNull(Object o) {
        if (o == null) {
            throw new NullPointerException("Argument has an invalid null value");
        }
    }

    @Override
    public int hashCode() {
        return getID();
    }

    private InternalRobot getRobotByID(int id) {
        if (!this.gameWorld.getObjectInfo().existsRobot(id))
            return null;
        return this.gameWorld.getObjectInfo().getRobotByID(id);
    }

    private int locationToInt(MapLocation loc) {
        return this.gameWorld.locationToIndex(loc);
    }

    private MapInfo getMapInfo(MapLocation loc) throws GameActionException {
        GameWorld gw = this.gameWorld;

        Trap trap = gw.getTrap(loc);
        TrapType type = (trap != null && trap.getTeam() == robot.getTeam()) ? trap.getType() : TrapType.NONE;

        int territory = gameWorld.getTeamSide(loc);
        Team territoryTeam = null;
        if (territory == 0)
            territoryTeam = Team.NEUTRAL;
        else
            territoryTeam = territory == 1 ? Team.A : Team.B;

        MapInfo currentLocInfo = new MapInfo(loc, gw.isPassable(loc), gw.getWall(loc), gw.getDam(loc),
                gw.getSpawnZone(loc), gw.getWater(loc), gw.getBreadAmount(loc), type, territoryTeam);

        return currentLocInfo;
    }

    // *********************************
    // ****** GLOBAL QUERY METHODS *****
    // *********************************

    @Override
    public int getRoundNum() {
        return this.gameWorld.getCurrentRound();
    }

    @Override
    public int getMapWidth() {
        return this.gameWorld.getGameMap().getWidth();
    }

    @Override
    public int getMapHeight() {
        return this.gameWorld.getGameMap().getHeight();
    }

    // *********************************
    // ****** UNIT QUERY METHODS *******
    // *********************************

    @Override
    public int getID() {
        return this.robot.getID();
    }

    @Override
    public Team getTeam() {
        return this.robot.getTeam();
    }

    @Override
    public MapLocation getLocation() {
        return this.robot.getLocation();
    }

    @Override
    public int getExperience(SkillType skill) {
        return this.robot.getExp(skill);
    }

    @Override
    public int getLevel(SkillType skill) {
        return this.robot.getLevel(skill);
    }

    @Override
    public int getHealth() {
        return this.robot.getHealth();
    }

    @Override
    public int getMoney() {
        return this.gameWorld.getTeamInfo().getMoney(getTeam());
    }

    // ***********************************
    // ****** GENERAL VISION METHODS *****
    // ***********************************

    @Override
    public boolean onTheMap(MapLocation loc) {
        assertNotNull(loc);
        if (!this.gameWorld.getGameMap().onTheMap(loc))
            return false;
        return true;
    }

    private void assertCanSenseLocation(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertIsSpawned();
        if (!this.gameWorld.getGameMap().onTheMap(loc))
            throw new GameActionException(CANT_SENSE_THAT,
                    "Target location is not on the map");
        if (!this.robot.canSenseLocation(loc))
            throw new GameActionException(CANT_SENSE_THAT,
                    "Target location not within vision range");
    }

    private void assertCanActLocation(MapLocation loc, int maxRadius) throws GameActionException {
        assertNotNull(loc);
        assertIsSpawned();
        if (getLocation().distanceSquaredTo(loc) > maxRadius)
            throw new GameActionException(OUT_OF_RANGE,
                    "Target location not within action range");
        if (!this.gameWorld.getGameMap().onTheMap(loc))
            throw new GameActionException(CANT_SENSE_THAT,
                    "Target location is not on the map");
    }

    @Override
    public boolean canSenseLocation(MapLocation loc) {
        try {
            assertCanSenseLocation(loc);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public boolean isLocationOccupied(MapLocation loc) throws GameActionException {
        assertCanSenseLocation(loc);
        return this.gameWorld.getRobot(loc) != null;
    }

    @Override
    public boolean canSenseRobotAtLocation(MapLocation loc) {
        try {
            return isLocationOccupied(loc);
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public RobotInfo senseRobotAtLocation(MapLocation loc) throws GameActionException {
        assertCanSenseLocation(loc);
        InternalRobot bot = this.gameWorld.getRobot(loc);
        return bot == null ? null : bot.getRobotInfo();
    }

    @Override
    public boolean canSenseRobot(int id) {
        InternalRobot sensedRobot = getRobotByID(id);
        return sensedRobot == null || !sensedRobot.isSpawned() ? false : canSenseLocation(sensedRobot.getLocation());
    }

    @Override
    public RobotInfo senseRobot(int id) throws GameActionException {
        if (!canSenseRobot(id))
            throw new GameActionException(CANT_SENSE_THAT,
                    "Can't sense given robot; It may be out of vision range or not exist anymore");
        return getRobotByID(id).getRobotInfo();
    }

    private void assertRadiusNonNegative(int radiusSquared) throws GameActionException {
        if (radiusSquared < -1) {
            throw new GameActionException(CANT_DO_THAT, "The radius for a sense command can't be negative and not -1");
        }
    }

    @Override
    public RobotInfo[] senseNearbyRobots() {
        try {
            return senseNearbyRobots(-1);
        } catch (GameActionException e) {
            return new RobotInfo[0];
        }
    }

    @Override
    public RobotInfo[] senseNearbyRobots(int radiusSquared) throws GameActionException {
        assertRadiusNonNegative(radiusSquared);
        return senseNearbyRobots(radiusSquared, null);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(int radiusSquared, Team team) throws GameActionException {
        assertRadiusNonNegative(radiusSquared);
        return senseNearbyRobots(getLocation(), radiusSquared, team);
    }

    @Override
    public RobotInfo[] senseNearbyRobots(MapLocation center, int radiusSquared, Team team) throws GameActionException {
        assertNotNull(center);
        assertIsSpawned();
        assertRadiusNonNegative(radiusSquared);
        int actualRadiusSquared = radiusSquared == -1 ? GameConstants.VISION_RADIUS_SQUARED
                : Math.min(radiusSquared, GameConstants.VISION_RADIUS_SQUARED);
        InternalRobot[] allSensedRobots = gameWorld.getAllRobotsWithinRadiusSquared(center, actualRadiusSquared, team);
        List<RobotInfo> validSensedRobots = new ArrayList<>();
        for (InternalRobot sensedRobot : allSensedRobots) {
            // check if this robot
            if (sensedRobot.equals(this.robot))
                continue;
            // check if can sense
            if (!canSenseLocation(sensedRobot.getLocation()))
                continue;
            // check if right team
            if (team != null && sensedRobot.getTeam() != team)
                continue;
            validSensedRobots.add(sensedRobot.getRobotInfo());
        }
        return validSensedRobots.toArray(new RobotInfo[validSensedRobots.size()]);
    }

    @Override
    public MapLocation[] senseNearbyCrumbs(int radiusSquared) throws GameActionException {
        assertRadiusNonNegative(radiusSquared);
        assertIsSpawned();
        int actualRadiusSquared = radiusSquared == -1 ? GameConstants.VISION_RADIUS_SQUARED
                : Math.min(radiusSquared, GameConstants.VISION_RADIUS_SQUARED);

        ArrayList<MapLocation> breadLocs = new ArrayList<>();
        for (MapLocation loc : getAllLocationsWithinRadiusSquared(getLocation(), actualRadiusSquared)) {
            if (gameWorld.getBreadAmount(loc) != 0)
                breadLocs.add(loc);
        }
        return breadLocs.toArray(new MapLocation[breadLocs.size()]);
    }

    @Override
    public boolean sensePassability(MapLocation loc) throws GameActionException {
        assertCanSenseLocation(loc);
        return this.gameWorld.isPassable(loc);
    }

    @Override
    public MapLocation[] senseNearbyRuins(int radiusSquared) throws GameActionException {
        assertRadiusNonNegative(radiusSquared);
        assertIsSpawned();
        int actualRadiusSquared = radiusSquared == -1 ? GameConstants.VISION_RADIUS_SQUARED
                : Math.min(radiusSquared, GameConstants.VISION_RADIUS_SQUARED);
        ArrayList<MapLocation> ruinInfos = new ArrayList<>();

        for (MapLocation loc : gameWorld.getAllRuins()) {
            if (loc.distanceSquaredTo(robot.getLocation()) <= actualRadiusSquared) {
                ruinInfos.add(loc);
            }
        }

        return ruinInfos.toArray(new MapLocation[ruinInfos.size()]);
    }

    @Override
    public boolean senseLegalStartingRuinPlacement(MapLocation loc) throws GameActionException {
        assertCanSenseLocation(loc);

        if (!gameWorld.isPassable(loc)) {
            return false;
        }

        boolean valid = true;

        for (MapLocation ruin : gameWorld.getAllRuins()) {
            if (ruin.distanceSquaredTo(loc) <= GameConstants.MIN_RUIN_SPACING_SQUARED) {
                valid = false;
                break;
            }
        }

        return valid;
    }

    @Override
    public MapInfo senseMapInfo(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanSenseLocation(loc);
        return getMapInfo(loc);
    }

    @Override
    public MapInfo[] senseNearbyMapInfos() {
        try {
            return senseNearbyMapInfos(-1);
        } catch (GameActionException e) {
            return new MapInfo[0];
        }
    }

    @Override
    public MapInfo[] senseNearbyMapInfos(int radiusSquared) throws GameActionException {
        assertRadiusNonNegative(radiusSquared);
        return senseNearbyMapInfos(getLocation(), radiusSquared);
    }

    @Override
    public MapInfo[] senseNearbyMapInfos(MapLocation center) throws GameActionException {
        assertNotNull(center);
        return senseNearbyMapInfos(center, -1);
    }

    @Override
    public MapInfo[] senseNearbyMapInfos(MapLocation center, int radiusSquared) throws GameActionException {
        assertNotNull(center);
        assertIsSpawned();
        assertRadiusNonNegative(radiusSquared);
        int actualRadiusSquared = radiusSquared == -1 ? GameConstants.VISION_RADIUS_SQUARED
                : Math.min(radiusSquared, GameConstants.VISION_RADIUS_SQUARED);
        MapLocation[] allSensedLocs = gameWorld.getAllLocationsWithinRadiusSquared(center, actualRadiusSquared);
        List<MapInfo> validSensedMapInfo = new ArrayList<>();
        for (MapLocation mapLoc : allSensedLocs) {
            // Can't actually sense location
            if (!canSenseLocation(mapLoc)) {
                continue;
            }
            MapInfo mapInfo = getMapInfo(mapLoc);
            validSensedMapInfo.add(mapInfo);
        }
        return validSensedMapInfo.toArray(new MapInfo[validSensedMapInfo.size()]);
    }

    @Override
    public MapLocation adjacentLocation(Direction dir) {
        return getLocation().add(dir);
    }

    @Override
    public MapLocation[] getAllLocationsWithinRadiusSquared(MapLocation center, int radiusSquared)
            throws GameActionException {
        assertNotNull(center);
        assertRadiusNonNegative(radiusSquared);
        int actualRadiusSquared = radiusSquared == -1 ? GameConstants.VISION_RADIUS_SQUARED
                : Math.min(radiusSquared, GameConstants.VISION_RADIUS_SQUARED);
        MapLocation[] possibleLocs = this.gameWorld.getAllLocationsWithinRadiusSquared(center, actualRadiusSquared);
        List<MapLocation> visibleLocs = Arrays.asList(possibleLocs).stream().filter(x -> canSenseLocation(x))
                .collect(Collectors.toList());
        return visibleLocs.toArray(new MapLocation[visibleLocs.size()]);
    }

    // ***********************************
    // ****** READINESS METHODS **********
    // ***********************************

    private void assertIsSpawned() throws GameActionException {
        if (!this.robot.isSpawned()) {
            throw new GameActionException(IS_NOT_READY,
                    "This robot is not spawned in.");
        }
    }

    @Override
    public boolean isSpawned() {
        try {
            assertIsSpawned();
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    private void assertIsActionReady() throws GameActionException {
        assertIsSpawned();
        if (!this.robot.canActCooldown())
            throw new GameActionException(IS_NOT_READY,
                    "This robot's action cooldown has not expired.");
    }

    @Override
    public boolean isActionReady() {
        try {
            assertIsActionReady();
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public int getActionCooldownTurns() {
        return this.robot.getActionCooldownTurns();
    }

    private void assertIsMovementReady() throws GameActionException {
        assertIsSpawned();
        if (!this.robot.canMoveCooldown())
            throw new GameActionException(IS_NOT_READY,
                    "This robot's movement cooldown has not expired.");
    }

    @Override
    public boolean isMovementReady() {
        try {
            assertIsMovementReady();
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public int getMovementCooldownTurns() {
        return this.robot.getMovementCooldownTurns();
    }

    // ***********************************
    // ****** MOVEMENT METHODS ***********
    // ***********************************

    private void assertCanMove(Direction dir) throws GameActionException {
        assertNotNull(dir);
        assertIsMovementReady();
        assertIsSpawned();
        MapLocation loc = adjacentLocation(dir);
        if (!onTheMap(loc))
            throw new GameActionException(OUT_OF_RANGE,
                    "Can only move to locations on the map; " + loc + " is not on the map.");
        if (isLocationOccupied(loc))
            throw new GameActionException(CANT_MOVE_THERE,
                    "Cannot move to an occupied location; " + loc + " is occupied.");
        if (!this.gameWorld.isPassable(loc))
            throw new GameActionException(CANT_MOVE_THERE,
                    "Cannot move to an impassable location; " + loc + " is impassable.");
    }

    @Override
    public boolean canMove(Direction dir) {
        try {
            assertCanMove(dir);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public void move(Direction dir) throws GameActionException {
        assertCanMove(dir);
        MapLocation nextLoc = adjacentLocation(dir);
        Team[] allSpawnZones = { null, Team.A, Team.B };
        this.robot.setLocation(nextLoc);

        int amtBread = this.gameWorld.getBreadAmount(nextLoc);
        if (amtBread != 0) {
            this.robot.addResourceAmount(amtBread);
            this.gameWorld.getMatchMaker().addClaimedResource(nextLoc);
        }
        this.gameWorld.removeBread(nextLoc);
        this.robot.addMovementCooldownTurns();

        Team nextTeam = this.gameWorld.getTeam(nextLoc);
        if (nextTeam == Team.NEUTRAL) {
            this.robot.addPaint(-GameConstants.PENALTY_NEUTRAL_TERRITORY);
        } else if (nextTeam == this.robot.getTeam().opponent()) {
            this.robot.addPaint(-GameConstants.PENALTY_ENEMY_TERRITORY);
        }

        // trap trigger methods
        for (int i = this.gameWorld.getTrapTriggers(nextLoc).size() - 1; i >= 0; i--) {
            Trap trap = this.gameWorld.getTrapTriggers(nextLoc).get(i);
            if (trap.getTeam() == this.robot.getTeam()) {
                continue;
            }
            this.robot.addTrapTrigger(trap, true);
        }
    }

    // ***********************************
    // ************ SPAWNING *************
    // ***********************************

    public MapLocation[] getAllySpawnLocations() {
        MapLocation[] allyLocations = this.gameWorld.getSpawnLocations(getTeam());
        return Arrays.copyOf(allyLocations, allyLocations.length);

    }

    // ***********************************
    // ******** BUILDING METHODS *********
    // ***********************************

    private void assertIsRobotType(UnitType type) throws GameActionException {
        if (!UnitType.isRobotType(type)){
            throw new GameActionException(CANT_DO_THAT, "Given type " + type + " is not a robot type!");
        }
    }

    private void assertIsTowerType(UnitType type) throws GameActionException{
        if (!UnitType.isTowerType(type)){
            throw new GameActionException(CANT_DO_THAT, "Given type " + type + " is not a tower type!");
        }
    }

    private void assertCanBuildRobot(UnitType type, MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanActLocation(loc, GameConstants.BUILD_ROBOT_RADIUS_SQUARED);
        assertIsActionReady();
        assertIsTowerType(this.robot.getType());
        assertIsRobotType(type);

        throw new NotImplementedException();
        // TODO not implemented
    }

    @Override
    public boolean canBuildRobot(UnitType type, MapLocation loc) {
        try {
            assertCanBuildRobot(type, loc);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public void buildRobot(UnitType type, MapLocation loc) throws GameActionException {
        assertCanBuildRobot(type, loc);
        this.robot.addActionCooldownTurns(GameConstants.BUILD_ROBOT_COOLDOWN);
        this.robot.buildRobot(type, loc);
    }

    private void assertCanMarkTowerPattern(UnitType type, MapLocation loc) throws GameActionException {
        assertIsRobotType(this.robot.getType());
        assertIsTowerType(type);
        throw new NotImplementedException();
        // TODO not implemented
    }

    @Override
    public boolean canMarkTowerPattern(UnitType type, MapLocation loc) {
        try {
            assertCanMarkTowerPattern(type, loc);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public void markTowerPattern(UnitType type, MapLocation loc) {
        throw new NotImplementedException();
        // TODO not implemented
    }

    private void assertCanMarkResourcePattern(MapLocation loc) throws GameActionException {
        assertIsRobotType(this.robot.getType());

        if (loc.x < GameConstants.PATTERN_SIZE / 2
                || loc.y < GameConstants.PATTERN_SIZE / 2
                || loc.x >= getMapWidth() - GameConstants.PATTERN_SIZE / 2
                || loc.y >= getMapHeight() - GameConstants.PATTERN_SIZE / 2) {
            throw new GameActionException(CANT_DO_THAT,
                    "Cannot mark resource pattern centered at (" + loc.x + ", " + loc.y
                            + ") because it is too close to the edge of the map");
        }
    }

    @Override
    public boolean canMarkResourcePattern(MapLocation loc) {
        try {
            assertCanMarkResourcePattern(loc);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public void markResourcePattern(MapLocation loc) {
        throw new NotImplementedException();
        // TODO not implemented
    }

    // *****************************
    // ****** ATTACK / HEAL ********
    // *****************************

    private void assertCanAttack(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanActLocation(loc, GameConstants.ATTACK_RADIUS_SQUARED);
        assertIsActionReady();
        InternalRobot bot = gameWorld.getRobot(loc);
        if (bot == null || bot.getTeam() == this.getTeam()) {
            throw new GameActionException(CANT_DO_THAT, "No enemy robot to attack at this location");
        }
        if (this.robot.hasFlag()) {
            throw new GameActionException(CANT_DO_THAT, "Can't attack while holding a flag");
        }
        if (gameWorld.isSetupPhase()) {
            throw new GameActionException(CANT_DO_THAT, "Cannot attack during setup phase");
        }
    }

    @Override
    public int getAttackDamage() {
        return this.robot.getDamage();
    }

    @Override
    public boolean canAttack(MapLocation loc) {
        try {
            assertCanAttack(loc);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public void attack(MapLocation loc) throws GameActionException {
        assertCanAttack(loc);
        this.robot.addActionCooldownTurns((int) Math.round(GameConstants.ATTACK_COOLDOWN
                * (1 + .01 * SkillType.ATTACK.getCooldown(this.robot.getLevel(SkillType.ATTACK)))));
        this.robot.attack(loc);
    }

    @Override
    public int getHealAmount() {
        return this.robot.getHeal();
    }

    private void assertCanHeal(MapLocation loc) throws GameActionException {
        assertNotNull(loc);
        assertCanActLocation(loc, GameConstants.HEAL_RADIUS_SQUARED);
        assertIsActionReady();
        if (getLocation().equals(loc)) {
            throw new GameActionException(CANT_DO_THAT, "You can't heal yourself");
        }
        if (this.gameWorld.getRobot(loc) == null) {
            throw new GameActionException(CANT_DO_THAT, "There is no robot at this location.");
        }
        if (this.gameWorld.getRobot(loc).getTeam() != this.getTeam()) {
            throw new GameActionException(CANT_DO_THAT, "The robot at this location is the other team.");
        }
        if (this.gameWorld.getRobot(loc).getHealth() == GameConstants.DEFAULT_HEALTH) {
            throw new GameActionException(CANT_DO_THAT, "The robot at this location is at full health.");
        }
        if (this.robot.hasFlag()) {
            throw new GameActionException(CANT_DO_THAT, "Can't heal while holding a flag");
        }
    }

    @Override
    public boolean canHeal(MapLocation loc) {
        try {
            assertCanHeal(loc);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    public void heal(MapLocation loc) throws GameActionException {
        assertCanHeal(loc);
        InternalRobot bot = this.gameWorld.getRobot(loc);
        int healAmt = this.robot.getHeal();
        this.robot.addActionCooldownTurns((int) Math.round(GameConstants.HEAL_COOLDOWN
                * (1 + .01 * SkillType.HEAL.getCooldown(this.robot.getLevel(SkillType.HEAL)))));

        bot.addHealth(healAmt);
        this.robot.incrementSkill(SkillType.HEAL);
        this.gameWorld.getMatchMaker().addAction(getID(), Action.HEAL, bot.getID());
    }

    // ***********************************
    // ****** COMMUNICATION METHODS ******
    // ***********************************

    private void assertValidIndex(int index) throws GameActionException {
        if (index < 0 || index >= GameConstants.SHARED_ARRAY_LENGTH)
            throw new GameActionException(CANT_DO_THAT,
                    "You can't access this index as it is not within the shared array.");
    }

    private void assertValidValue(int value) throws GameActionException {
        if (value < 0 || value > GameConstants.MAX_SHARED_ARRAY_VALUE)
            throw new GameActionException(CANT_DO_THAT, "You can't write this value to the shared array " +
                    "as it is not within the range of allowable values: [0, " + GameConstants.MAX_SHARED_ARRAY_VALUE
                    + "].");
    }

    @Override
    public int readSharedArray(int index) throws GameActionException {
        assertValidIndex(index);
        return this.gameWorld.getTeamInfo().readSharedArray(getTeam(), index);
    }

    private void assertCanWriteSharedArray(int index, int value) throws GameActionException {
        assertValidIndex(index);
        assertValidValue(value);
    }

    @Override
    public boolean canWriteSharedArray(int index, int value) {
        try {
            assertCanWriteSharedArray(index, value);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public void writeSharedArray(int index, int value) throws GameActionException {
        assertCanWriteSharedArray(index, value);
        this.gameWorld.getTeamInfo().writeSharedArray(getTeam(), index, value);
    }

    @Override
    private void assertCanSendMessage(MapLocation loc, Message message) throws GameActionException {
        assertNotNull(loc);
        assertCanActLocation(loc, GameConstants.MESSAGE_RADIUS_SQUARED);
        assertNotNull(this.gameWorld.getRobot(loc));
        assert (getTeam() == this.gameWorld.getRobot(loc).getTeam());
        assertNotNull(message);
        if (true) { // TODO: replace this with isRobot (as opposed to isTower)
            assert (this.robot.getSentMessagesCount() < GameConstants.MAX_MESSAGES_SENT_ROBOT);
        } else {
            assert (this.robot.getSentMessagesCount() < GameConstants.MAX_MESSAGES_SENT_TOWER);
        }
        // TODO: assert that the distance between the robots is < sqrt(20?) and they are
        // connected by paint once that functionality is available
        // TODO: assert that robot -> tower and tower -> robot only
    }

    @Override
    public boolean canSendMessage(MapLocation loc, int messageContent) {
        try {
            Message message = new Message(messageContent, this.robot.getID(), this.gameWorld.getCurrentRound());
            assertCanSendMessage(loc, message);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public void sendMessage(MapLocation loc, int messageContent) throws GameActionException {
        Message message = new Message(messageContent, this.robot.getID(), this.gameWorld.getCurrentRound());
        assertCanSendMessage(loc, message);
        InternalRobot robot = this.gameWorld.getRobot(loc);
        this.robot.sendMessage(robot, message);
    }

    // ***********************************
    // ****** OTHER ACTION METHODS *******
    // ***********************************

    private void assertCanTransferPaint(MapLocation loc, int amount) throws GameActionException {
        assertNotNull(loc);
        assertCanActLocation(loc, GameConstants.PAINT_TRANSFER_RADIUS_SQUARED);
        assertNotNull(robot);
        assertIsActionReady();
        InternalRobot robot = this.gameWorld.getRobot(loc);
        if (loc == this.robot.getLocation()) {
            throw new GameActionException(CANT_DO_THAT, "Cannot transfer paint to yourself!");
        }
        if (amount == 0) {
            throw new GameActionException(CANT_DO_THAT, "Cannot transfer zero paint!");
        }
        if (robot.getTeam() != this.robot.getTeam()) {
            throw new GameActionException(CANT_DO_THAT, "Cannot transfer resources to the enemy team!");
        }
        if (UnitType.isTowerType(this.robot.getType())) {
            throw new GameActionException(CANT_DO_THAT, "Towers cannot transfer paint!");
        }
        if (amount > 0 && this.robot.getType() != UnitType.MOPPER) {
            throw new GameActionException(CANT_DO_THAT, "Only mopppers can give paint to allies!");
        }
        if (UnitType.isRobotType(robot.getType()) && amount < 0) {
            throw new GameActionException(CANT_DO_THAT, "Moppers can only give paint to ally robots!");
        }
        if (-1 * amount > this.robot.getPaint()) {
            throw new GameActionException(CANT_DO_THAT, "Cannot take more paint from towers than they currently have!");
        }
        if (amount > this.robot.getPaint()) {
            throw new GameActionException(CANT_DO_THAT, "Cannot give more paint than you currently have!");
        }
    }

    public boolean canTransferPaint(MapLocation loc, int amount) {
        try {
            assertCanTransferPaint(loc, amount);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    public void transferPaint(MapLocation loc, int amount) throws GameActionException {
        assertCanTransferPaint(loc, amount);
        this.robot.addPaint(-1 * amount);
        InternalRobot robot = this.gameWorld.getRobot(loc);
        robot.addPaint(amount);
        this.robot.addActionCooldownTurns(GameConstants.PAINT_TRANSFER_COOLDOWN);
    }

    private void assertCanBuyGlobal(GlobalUpgrade ug) throws GameActionException {
        int i = -1;
        if (ug == GlobalUpgrade.ATTACK || ug == GlobalUpgrade.ACTION)
            i = 0;
        else if (ug == GlobalUpgrade.CAPTURING)
            i = 1;
        else if (ug == GlobalUpgrade.HEALING)
            i = 2;
        boolean hasBought = this.gameWorld.getTeamInfo().getGlobalUpgrades(getTeam())[i];
        if (hasBought)
            throw new GameActionException(CANT_DO_THAT, "Cannot buy an upgrade you already have!");
        if (this.gameWorld.getTeamInfo().getGlobalUpgradePoints(getTeam()) <= 0)
            throw new GameActionException(CANT_DO_THAT, "Cannot buy an upgrade with no global upgrade points!");
    }

    @Override
    public boolean canBuyGlobal(GlobalUpgrade ug) {
        try {
            assertCanBuyGlobal(ug);
            return true;
        } catch (GameActionException e) {
            return false;
        }
    }

    @Override
    public void buyGlobal(GlobalUpgrade ug) throws GameActionException {
        assertCanBuyGlobal(ug);
        this.gameWorld.getTeamInfo().makeGlobalUpgrade(getTeam(), ug);
        this.gameWorld.getMatchMaker().addAction(getID(), Action.GLOBAL_UPGRADE,
                FlatHelpers.getGlobalUpgradeTypeFromGlobalUpgrade(ug));
    }

    @Override
    public GlobalUpgrade[] getGlobalUpgrades(Team team) {
        boolean[] boolUpgrades = this.gameWorld.getTeamInfo().getGlobalUpgrades(team);
        ArrayList<GlobalUpgrade> upgrades = new ArrayList<>();
        if (boolUpgrades[0])
            upgrades.add(GlobalUpgrade.ATTACK);
        if (boolUpgrades[1])
            upgrades.add(GlobalUpgrade.CAPTURING);
        if (boolUpgrades[2])
            upgrades.add(GlobalUpgrade.HEALING);
        return upgrades.toArray(new GlobalUpgrade[upgrades.size()]);
    }

    @Override
    public void resign() {
        Team team = getTeam();
        gameWorld.getObjectInfo().eachRobot((robot) -> {
            if (robot.getTeam() == team) {
                gameWorld.destroyRobot(robot.getID());
            }
            return true;
        });
        gameWorld.setWinner(team.opponent(), DominationFactor.RESIGNATION);
    }

    // ***********************************
    // ******** DEBUG METHODS ************
    // ***********************************

    @Override
    public void setIndicatorString(String string) {
        if (string.length() > GameConstants.INDICATOR_STRING_MAX_LENGTH) {
            string = string.substring(0, GameConstants.INDICATOR_STRING_MAX_LENGTH);
        }
        this.robot.setIndicatorString(string);
    }

    @Override
    public void setIndicatorDot(MapLocation loc, int red, int green, int blue) {
        assertNotNull(loc);
        this.gameWorld.getMatchMaker().addIndicatorDot(getID(), loc, red, green, blue);
    }

    @Override
    public void setIndicatorLine(MapLocation startLoc, MapLocation endLoc, int red, int green, int blue) {
        assertNotNull(startLoc);
        assertNotNull(endLoc);
        this.gameWorld.getMatchMaker().addIndicatorLine(getID(), startLoc, endLoc, red, green, blue);
    }
}
