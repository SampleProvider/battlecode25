package TSPAARKJAN16;

import battlecode.common.*;

// commit: debd90cbba129fd6d21231d314d992ee534e5292

public class RobotPlayer {
    public static void updateInfo() throws Exception {
        // every time we move
        G.me = G.rc.getLocation();
        G.allyRobots = G.rc.senseNearbyRobots(-1, G.team);
        G.opponentRobots = G.rc.senseNearbyRobots(-1, G.opponentTeam);
        G.allyRobotsString = new StringBuilder();
        for (int i = G.allyRobots.length; --i >= 0;) {
            if (G.allyRobots[i].type.isRobotType()) {
                G.allyRobotsString.append(G.allyRobots[i].toString());
            }
        }
        G.opponentRobotsString = new StringBuilder();
        for (int i = G.opponentRobots.length; --i >= 0;) {
            if (G.opponentRobots[i].type.isRobotType()) {
                G.opponentRobotsString.append(G.opponentRobots[i].toString());
            }
        }
        G.nearbyMapInfos = G.rc.senseNearbyMapInfos();

        String s = G.me.toString();
        Motion.lastVisitedLocations.append(s);
        switch (s.length()) {
            case 6:
                Motion.lastVisitedLocations.append("  ");
                break;
            case 7:
                Motion.lastVisitedLocations.append(" ");
                break;
        }
    }

    public static void updateRound() throws Exception {
        // every round
        Motion.movementCooldown -= GameConstants.COOLDOWNS_PER_TURN * (G.rc.getRoundNum() - G.round);
        Motion.movementCooldown = Math.max(Motion.movementCooldown, 0);
        G.round = G.rc.getRoundNum();
        updateInfo();
        POI.updateRound();
    }

    public static void run(RobotController rc) throws Exception {
        try {
            G.rc = rc;
            Random.state = G.rc.getID() * 0x2bda6bc + 0x9734e9;
            G.mapCenter = new MapLocation(G.rc.getMapWidth() / 2, G.rc.getMapHeight() / 2);
            G.mapArea = G.rc.getMapWidth() * G.rc.getMapHeight();
            G.team = G.rc.getTeam();
            G.opponentTeam = G.team.opponent();
            POI.init();
            G.indicatorString = new StringBuilder();
            updateInfo();
            switch (G.rc.getType()) {
                case MOPPER, SOLDIER, SPLASHER -> Robot.init();
                default -> Tower.init();
            }
            // init bytecode count
            G.indicatorString.append("INIT " + Clock.getBytecodeNum() + " ");
            while (true) {
                int r = G.rc.getRoundNum();
                try {
                    updateRound();
                    switch (G.rc.getType()) {
                        case MOPPER, SOLDIER, SPLASHER -> Robot.run();
                        default -> Tower.run();
                    }
                    G.rc.setIndicatorString(G.indicatorString.toString());
                    G.indicatorString = new StringBuilder();
                } catch (GameActionException e) {
                    // System.out.println("Unexpected GameActionException");
                    e.printStackTrace();
                } catch (Exception e) {
                    // System.out.println("Unexpected Exception");
                    e.printStackTrace();
                }
                if (G.rc.getRoundNum() != r) {
                    // System.err.println("Bytecode overflow! (Round " + r + ", " + G.rc.getType() + ", " + G.rc.getLocation() + ")");
                    G.indicatorString.append("BYTE=" + r + " ");
                }
                // for (int i = 0; i <= 50; i++) {
                //     int a=Random.rand()%G.rc.getMapHeight(),b=Random.rand()%G.rc.getMapWidth(),c=Random.rand()%G.rc.getMapHeight(),d=Random.rand()%G.rc.getMapWidth();
                //   // // G.rc.setIndicatorLine(new MapLocation(b, a), new MapLocation(d, c), Random.rand()%256, Random.rand()%256, Random.rand()%256);
                // }
                Clock.yield();
            }
        } catch (GameActionException e) {
            // System.out.println("Unexpected GameActionException");
            e.printStackTrace();
        } catch (Exception e) {
            // System.out.println("Unexpected Exception");
            e.printStackTrace();
        }
    }
}