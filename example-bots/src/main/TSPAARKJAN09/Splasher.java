package TSPAARKJAN09;

import battlecode.common.*;

public class Splasher {
    public static final int EXPLORE = 0;
    public static final int ATTACK = 1;
    public static final int RETREAT = 2;
    public static int mode = EXPLORE;

    // every tile in attack range
    public static MapLocation[] attackRange = new MapLocation[] {
            new MapLocation(0, 0),
            new MapLocation(1, 0),
            new MapLocation(0, 1),
            new MapLocation(-1, 0),
            new MapLocation(0, -1),
            new MapLocation(1, 1),
            new MapLocation(1, -1),
            new MapLocation(-1, 1),
            new MapLocation(-1, -1),
            new MapLocation(2, 0),
            new MapLocation(0, 2),
            new MapLocation(-2, 0),
            new MapLocation(0, -2),
            new MapLocation(2, 1),
            new MapLocation(2, -1),
            new MapLocation(-2, 1),
            new MapLocation(-2, -1),
            new MapLocation(1, 2),
            new MapLocation(-1, 2),
            new MapLocation(1, -2),
            new MapLocation(-1, -2),
            new MapLocation(2, 2),
            new MapLocation(2, -2),
            new MapLocation(-2, 2),
            new MapLocation(-2, -2)
    };

    public static MapLocation attackTarget = new MapLocation(-1, -1);
    public static int attackTargetTower;
    public static StringBuilder triedAttackTargets = new StringBuilder();

    public static void updateAttackTarget() throws Exception {
        if (attackTargetTower != -1) {
            if (POI.parseTowerTeam(POI.towers[attackTargetTower]) != G.opponentTeam) {
                attackTarget = new MapLocation(-1, -1);
            }
            else {
                MapLocation loc = POI.parseLocation(POI.towers[attackTargetTower]);
                search: if (G.me.distanceSquaredTo(loc) <= 4) {
                    for (int y = -2; y <= 2; y++) {
                        for (int x = -2; x <= 2; x++) {
                            if (G.rc.senseMapInfo(new MapLocation(loc.x + x, loc.y + y)).getPaint().isEnemy()) {
                                break search;
                            }
                        }
                    }
                    attackTarget = new MapLocation(-1, -1);
                }
            }
            // : make it change targets if it finds ruin with 24 empty/ally paint
        }
        if (attackTarget.x == -1) {
            int best = -1;
            int bestWeight = 0;
            String tried = triedAttackTargets.toString();
            for (int i = 144; --i >= 0;) {
                if (POI.towers[i] == -1) {
                    break;
                }
                if (POI.parseTowerTeam(POI.towers[i]) == G.team) {
                    continue;
                }
                if (POI.parseTowerTeam(POI.towers[i]) == Team.NEUTRAL && G.rc.getNumberTowers() == 25) {
                    continue;
                }
                int distance = Motion.getChebyshevDistance(G.me, POI.parseLocation(POI.towers[i]));
                int weight = -distance;
                if (tried.contains("-" + i + "-")) {
                    weight -= 1000;
                }
                if (best == -1 || weight > bestWeight) {
                    best = i;
                    bestWeight = weight;
                }
            }
            if (best == -1) {
                mode = EXPLORE;
                return;
            }
            attackTargetTower = best;
            attackTarget = POI.parseLocation(POI.towers[best]);
            triedAttackTargets.append(":" + best + ":");
            mode = ATTACK;
        }
    }

    public static void run() throws Exception {
        if (G.rc.getPaint() < G.rc.getType().paintCapacity / 3) {
            mode = RETREAT;
        } else if (G.rc.getPaint() > G.rc.getType().paintCapacity * 3 / 4 && mode == RETREAT) {
            mode = EXPLORE;
        }
        if (mode != RETREAT) {
            updateAttackTarget();
        }
        else {
            triedAttackTargets = new StringBuilder();
        }
        MapLocation bestLoc = null;
        int bestScore = 0;
        switch (mode) {
            case EXPLORE:
                G.indicatorString.append("EXPLORE ");
                // painting heuristic
                for (int i = attackRange.length; --i >= 0;) {
                    MapLocation loc = new MapLocation(G.me.x + attackRange[i].x, G.me.y + attackRange[i].y);
                    if (G.rc.canAttack(loc)) {
                        int score = 0;
                        for (int dir = 9; --dir >= 0;) {
                            // only care about sqrt(2) distance because bytecode restrictions
                            MapLocation nxt = loc.add(G.ALL_DIRECTIONS[dir]);
                            if (G.rc.canSenseLocation(nxt)) {
                                MapInfo info = G.rc.senseMapInfo(nxt);
                                if (info.isPassable()) {
                                    PaintType paint = info.getPaint();
                                    if (paint == PaintType.EMPTY)
                                        score++;
                                    if (paint.isEnemy())
                                        score += 2; // bonus points for deleting opponent paint
                                    if (!paint.isAlly() && nxt == G.me) {
                                        // bonus points for painting self
                                        score++;
                                    }
                                }
                            }
                        }
                        if (score > bestScore) {
                            bestLoc = loc;
                            bestScore = score;
                        }
                        if (Clock.getBytecodesLeft() < 2000) {
                            break;
                        }
                    }
                }
                if (bestScore > 4 && bestLoc != null) {
                    G.rc.attack(bestLoc, G.rng.nextBoolean());
                }
                Motion.exploreRandomly();
                // // G.rc.setIndicatorDot(G.me, 0, 255, 0);
                break;
            case ATTACK:
                G.indicatorString.append("ATTACK ");
                // painting heuristic
                for (int i = attackRange.length; --i >= 0;) {
                    MapLocation loc = new MapLocation(G.me.x + attackRange[i].x, G.me.y + attackRange[i].y);
                    if (G.rc.canAttack(loc)) {
                        int score = 0;
                        for (int dir = 9; --dir >= 0;) {
                            // only care about sqrt(2) distance because bytecode restrictions
                            MapLocation nxt = loc.add(G.ALL_DIRECTIONS[dir]);
                            if (G.rc.canSenseLocation(nxt)) {
                                MapInfo info = G.rc.senseMapInfo(nxt);
                                if (info.isPassable()) {
                                    PaintType paint = info.getPaint();
                                    if (paint == PaintType.EMPTY)
                                        score++;
                                    if (paint.isEnemy()) {
                                        score += 2; // bonus points for deleting opponent paint
                                        if (attackTarget.x != -1 && Motion.getChebyshevDistance(nxt, attackTarget) <= 2) {
                                            score += 4;
                                        }
                                    }
                                    if (!paint.isAlly() && nxt == G.me) {
                                        // bonus points for painting self
                                        score++;
                                    }
                                }
                            }
                        }
                        if (score > bestScore) {
                            bestLoc = loc;
                            bestScore = score;
                        }
                        if (Clock.getBytecodesLeft() < 2000) {
                            break;
                        }
                    }
                }
                if (bestScore > 4 && bestLoc != null) {
                    G.rc.attack(bestLoc, G.rng.nextBoolean());
                }
                Motion.bugnavTowards(attackTarget);
                // // G.rc.setIndicatorLine(G.me, attackTarget, 255, 255, 0);
                // // G.rc.setIndicatorDot(G.me, 255, 0, 0);
                break;
            case RETREAT:
                G.indicatorString.append("RETREAT ");
                Robot.retreat();
                break;
        }
    }
}
