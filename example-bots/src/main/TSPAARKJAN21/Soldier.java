package TSPAARKJAN21;

import battlecode.common.*;

public class Soldier {
    public static final int EXPLORE = 0;
    public static final int BUILD_TOWER = 1;
    public static final int BUILD_RESOURCE = 2;
    public static final int EXPAND_RESOURCE = 3;
    public static final int ATTACK = 4;
    public static final int RETREAT = 5;
    public static int mode = EXPLORE;

    // ratio to reduce retreat requirement by if building tower/srp
    public static final double SOL_RETREAT_REDUCED_RATIO = 0.5;
    // exploration weight multiplier
    public static final int SOL_EXPLORE_OPP_WEIGHT = 5; // tested: 3 (45/94), 4 (44/94), 6 (47/94)
    // controls rounds between visiting ruins
    public static final int SOL_RUIN_VISIT_TIMEOUT_BASE = -100;
    // increase timeout based on number of towers and map area
    public static final int SOL_RUIN_VISIT_TIMEOUT_TOW_INCREASE = 80;
    public static final double SOL_RUIN_VISIT_TIMEOUT_MAP_INCREASE = 0.2;
    // controls ratio of money to paint (higher = more money)
    public static final double SOL_MONEY_PAINT_TOWER_RATIO = 1.0;
    // stop building towers if enemy paint interferes too much
    public static final int SOL_MAX_TOWER_ENEMY_PAINT = 8;
    public static final int SOL_MAX_TOWER_ENEMY_PAINT_NO_HELP = 1;
    public static final int SOL_MAX_TOWER_ENEMY_PAINT_HARD = 8; // tested: 12 against 16 (52/94), 8 against 12 (53/94)
    public static final int SOL_TOWER_HELP_DIST = 5;
    public static final int SOL_MAX_TOWER_BLOCKED_TIME = 30;
    // max soldiers that will build a tower at a time
    public static final int SOL_MAX_TOWER_BUILDING_SOLDIERS = 3;
    // max build time
    public static final int SOL_MAX_TOWER_TIME = 200;
    // don't build SRP immediately after spawning or in early game
    public static final int SOL_MIN_SRP_ROUND = 20;
    public static final int SOL_SPAWN_SRP_MIN_ROUNDS = 10;
    // controls rounds between repairing/expanding an SRP
    public static final int SOL_SRP_VISIT_TIMEOUT = 100;
    public static final double SOL_SRP_VISIT_TIMEOUT_MAP_INCREASE = 0.5;
    // balance exploring and building SRPs (don't SRP if near target)
    public static final int SOL_SRP_EXPAND_TIMEOUT = 20;
    public static final int SOL_SRP_EXP_OVERRIDE_DIST = 100;
    // stop building SRP if enemy paint interferes too much
    public static final int SOL_MAX_SRP_ENEMY_PAINT = 1;
    public static final int SOL_MAX_SRP_BLOCKED_TIME = 5;
    public static final int SOL_MAX_SRP_ENEMY_PAINT_HARD = 8;
    // max build time for SRP
    public static final int SOL_MAX_SRP_TIME = 50;
    // max time spent traveling to an SRP target (obstructed by bot / inaccessible)
    public static final int SOL_MAX_SRP_TARGET_TIME = 15;
    // don't build SRP if not enough paint (runs out quickly)
    public static final int SOL_SRP_MIN_PAINT = 75;
    // dont run out of paint waiting for paint painting area
    public static final int SOL_RETREAT_PAINT_MIN_PAINT = 20;

    public static MapLocation exploreLocation = null; // EXPLORE mode
    public static MapLocation ruinLocation = null; // BUILD_TOWER mode
    public static int buildTowerType = 0;
    public static UnitType towerType = null; // ATTACK mode
    public static MapLocation towerLocation = null; // ATTACK mode
    public static MapLocation resourceLocation = null; // BUILD_RESOURCE mode

    public static boolean reducedRetreating = false;
    public static boolean avoidRetreating = false;

    // queue of next locations to check for expanding SRP
    // used in explore mode to mark initial build since needs centered for markers
    // (goes into expand mode, reaches the target location, and starts building)
    public static MapLocation[] srpCheckLocations = new MapLocation[] {};
    public static int srpCheckIndex = 0;
    public static int lastSrpExpansion = -SOL_SRP_EXPAND_TIMEOUT + SOL_SPAWN_SRP_MIN_ROUNDS;
    // cache using 2d boolean array using bitshifting
    public static long[] disqualifiedSrpLocations = new long[64];

    public static int buildBlockedTime = 0;
    public static int buildTime = 0;
    public static int srpTargetTime = 0;

    // commonly used stuff
    // map nearby map infos into 2d array in (y, x) form
    // used for tower building, SRP detection, expansion, building
    public static MapInfo[][] mapInfos = new MapInfo[9][9];

    /**
     * Always:
     * If low on paint (reduceRetreating halves paint), retreat
     * Default to explore mode
     * Most motion will attempt to paint under self to reduce passive paint drain
     * 
     * Explore:
     * Run around randomly while painting below self, pick towers from POI
     * If seeing opponent tower or ruin, go to attack/tower build mode
     * - tower build mode then checks if actually needed, may switch back to explore
     * If existing SRP found, enter SRP expand mode
     * If can build, queue location and enter SRP expand mode
     * 
     * Build tower:
     * Automatically make reducedRetreating true
     * If pattern is complete but tower not completed, leave lowest ID to complete
     * - avoidRetreating true if is lowest ID, don't abandon the tower
     * Place stuff
     * If tower pattern obstructed by enemy paint long enougn, return to explore
     * If building for too long, return to explore
     * Complete tower, return to explore
     * 
     * Build SRP:
     * Automatically make reducedRetreating true
     * Place SRP
     * If SRP obstructed by enemy paint long enougn, return to explore
     * If building for too long, return to explore
     * Complete SRP, queue 16 expansion locations and enter SRP expand mode
     * - Not as optimal anymore, sad
     * 
     * Expand SRP:
     * Go to queued locations of expansion and see if can build (race conditions)
     * - SRPs won't be built overlapping with ruin 5x5 squares (but can with towers)
     * - Tiling is checked
     * If trying to reach expand target for too long, return to explore
     * If can build
     * - Place secondary marker at center
     * - Enter SRP build mode
     * 
     * Attack:
     * Attack tower until ded lmao
     * Attempt to repair SRPs if not in range to attack tower
     * 
     * Retreat:
     * Use Robot retreat
     * Try to paint under self when near tower
     */
    public static void run() throws Exception {
        // somewhat spaghetti fix for soldiers
        if (G.round == G.roundSpawned)
            lastSrpExpansion = G.roundSpawned - SOL_SRP_EXPAND_TIMEOUT + SOL_SPAWN_SRP_MIN_ROUNDS;
        if (mode == RETREAT) {
            Motion.tryTransferPaint();
        }
        if (!avoidRetreating
                && G.rc.getPaint() < Motion.getRetreatPaint() * (reducedRetreating ? SOL_RETREAT_REDUCED_RATIO : 1)
                && G.maxChips < 6000
                && G.allyRobots.length < 9) {
            mode = RETREAT;
            if (ruinLocation != null) {
                G.setLastVisited(ruinLocation, -2000);
            }
            if (resourceLocation != null) {
                G.setLastVisited(resourceLocation, -2000);
            }
        } else if (mode == RETREAT && G.rc.getPaint() > Motion.paintNeededToStopRetreating) {
            mode = EXPLORE;
            Motion.retreatTower = -1;
        }
        // map mapinfos infos
        int miDx = 4 - G.me.x, miDy = 4 - G.me.y;
        for (int i = G.nearbyMapInfos.length; --i >= 0;) {
            MapLocation loc = G.nearbyMapInfos[i].getMapLocation().translate(miDx, miDy);
            mapInfos[loc.y][loc.x] = G.nearbyMapInfos[i];
        }
        int a = Clock.getBytecodeNum();
        reducedRetreating = false;
        avoidRetreating = false;
        switch (mode) {
            case EXPLORE -> exploreCheckMode();
            case BUILD_TOWER -> buildTowerCheckMode();
            case BUILD_RESOURCE -> buildResourceCheckMode();
            case EXPAND_RESOURCE -> expandResourceCheckMode();
            case ATTACK -> attackCheckMode();
            case RETREAT -> {
                // VERY IMPORTANT DO NOT TOUCH
                buildBlockedTime = 0;
                buildTime = 0;
                srpTargetTime = 0;
                Motion.setRetreatLoc();
                if (Motion.retreatTower == -1) {
                    mode = EXPLORE;
                    exploreCheckMode();
                }
            }
        }
        int b = Clock.getBytecodeNum();
        G.indicatorString.append((b - a) + " ");
        switch (mode) {
            case EXPLORE -> explore();
            case BUILD_TOWER -> buildTower();
            case BUILD_RESOURCE -> buildResource();
            case EXPAND_RESOURCE -> expandResource();
            case ATTACK -> attack();
            case RETREAT -> {
                G.indicatorString.append("RETREAT ");
                if (G.rc.getPaint() > SOL_RETREAT_PAINT_MIN_PAINT && Motion.retreatTower >= 0
                        && G.me.isWithinDistanceSquared(POI.towerLocs[Motion.retreatTower], 8))
                    Motion.retreat(moveWithPaintMicro);
                else
                    Motion.retreat();
                Motion.tryTransferPaint();
                // G.rc.setIndicatorDot(G.me, 255, 0, 255);
            }
        }
        G.indicatorString.append((Clock.getBytecodeNum() - b) + " ");
    }

    public static void exploreCheckMode() throws Exception {
        G.indicatorString.append("CHK_E ");
        // VERY IMPORTANT DO NOT TOUCH
        buildBlockedTime = 0;
        buildTime = 0;
        srpTargetTime = 0;
        // dont build tower that bot was just building
        final double towerVisitTimeout = SOL_RUIN_VISIT_TIMEOUT_BASE
                + SOL_RUIN_VISIT_TIMEOUT_MAP_INCREASE * G.mapArea
                + SOL_RUIN_VISIT_TIMEOUT_TOW_INCREASE * G.rc.getNumberTowers();
        // switch modes if seeing towers/ruins
        for (int i = G.nearbyRuins.length; --i >= 0;) {
            MapLocation loc = G.nearbyRuins[i];
            if (G.rc.canSenseRobotAtLocation(loc)) {
                RobotInfo bot = G.rc.senseRobotAtLocation(loc);
                if (bot.team == G.opponentTeam) {
                    towerLocation = loc;
                    towerType = bot.type;
                    mode = ATTACK;
                    return;
                }
            } else if (G.getLastVisited(loc) + towerVisitTimeout < G.round) {
                ruinLocation = loc;
                mode = BUILD_TOWER;
                // if the tower doesn't need more help it'll return to explore mode
                // need to do this because of lowest ID check - bot may leave if other bot with
                // lower ID comes, but may not swap roles to build the tower
                buildTowerCheckMode();
                return;
            }
        }
        if (lastSrpExpansion + SOL_SRP_EXPAND_TIMEOUT >= G.round || (exploreLocation != null
                && G.me.isWithinDistanceSquared(exploreLocation, SOL_SRP_EXP_OVERRIDE_DIST))) {
            G.indicatorString.append("SKIP_CHK_RP:" + lastSrpExpansion + ":" + exploreLocation + " ");
        } else if (G.rc.getPaint() > SOL_SRP_MIN_PAINT) {
            // scan for SRP centers nearby to repair
            final double srpVisitTimeout = SOL_SRP_VISIT_TIMEOUT + SOL_SRP_VISIT_TIMEOUT_MAP_INCREASE * G.mapArea;
            MapInfo info;
            for (int i = G.nearbyMapInfos.length; --i >= 0;) {
                info = G.nearbyMapInfos[i];
                if (!info.isResourcePatternCenter()
                        && G.getLastVisited(info.getMapLocation()) + srpVisitTimeout < G.round
                        && info.getMark() == PaintType.ALLY_SECONDARY) {
                    srpCheckLocations = new MapLocation[] { info.getMapLocation() };
                    srpCheckIndex = 0;
                    mode = EXPAND_RESOURCE;
                    // do this or bugs
                    expandResourceCheckMode();
                    return;
                }
            }
            // dont try near edge of map (less bytecode, FIXES CRASHES TOO)
            if (G.me.x < 1 || G.me.x > G.mapWidth - 2 || G.me.y < 1 || G.me.y > G.mapWidth - 2) {
                return;
            }
            // see if SRP is possible nearby
            if (G.round > SOL_MIN_SRP_ROUND) {
                for (int i = 8; --i >= 0;) {
                    MapLocation loc = G.me.add(G.ALL_DIRECTIONS[i]);
                    if (G.getLastVisited(loc) + srpVisitTimeout < G.round && canBuildSrpAtLocation(loc)) {
                        srpCheckLocations = new MapLocation[] { loc };
                        srpCheckIndex = 0;
                        mode = EXPAND_RESOURCE;
                        // do this or bugs
                        expandResourceCheckMode();
                        return;
                    }
                }
            }
        }
    }

    public static void buildTowerCheckMode() throws Exception {
        G.indicatorString.append("CHK_BTW ");
        reducedRetreating = true;
        G.setLastVisited(ruinLocation, G.round);
        // if been building for a long time stop
        if (buildTime > SOL_MAX_TOWER_TIME) {
            mode = EXPLORE;
            return;
        }
        buildTime++;
        buildTowerType = predictTowerType(ruinLocation);
        // if tower already built leave tower build mode
        if (!G.rc.canSenseLocation(ruinLocation) || G.rc.canSenseRobotAtLocation(ruinLocation)
                || G.rc.getNumberTowers() == 25) {
            mode = EXPLORE;
            return;
        }
        // if too many soldiers trying to build, we can leave
        MapLocation loc;
        int otherSoldiers = 0;
        if (G.me.isWithinDistanceSquared(ruinLocation, 1)) {
            for (int i = 8; --i >= 0;) {
                loc = ruinLocation.add(G.DIRECTIONS[i]);
                if (G.rc.canSenseRobotAtLocation(loc) && G.rc.senseRobotAtLocation(loc).type == UnitType.SOLDIER) {
                    otherSoldiers++;
                    if (otherSoldiers > SOL_MAX_TOWER_BUILDING_SOLDIERS) {
                        mode = EXPLORE;
                        return;
                    }
                }
            }
        }
        // check for moppers (used later)
        boolean hasHelp = false;
        for (int i = G.allyRobots.length; --i >= 0;) {
            if (G.allyRobots[i].type == UnitType.MOPPER
                    && G.allyRobots[i].location.isWithinDistanceSquared(G.me, SOL_TOWER_HELP_DIST)) {
                hasHelp = true;
                break;
            }
        }
        final int maxEnemyPaint = hasHelp ? SOL_MAX_TOWER_ENEMY_PAINT : SOL_MAX_TOWER_ENEMY_PAINT_NO_HELP;
        int enemyPaint = 0;
        int incorrectPaint = 0;
        int ox = ruinLocation.x - G.me.x + 2;
        int oy = ruinLocation.y - G.me.y + 2;
        boolean[][] pattern = Robot.towerPatterns[buildTowerType];
        PaintType curr;
        for (int dx = -1; dx++ < 4;) {
            for (int dy = -1; dy++ < 4;) {
                if (dx == 2 && dy == 2)
                    continue;
                // make sure not out of vision radius
                if (G.rc.canSenseLocation(ruinLocation.translate(dx - 2, dy - 2))) {
                    curr = mapInfos[oy + dy][ox + dx].getPaint();
                    if (curr != (pattern[dx][dy] ? PaintType.ALLY_SECONDARY : PaintType.ALLY_PRIMARY))
                        incorrectPaint++;
                    // stop building if there's lots of enemy paint
                    if (curr.isEnemy()) {
                        enemyPaint++;
                        if (enemyPaint >= SOL_MAX_TOWER_ENEMY_PAINT_HARD) {
                            G.indicatorString.append("BLOCK-H ");
                            // immediately give up if there's way too much paint
                            mode = EXPLORE;
                            return;
                        }
                    }
                }
            }
        }
        // check blocked by enemy paint
        if (enemyPaint >= maxEnemyPaint) {
            buildBlockedTime++;
            G.indicatorString.append("BLOCK ");
            // if pattern has been blocked for a long time just give up
            if (buildBlockedTime > SOL_MAX_TOWER_BLOCKED_TIME) {
                mode = EXPLORE;
                return;
            }
        } else {
            buildBlockedTime = 0;
        }
        if (incorrectPaint == 0) {
            G.indicatorString.append("COMPLETE ");
            // if pattern complete leave lowest bot ID to complete
            for (int i = G.allyRobots.length; --i >= 0;) {
                if (G.allyRobots[i].type == UnitType.SOLDIER
                        && G.allyRobots[i].getLocation().isWithinDistanceSquared(ruinLocation, 8)) {
                    if (G.allyRobots[i].ID < G.rc.getID()) {
                        // not lowest ID, leave
                        mode = EXPLORE;
                        return;
                    }
                }
            }
            // is lowest id
            avoidRetreating = true;
        } else if (enemyPaint == 0) {
            // if soldiers at tower have enough paint to complete pattern, DO NOT RETREAT
            RobotInfo r;
            int totalPaint = 0;
            Direction[] cardinals = Direction.cardinalDirections();
            for (int i = 4; --i >= 0;) {
                loc = ruinLocation.add(cardinals[i]);
                if (!G.me.equals(loc) && G.rc.canSenseRobotAtLocation(loc)) {
                    r = G.rc.senseRobotAtLocation(loc);
                    if (r.type == UnitType.SOLDIER)
                        totalPaint += (r.paintAmount - 1) / UnitType.SOLDIER.attackCost;
                }
            }
            totalPaint += G.rc.getPaint() / UnitType.SOLDIER.attackCost;
            G.indicatorString.append("TP=" + totalPaint + ",IP=" + incorrectPaint + " ");
            if (totalPaint >= incorrectPaint)
                avoidRetreating = true;
        } else {
            G.indicatorString.append("EP=" + enemyPaint + ",IP=" + incorrectPaint + " ");
        }
    }

    public static void buildResourceCheckMode() throws Exception {
        G.indicatorString.append("CHK_BRP ");
        reducedRetreating = true;
        // shouldn't interfere with towers here either, same as expand RP
        G.setLastVisited(resourceLocation, G.round);
        // if been building for a long time stop
        if (buildTime > SOL_MAX_SRP_TIME) {
            mode = EXPLORE;
            return;
        }
        buildTime++;
        // if the SRP has been blocked for a long time just give up
        // stop building if there's lots of enemy paint within the SRP
        int enemyPaint = 0;
        int incorrectPaint = 0;
        int ox = resourceLocation.x - G.me.x + 2;
        int oy = resourceLocation.y - G.me.y + 2;
        PaintType curr;
        for (int dx = -1; dx++ < 4;) {
            for (int dy = -1; dy++ < 4;) {
                // make sure not out of vision radius
                if (G.rc.canSenseLocation(resourceLocation.translate(dx - 2, dy - 2))) {
                    curr = mapInfos[oy + dy][ox + dx].getPaint();
                    if (curr != (Robot.resourcePattern[dx][dy] ? PaintType.ALLY_SECONDARY : PaintType.ALLY_PRIMARY))
                        incorrectPaint++;
                    // stop building if there's lots of enemy paint
                    if (curr.isEnemy()) {
                        enemyPaint++;
                        if (enemyPaint >= SOL_MAX_SRP_ENEMY_PAINT_HARD) {
                            G.indicatorString.append("BLOCK-H ");
                            // immediately give up if there's way too much paint
                            mode = EXPLORE;
                            return;
                        }
                    }
                }
            }
        }
        // check blocked by enemy paint
        if (enemyPaint >= SOL_MAX_SRP_ENEMY_PAINT) {
            buildBlockedTime++;
            G.indicatorString.append("BLOCK ");
            // if pattern has been blocked for a long time just give up
            if (buildBlockedTime > SOL_MAX_SRP_BLOCKED_TIME) {
                mode = EXPLORE;
                return;
            }
        } else {
            buildBlockedTime = 0;
        }
        // don't retreat if enough paint to finish building
        if (enemyPaint == 0 && G.rc.getPaint() / UnitType.SOLDIER.attackCost >= incorrectPaint)
            avoidRetreating = true;
    }

    public static void expandResourceCheckMode() throws Exception {
        G.indicatorString.append("CHK_ERP ");
        // if been trying to reach target for a long time stop (inaccessible/occupied)
        if (srpTargetTime > SOL_MAX_SRP_TARGET_TIME) {
            mode = EXPLORE;
            return;
        }
        srpTargetTime++;
        MapLocation target = srpCheckLocations[srpCheckIndex];
        // keep disqualifying locations in a loop
        // done ASAP, don't waste time going to SRPs that can be disqualified
        final double visitTimeout = SOL_SRP_VISIT_TIMEOUT + SOL_SRP_VISIT_TIMEOUT_MAP_INCREASE * G.mapArea;
        while (!G.rc.onTheMap(target) || G.getLastVisited(target) + visitTimeout >= G.round
                || cannotBuildSrpAtLocation(target)) {
            if (G.rc.onTheMap(target))
                // G.rc.setIndicatorDot(target, 255, 100, 0);
            if (++srpCheckIndex >= srpCheckLocations.length) {
                mode = EXPLORE;
                // don't waste turns
                if (Clock.getBytecodesLeft() > 10000)
                    exploreCheckMode();
                return;
            }
            target = srpCheckLocations[srpCheckIndex];
            // reset target time when disqualifying target
            srpTargetTime = 0;
        }
        // markers
        if (G.me.equals(target) && canBuildSrpAtLocation(G.me)) {
            resourceLocation = G.me;
            // only place one marker
            G.rc.mark(resourceLocation, true);
            // G.rc.setIndicatorDot(resourceLocation, 255, 200, 0);
            G.indicatorString.append("MK_SRP ");
            mode = BUILD_RESOURCE;
        }
    }

    public static void attackCheckMode() throws Exception {
        G.indicatorString.append("CHK_ATK ");
        // oh wait it's friendly tower
        if (G.rc.canSenseLocation(towerLocation) && (!G.rc.canSenseRobotAtLocation(towerLocation)
                || G.rc.senseRobotAtLocation(towerLocation).team == G.team)) {
            mode = EXPLORE;
            exploreCheckMode();
        }
    }

    public static void explore() throws Exception {
        G.indicatorString.append("EXPLORE ");
        // here instead of checkMode since checkMode may skip this if tower/SRP checked
        // find towers from POI to attack/build out of vision
        exploreLocation = null;
        int bestDistanceSquared = 10000;
        // to avoid too much POI, increase timeout based on map size & tower count
        final double visitTimeout = SOL_RUIN_VISIT_TIMEOUT_BASE
                + SOL_RUIN_VISIT_TIMEOUT_MAP_INCREASE * G.mapArea
                + SOL_RUIN_VISIT_TIMEOUT_TOW_INCREASE * G.rc.getNumberTowers();
        for (int i = POI.numberOfTowers; --i >= 0;) {
            if (POI.towerTeams[i] == G.opponentTeam) {
                // attack these
                MapLocation pos = POI.towerLocs[i];
                if (G.me.isWithinDistanceSquared(pos, bestDistanceSquared)
                        && G.getLastVisited(pos) + visitTimeout < G.round) {
                    bestDistanceSquared = G.me.distanceSquaredTo(pos);
                    exploreLocation = pos;
                }
            } else if (POI.towerTeams[i] == Team.NEUTRAL && G.rc.getNumberTowers() < 25) {
                // } else if (POI.towerTeams[i] == Team.NEUTRAL && G.rc.getNumberTowers() < 8) {
                // having 25 towers otherwise just softlocks the bots
                MapLocation pos = POI.towerLocs[i];
                // prioritize opponent towers more than ruins, so it has to be REALLY close
                if (G.me.isWithinDistanceSquared(pos, bestDistanceSquared / SOL_EXPLORE_OPP_WEIGHT)
                        && G.getLastVisited(pos) + visitTimeout < G.round) {
                    bestDistanceSquared = G.me.distanceSquaredTo(pos) * SOL_EXPLORE_OPP_WEIGHT; // lol
                    exploreLocation = pos;
                }
            }
        }
        if (exploreLocation == null) {
            Motion.exploreRandomly(moveWithPaintMicro);
        } else {
            Motion.bugnavTowards(exploreLocation, moveWithPaintMicro);
            // G.rc.setIndicatorLine(G.me, exploreLocation, 255, 255, 0);
        }
        // G.rc.setIndicatorDot(G.me, 0, 255, 0);
    }

    public static void buildTower() throws Exception {
        G.indicatorString.append("BUILD_TW ");
        G.indicatorString.append("TYPE=" + buildTowerType + " ");
        // move first, then paint, helps avoid passive paint drain
        if (G.rc.canCompleteTowerPattern(Robot.towers[buildTowerType], ruinLocation)) {
            G.rc.completeTowerPattern(Robot.towers[buildTowerType], ruinLocation);
            POI.addTower(-1, ruinLocation, G.team, Robot.towers[buildTowerType]);
            mode = EXPLORE;
            Motion.exploreRandomly(moveWithPaintMicro);
            // dot to signal building complete
            // G.rc.setIndicatorDot(ruinLocation, 255, 200, 0);
            return;
        }
        // try to stick close to the tower instead of relying on nugbav
        // compresses diagonals to cardinal directions
        // NORTHEAST -> NORTH, SOUTHEAST -> EAST, etc.
        MapLocation next = ruinLocation
                .add(Direction.cardinalDirections()[((ruinLocation.directionTo(G.me).ordinal() / 2) + 1) % 4]);
        // go directly to next or just bug bork to the location
        if (!G.me.isAdjacentTo(ruinLocation) || !Motion.move(G.me.directionTo(next))) {
            Motion.bugnavTowards(next);
        }
        // G.rc.setIndicatorLine(G.me, ruinLocation, 255, 200, 0);
        // remap map infos because move buh
        int miDx = 4 - G.me.x, miDy = 4 - G.me.y;
        for (int i = G.nearbyMapInfos.length; --i >= 0;) {
            MapLocation loc = G.nearbyMapInfos[i].getMapLocation().translate(miDx, miDy);
            mapInfos[loc.y][loc.x] = G.nearbyMapInfos[i];
        }
        // paint second
        MapLocation paintLocation = null;
        boolean[][] pattern = Robot.towerPatterns[buildTowerType];
        if (G.me.isWithinDistanceSquared(ruinLocation, 8) && mapInfos[4][4].getPaint() == PaintType.EMPTY) {
            // paint under self first (passive paint drain)
            boolean paint = pattern[G.me.x - ruinLocation.x + 2][G.me.y - ruinLocation.y + 2];
            if (G.rc.canAttack(G.me))
                G.rc.attack(G.me, paint);
        } else {
            // paint pattern otherwise
            int ox = ruinLocation.x - G.me.x + 2;
            int oy = ruinLocation.y - G.me.y + 2;
            boolean paint;
            PaintType exists;
            MapLocation loc;
            int offset = Random.rand() % 25;
            for (int i = 25; --i >= 0;) {
                int dx = (i + offset) % 5;
                int dy = ((i + offset) % 25) / 5;
                if (dx == 2 && dy == 2)
                    continue;
                // location guaranteed to be on the map, unless ruinLocation isn't a ruin
                // guaranteed within vision radius if can attack there
                loc = ruinLocation.translate(dx - 2, dy - 2);
                if (G.rc.canAttack(loc)) {
                    paint = pattern[dx][dy];
                    exists = mapInfos[oy + dy][ox + dx].getPaint();
                    // can't paint enemy paint
                    if (!exists.isEnemy()
                            && (paint ? PaintType.ALLY_SECONDARY : PaintType.ALLY_PRIMARY) != exists) {
                        G.rc.attack(loc, paint);
                        paintLocation = loc;
                        break;
                    }
                }
            }
        }
        if (paintLocation != null)
            // G.rc.setIndicatorLine(G.me, paintLocation, 200, 100, 0);
        // G.rc.setIndicatorDot(G.me, 0, 0, 255);
        if (G.rc.canCompleteTowerPattern(Robot.towers[buildTowerType], ruinLocation)) {
            G.rc.completeTowerPattern(Robot.towers[buildTowerType], ruinLocation);
            POI.addTower(-1, ruinLocation, G.team, Robot.towers[buildTowerType]);
            mode = EXPLORE;
        }
    }

    public static void buildResource() throws Exception {
        G.indicatorString.append("BUILD_RP ");
        // MUCH IS IDENTICAL TO TOWER BUILD CODE
        // MapLocation paintLocation = null;
        int ox = resourceLocation.x - G.me.x + 2;
        int oy = resourceLocation.y - G.me.y + 2;
        boolean paint;
        PaintType exists;
        MapLocation loc;
        boolean isPatternComplete = true;
        int offset = Random.rand() % 25;
        for (int i = 25; --i >= 0;) {
            int dx = (i + offset) % 5;
            int dy = ((i + offset) % 25) / 5;
            // location guaranteed to be on the map by canBuildSrpHere
            // guaranteed within vision radius if can attack there
            loc = resourceLocation.translate(dx - 2, dy - 2);
            if (G.rc.canAttack(loc)) {
                paint = Robot.resourcePattern[dx][dy];
                exists = mapInfos[oy + dy][ox + dx].getPaint();
                if ((paint ? PaintType.ALLY_SECONDARY : PaintType.ALLY_PRIMARY) != exists) {
                    isPatternComplete = false;
                    // can't paint enemy paint
                    if (!exists.isEnemy()) {
                        G.rc.attack(loc, paint);
                        // paintLocation = loc;
                        break;
                    }
                }
            }
        }
        // end up having to check complete because may already be complete
        if (isPatternComplete) {
            if (G.rc.canCompleteResourcePattern(resourceLocation))
                G.rc.completeResourcePattern(resourceLocation);
            if (G.rc.getPaint() < SOL_SRP_MIN_PAINT) {
                // early retreat since painting more is very slow
                mode = RETREAT;
            } else {
                // put the 4 optimal locations of next pattern into a queue
                // that the bot then pathfinds to and checks if can build pattern
                mode = EXPAND_RESOURCE;
                // go clockwise because queue disqualification done in order
                // but also prioritize good tiling first
                srpCheckLocations = new MapLocation[] {
                        resourceLocation.translate(4, 4),
                        resourceLocation.translate(4, 0),
                        resourceLocation.translate(4, -4),
                        resourceLocation.translate(0, -4),
                        resourceLocation.translate(-4, -4),
                        resourceLocation.translate(-4, 0),
                        resourceLocation.translate(-4, 4),
                        resourceLocation.translate(0, 4),
                        resourceLocation.translate(3, 4),
                        resourceLocation.translate(4, 3),
                        resourceLocation.translate(4, -3),
                        resourceLocation.translate(3, -4),
                        resourceLocation.translate(-3, -4),
                        resourceLocation.translate(-4, -3),
                        resourceLocation.translate(-4, 3),
                        resourceLocation.translate(-3, 4)
                };
                srpCheckIndex = 0;
            }
            Motion.exploreRandomly(moveWithPaintMicro);
            // dot to signal building complete
            // G.rc.setIndicatorDot(resourceLocation, 255, 200, 0);
        } else {
            // just sit in the middle of the SRP
            Motion.bugnavTowards(resourceLocation, moveWithPaintMicro);
            // G.rc.setIndicatorLine(G.me, resourceLocation, 255, 100, 0);
        }
        // if (paintLocation != null)
            // G.rc.setIndicatorLine(G.me, paintLocation, 200, 100, 0);
        // G.rc.setIndicatorDot(G.me, 0, 200, 255);
    }

    public static void expandResource() throws Exception {
        G.indicatorString.append("EXPAND_RP ");
        lastSrpExpansion = G.round;
        Motion.bugnavTowards(srpCheckLocations[srpCheckIndex], moveWithPaintMicro);
        // show the queue and current target
        // for (int i = srpCheckLocations.length; --i >= srpCheckIndex;) {
            // if (G.rc.onTheMap(srpCheckLocations[i]))
                // G.rc.setIndicatorDot(srpCheckLocations[i], 200, 100, 150);
        // }
        // G.rc.setIndicatorLine(G.me, srpCheckLocations[srpCheckIndex], 255, 0, 150);
        // G.rc.setIndicatorDot(G.me, 0, 200, 255);
    }

    public static void attack() throws Exception {
        G.indicatorString.append("ATTACK ");
        // attack micro moment
        if (towerLocation.isWithinDistanceSquared(G.me, towerType.actionRadiusSquared)) {
            if (G.rc.canAttack(towerLocation))
                G.rc.attack(towerLocation);
            Motion.bugnavAway(towerLocation, attackMicro);
        } else {
            if (G.rc.isActionReady()) {
                Motion.bugnavTowards(towerLocation, attackMicro);
                if (G.rc.canAttack(towerLocation))
                    G.rc.attack(towerLocation);
            } else {
                Motion.bugnavAround(towerLocation, towerType.actionRadiusSquared + 1,
                        towerType.actionRadiusSquared + 1, moveWithPaintMicro);
            }
        }
        // G.rc.setIndicatorDot(G.me, 255, 0, 0);
    }

    /**
     * Checks if an SRP cannot be built at a location. If `false`, doesn't
     * necessarily mean *can* build, only *can't not* build.
     */
    public static boolean cannotBuildSrpAtLocation(MapLocation center) throws Exception {
        // cache
        if (((disqualifiedSrpLocations[center.y] >> center.x) & 1) == 1) {
            return true;
        }
        // check if on map first
        if (center.x < 2 || center.x > G.mapWidth - 3 || center.y < 2 || center.y > G.mapHeight - 3) {
            disqualifiedSrpLocations[center.y] |= 1L << center.x;
            return true;
        }
        int ox = center.x - G.me.x + 4;
        int oy = center.y - G.me.y + 4;
        // if SRP already there, don't
        if (G.rc.canSenseLocation(center) && mapInfos[oy][ox].isResourcePatternCenter()) {
            disqualifiedSrpLocations[center.y] |= 1L << center.x;
            return true;
        }
        // check for ruins/towers that could interfere
        MapLocation ruinLoc;
        for (int i = G.nearbyRuins.length; --i >= 0;) {
            ruinLoc = G.nearbyRuins[i];
            if (center.equals(ruinLoc) || !G.rc.canSenseRobotAtLocation(ruinLoc)
                    && Math.abs(center.x - ruinLoc.x) <= 4 && Math.abs(center.y - ruinLoc.y) <= 4) {
                disqualifiedSrpLocations[center.y] |= 1L << center.x;
                return true;
            }
        }
        // only 5 allowed locations for SRP markers (center included)
        // within vision radius - everything else not allowed
        // also check passibility within 5x5 square
        // CODEGEN WARNING CODEGEN WARNING CODEGEN WARNING CODEGEN WARNING
        if (G.rc.canSenseLocation(center.translate(-2, -4))
                && (mapInfos[-4 + oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, -4))
                        && (mapInfos[-4 + oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(1, -4))
                        && (mapInfos[-4 + oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, -4))
                        && (mapInfos[-4 + oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-3, -3))
                        && (mapInfos[-3 + oy][-3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-2, -3))
                        && (mapInfos[-3 + oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, -3))
                        && (mapInfos[-3 + oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(0, -3))
                        && (mapInfos[-3 + oy][ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(1, -3))
                        && (mapInfos[-3 + oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, -3))
                        && (mapInfos[-3 + oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(3, -3))
                        && (mapInfos[-3 + oy][3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-4, -2))
                        && (mapInfos[-2 + oy][-4 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-3, -2))
                        && (mapInfos[-2 + oy][-3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-2, -2))
                        && (!mapInfos[-2 + oy][-2 + ox].isPassable()
                                || mapInfos[-2 + oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, -2))
                        && (!mapInfos[-2 + oy][-1 + ox].isPassable()
                                || mapInfos[-2 + oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(0, -2))
                        && (!mapInfos[-2 + oy][ox].isPassable()
                                || mapInfos[-2 + oy][ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(1, -2))
                        && (!mapInfos[-2 + oy][1 + ox].isPassable()
                                || mapInfos[-2 + oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, -2))
                        && (!mapInfos[-2 + oy][2 + ox].isPassable()
                                || mapInfos[-2 + oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(3, -2))
                        && (mapInfos[-2 + oy][3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(4, -2))
                        && (mapInfos[-2 + oy][4 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-4, -1))
                        && (mapInfos[-1 + oy][-4 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-3, -1))
                        && (mapInfos[-1 + oy][-3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-2, -1))
                        && (!mapInfos[-1 + oy][-2 + ox].isPassable()
                                || mapInfos[-1 + oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, -1))
                        && (!mapInfos[-1 + oy][-1 + ox].isPassable()
                                || mapInfos[-1 + oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(0, -1))
                        && (!mapInfos[-1 + oy][ox].isPassable()
                                || mapInfos[-1 + oy][ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(1, -1))
                        && (!mapInfos[-1 + oy][1 + ox].isPassable()
                                || mapInfos[-1 + oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, -1))
                        && (!mapInfos[-1 + oy][2 + ox].isPassable()
                                || mapInfos[-1 + oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(3, -1))
                        && (mapInfos[-1 + oy][3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(4, -1))
                        && (mapInfos[-1 + oy][4 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-3, 0))
                        && (mapInfos[oy][-3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-2, 0))
                        && (!mapInfos[oy][-2 + ox].isPassable()
                                || mapInfos[oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, 0))
                        && (!mapInfos[oy][-1 + ox].isPassable()
                                || mapInfos[oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(0, 0))
                        && (!mapInfos[oy][ox].isPassable())
                || G.rc.canSenseLocation(center.translate(1, 0))
                        && (!mapInfos[oy][1 + ox].isPassable()
                                || mapInfos[oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, 0))
                        && (!mapInfos[oy][2 + ox].isPassable()
                                || mapInfos[oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(3, 0))
                        && (mapInfos[oy][3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-4, 1))
                        && (mapInfos[1 + oy][-4 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-3, 1))
                        && (mapInfos[1 + oy][-3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-2, 1))
                        && (!mapInfos[1 + oy][-2 + ox].isPassable()
                                || mapInfos[1 + oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, 1))
                        && (!mapInfos[1 + oy][-1 + ox].isPassable()
                                || mapInfos[1 + oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(0, 1))
                        && (!mapInfos[1 + oy][ox].isPassable()
                                || mapInfos[1 + oy][ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(1, 1))
                        && (!mapInfos[1 + oy][1 + ox].isPassable()
                                || mapInfos[1 + oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, 1))
                        && (!mapInfos[1 + oy][2 + ox].isPassable()
                                || mapInfos[1 + oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(3, 1))
                        && (mapInfos[1 + oy][3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(4, 1))
                        && (mapInfos[1 + oy][4 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-4, 2))
                        && (mapInfos[2 + oy][-4 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-3, 2))
                        && (mapInfos[2 + oy][-3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-2, 2))
                        && (!mapInfos[2 + oy][-2 + ox].isPassable()
                                || mapInfos[2 + oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, 2))
                        && (!mapInfos[2 + oy][-1 + ox].isPassable()
                                || mapInfos[2 + oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(0, 2))
                        && (!mapInfos[2 + oy][ox].isPassable()
                                || mapInfos[2 + oy][ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(1, 2))
                        && (!mapInfos[2 + oy][1 + ox].isPassable()
                                || mapInfos[2 + oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, 2))
                        && (!mapInfos[2 + oy][2 + ox].isPassable()
                                || mapInfos[2 + oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(3, 2))
                        && (mapInfos[2 + oy][3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(4, 2))
                        && (mapInfos[2 + oy][4 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-3, 3))
                        && (mapInfos[3 + oy][-3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-2, 3))
                        && (mapInfos[3 + oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, 3))
                        && (mapInfos[3 + oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(0, 3))
                        && (mapInfos[3 + oy][ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(1, 3))
                        && (mapInfos[3 + oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, 3))
                        && (mapInfos[3 + oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(3, 3))
                        && (mapInfos[3 + oy][3 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-2, 4))
                        && (mapInfos[4 + oy][-2 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(-1, 4))
                        && (mapInfos[4 + oy][-1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(1, 4))
                        && (mapInfos[4 + oy][1 + ox].getMark() == PaintType.ALLY_SECONDARY)
                || G.rc.canSenseLocation(center.translate(2, 4))
                        && (mapInfos[4 + oy][2 + ox].getMark() == PaintType.ALLY_SECONDARY)) {
            disqualifiedSrpLocations[center.y] |= 1L << center.x;
            return true;
        }
        return false;
    }

    /**
     * Check if an SRP can be built or repaired at location.
     */
    public static boolean canBuildSrpAtLocation(MapLocation center) throws Exception {
        // if on top of a current SRP, yes
        if (G.rc.canSenseLocation(center) && G.rc.senseMapInfo(center).getMark() == PaintType.ALLY_SECONDARY)
            return true;
        else
            return !cannotBuildSrpAtLocation(center);
    }

    /**
     * Marker above = defense (0) (which we will never use lmao)
     * Marker to left = money (1)
     * Marker to right = paint (2)
     * Marker below = rc.disintigrate
     */
    public static int predictTowerType(MapLocation loc) throws Exception {
        G.indicatorString.append("(M=" + POI.moneyTowers + ", P=" + POI.paintTowers + ") ");
        // check for marker
        int ox = loc.x - G.me.x + 4;
        int oy = loc.y - G.me.y + 4;
        // if (mapInfos[oy + 1][ox].getMark() == PaintType.ALLY_PRIMARY)
        // return 0;
        if (G.me.isWithinDistanceSquared(loc.add(Direction.WEST), 20)
                && mapInfos[oy][ox - 1].getMark() == PaintType.ALLY_PRIMARY)
            return 1;
        if (G.me.isWithinDistanceSquared(loc.add(Direction.EAST), 20)
                && mapInfos[oy][ox + 1].getMark() == PaintType.ALLY_PRIMARY)
            return 2;
        // no im not adding the rc.disintigrate too much bytecode
        int towerType = G.rc.getChips() < 20000 && POI.paintTowers * SOL_MONEY_PAINT_TOWER_RATIO > POI.moneyTowers ? 1
                : 2;
        MapLocation place = loc;
        switch (towerType) {
            case 1 -> place = loc.translate(-1, 0);
            case 2 -> place = loc.translate(1, 0);
        }
        if (G.rc.canMark(place))
            G.rc.mark(place, false);
        return towerType;
    }

    /**
     * Weights neutral tiles as ally tiles if it can paint them, then paints them if
     * it moves to one. DO NOT CHAIN WITH OTHER MICRO FUNCTIONS.
     */
    public static Micro moveWithPaintMicro = new Micro() {
        @Override
        public int[] micro(Direction d, MapLocation dest) throws Exception {
            int[] scores = Motion.defaultMicro.micro(d, dest);
            MapLocation nxt, bestLoc = G.me;
            int best = -1000000000;
            int numTurnsUntilNextMove = ((G.cooldown(G.rc.getPaint(), GameConstants.MOVEMENT_COOLDOWN)
                    + Motion.movementCooldown)
                    / 10);
            boolean canPaintBest = false;
            for (int i = 8; --i >= 0;) {
                nxt = G.me.add(G.ALL_DIRECTIONS[i]);
                if (G.rc.onTheMap(nxt) && G.rc.senseMapInfo(nxt).getPaint() == PaintType.EMPTY && G.rc.canAttack(nxt)) {
                    // equalize
                    scores[i] += 5 * GameConstants.PENALTY_NEUTRAL_TERRITORY * numTurnsUntilNextMove;
                    if (scores[i] > best) {
                        best = scores[i];
                        canPaintBest = true;
                        bestLoc = nxt;
                    }
                } else if (scores[i] > best) {
                    best = scores[i];
                    canPaintBest = false;
                }
            }
            if (canPaintBest) {
                // no more checkerboarding :(
                G.rc.attack(bestLoc, false);
            } else if (G.rc.getActionCooldownTurns() < GameConstants.COOLDOWN_LIMIT) {
                // try to paint nearby
                MapLocation loc;
                for (int dx = -2; ++dx <= 2;) {
                    for (int dy = -2; ++dy <= 2;) {
                        loc = G.me.translate(dx, dy);
                        if (G.rc.onTheMap(loc) && mapInfos[dy + 4][dx + 4].getPaint() == PaintType.EMPTY) {
                            // still have to check if on map
                            if (G.rc.canAttack(loc))
                                G.rc.attack(loc);
                        }
                    }
                }
                loc = G.me.translate(-3, 0);
                if (G.rc.onTheMap(loc) && mapInfos[4][1].getPaint() == PaintType.EMPTY) {
                    // still have to check if on map
                    if (G.rc.canAttack(loc))
                        G.rc.attack(loc);
                }
                loc = G.me.translate(0, 3);
                if (G.rc.onTheMap(loc) && mapInfos[7][4].getPaint() == PaintType.EMPTY) {
                    // still have to check if on map
                    if (G.rc.canAttack(loc))
                        G.rc.attack(loc);
                }
                loc = G.me.translate(3, 0);
                if (G.rc.onTheMap(loc) && mapInfos[4][7].getPaint() == PaintType.EMPTY) {
                    // still have to check if on map
                    if (G.rc.canAttack(loc))
                        G.rc.attack(loc);
                }
                loc = G.me.translate(0, -3);
                if (G.rc.onTheMap(loc) && mapInfos[1][4].getPaint() == PaintType.EMPTY) {
                    // still have to check if on map
                    if (G.rc.canAttack(loc))
                        G.rc.attack(loc);
                }
            }
            return scores;
        }
    };

    public static Micro attackMicro = new Micro() {
        @Override
        public int[] micro(Direction d, MapLocation dest) throws Exception {
            // try to stay out of range if on cd, otherwise try to get in range
            int[] scores = Motion.defaultMicro.micro(d, dest);
            if (G.rc.isActionReady()) {
                for (int i = 8; --i >= 0;) {
                    if (G.me.add(G.DIRECTIONS[i]).isWithinDistanceSquared(towerLocation,
                            G.rc.getType().actionRadiusSquared)) {
                        scores[i] += 40;
                    }
                }
            } else {
                for (int i = 8; --i >= 0;) {
                    if (!G.me.add(G.DIRECTIONS[i]).isWithinDistanceSquared(towerLocation,
                            towerType.actionRadiusSquared)) {
                        scores[i] += 40;
                    }
                }
            }
            return scores;
        }
    };
}