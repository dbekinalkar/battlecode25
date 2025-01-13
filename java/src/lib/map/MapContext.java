package lib.map;

import battlecode.common.*;

import java.util.*;

public class MapContext {
    RobotController rc;
    HashSet<MapLocation> buildable;
    HashSet<MapLocation> ruined;
    HashSet<MapLocation> enemyTowers;
    HashSet<MapLocation> allyTowers;

    public MapContext(RobotController rc) {
        this.rc = rc;
        buildable = new HashSet<MapLocation>();
        enemyTowers = new HashSet<MapLocation>();
    }

    public void parseMap() throws GameActionException {
        MapLocation[] locs = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 20);

        for(MapLocation loc: locs) {
            MapInfo mi = rc.senseMapInfo(loc);
            RobotInfo ri = rc.senseRobotAtLocation(loc);

            if(mi.hasRuin() ) {
                if(ri != null) {
                    if (ri.getTeam() == rc.getTeam()) {
                        allyTowers.add(loc);
                    }
                    else {
                        enemyTowers.add(loc);
                    }
                }
                else {
                    if(isBuildable(loc)) {
                        buildable.add(loc);
                    }
                    else {
                        buildable.remove(loc);
                    }
                }
            }
        }

    }

    private boolean isBuildable(MapLocation ruinLoc) {

        for(int i = -2; i < 3; i++) {
            for(int j = -2; j < 3; j++) {
                try {
                    if(i == 0 && j == 0) continue;
                    MapLocation loc = ruinLoc.translate(i, j);

                    MapInfo mi = rc.senseMapInfo(loc);

                    if(mi.getPaint().isEnemy()) {
                        ruined.add(ruinLoc);
                        return false;
                    }
                } catch (GameActionException ignored) {}
            }
        }

        return true;
    }

    public Optional<MapLocation> closest(HashSet<MapLocation> locs, MapLocation loc) {
        return locs.stream().min(Comparator.comparingInt(loc::distanceSquaredTo));
    }
}
