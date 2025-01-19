package dbekinalkar.lib.map;

import battlecode.common.*;

import java.util.*;

public class MapContext {
    public RobotController rc;

    public MapLocation lastLoc;
    public MapLocation currLoc;

    public HashSet<MapLocation> buildable;
    public HashSet<MapLocation> unbuildable;

    public HashSet<MapLocation> enemyTowers;
    public HashSet<MapLocation> allyTowers;

    public HashSet<MapLocation> paintable;
    public HashSet<MapLocation> unpaintable;
    public PaintType[][] paint;

    public MapContext(RobotController rc) {
        this.rc = rc;
        this.lastLoc = this.rc.getLocation();
        this.currLoc = this.lastLoc;

        buildable = new HashSet<MapLocation>();
        unbuildable = new HashSet<MapLocation>();

        enemyTowers = new HashSet<MapLocation>();
        allyTowers = new HashSet<MapLocation>();

        paintable = new HashSet<MapLocation>();
        unpaintable = new HashSet<MapLocation>();

        paint = new PaintType[this.rc.getMapWidth()][this.rc.getMapHeight()];
    }

    public void parseMap() throws GameActionException {
        this.lastLoc = this.currLoc;
        this.currLoc = this.rc.getLocation();


        System.out.println("Parsing MapContext");
        MapLocation[] locs = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), 20);

        for(MapLocation loc: locs) {
            MapInfo mi = rc.senseMapInfo(loc);
            RobotInfo ri = rc.senseRobotAtLocation(loc);

            paint[loc.x][loc.y] = mi.getPaint();
            if(!mi.isPassable()) paint[loc.x][loc.y] = null;

            if(paint[loc.x][loc.y] != null) {
                switch(paint[loc.x][loc.y]) {
                    case PaintType.EMPTY:
                        paintable.add(loc);
                        unpaintable.remove(loc);
                        break;
                    case PaintType.ALLY_PRIMARY:
                    case PaintType.ALLY_SECONDARY:
                        paintable.remove(loc);
                        break;
                    case PaintType.ENEMY_PRIMARY:
                    case PaintType.ENEMY_SECONDARY:
                        unpaintable.add(loc);
                        paintable.remove(loc);
                        break;
                }
            }

            if(mi.hasRuin() ) {
                if(ri != null) {
                    buildable.remove(loc);
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
        if(unbuildable.contains(ruinLoc)) return false;

        for(int i = -2; i < 3; i++) {
            for(int j = -2; j < 3; j++) {
                if(i == 0 && j == 0) continue;
                MapLocation loc = ruinLoc.translate(i, j);

                PaintType p;

                try {
                    MapInfo mi = rc.senseMapInfo(loc);
                    p = mi.getPaint();
                } catch (GameActionException ignored) {
                    p = this.paint[loc.x][loc.y];
                }

                if(p != null && p.isEnemy()) {
                    unbuildable.add(ruinLoc);
                    return false;
                }
            }
        }

        return true;
    }

    public Optional<MapLocation> closest(HashSet<MapLocation> locs, MapLocation loc) {
        return locs.stream().min(Comparator.comparingInt(a -> loc.distanceSquaredTo(a)));
    }
}
