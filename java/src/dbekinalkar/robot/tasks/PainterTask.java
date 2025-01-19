package dbekinalkar.robot.tasks;

import battlecode.common.*;
import dbekinalkar.robot.Robot;
import dbekinalkar.lib.pathfinding.Pathfinder;
import dbekinalkar.lib.pattern.Patterns;
import dbekinalkar.lib.task_manager.Task;

import java.util.Optional;

public class PainterTask extends Task {

    public MapLocation target;

    public PainterTask(Robot r) {
        super(r);
    }

    int[] building = {-2, -1, 0, 1, 2};
    int[] rev_building = {2, 1, 0, -1, -2};


    @Override
    public boolean setup() {
        return true;
    }

    @Override
    public void run() throws GameActionException {

         boolean ret = refill();

         if(ret) return;

         ret = buildTower();

         if(this.r.rc.getID() == 11457) System.out.println("Build tower: " + ret);

         if(ret) return;


         ret = paint();

         if(ret) return;

         scout();
    }

    public boolean buildTower() throws GameActionException {
        Optional<MapLocation> ret = this.r.context.closest(this.r.context.buildable, this.r.rc.getLocation());

        if(ret.isEmpty()) {
            System.out.println("No buildable locations");
            return false;
        }

        target = ret.get();

        if(this.r.rc.canCompleteTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, target)) {
            this.r.rc.completeTowerPattern(UnitType.LEVEL_ONE_MONEY_TOWER, target);
            return true;
        }

        // 3 cases we can't: no pattern, too far, no chips

        // CASE 1: No Pattern

        Direction d = this.r.rc.getLocation().directionTo(target);

        int[] dx = switch(d) {
            case EAST, NORTHEAST, SOUTHEAST -> building;
            default -> rev_building;
        };

        int[] dy =
        switch (d) {
            case NORTH, NORTHWEST, NORTHEAST -> building;
            default -> rev_building;
        };

        int i = 0;
        int j = 0;

        while(j < 5) {
            try {
//                MapLocation loc = target.translate(i-2, j-2);
                MapLocation loc = target.translate(dx[i], dy[j]);
//                if(this.r.context.paintable.contains(loc)) {
                MapInfo mi = this.r.rc.senseMapInfo(loc);
                if(mi.getPaint() != Patterns.chip_tower[i][j]) {
                    this.r.rc.attack(loc, Patterns.chip_tower[i][j] == Patterns.two);
                    Pathfinder.navigate(this.r, target);

                    return true;
                }
            } catch (GameActionException ignored) {}
            finally {
                if(j == 0) {
                    j = i + 1;
                    i = 0;
                }
                else {
                    i++;
                    j--;
                }
            }
        }

        // CASE 2: too far
        // CASE 3: not enough chips
        if(target.distanceSquaredTo(this.r.rc.getLocation()) > 4 || this.r.rc.getChips() < 1000) {
            Pathfinder.navigate(this.r, target);
            return true;
        }

        // CASE 4: failed for some other reason, brick
        System.out.println(this.r.rc.getID() + " build tower unknown failure");
        return false;
    }

    public boolean paint() throws GameActionException {
        System.out.println("Painting");
        Optional<MapLocation> ret = this.r.context.closest(this.r.context.paintable, this.r.rc.getLocation());

        if(ret.isEmpty()) return false;

        target = ret.get();

        boolean res = Pathfinder.navigate(this.r, target);

        if(this.r.rc.canPaint(target)) {
            this.r.rc.attack(target);
            return true;
        }

        return false;
    }

    public boolean scout() throws GameActionException {
        Pathfinder.navigate(this.r, new MapLocation(0, this.r.rc.getMapHeight() / 2));
        return false;
    }

    public boolean refill() throws GameActionException {
        if(this.r.rc.getPaint() > 30) return false;
        System.out.println("Refilling");

        Optional<MapLocation> ret = this.r.context.closest(this.r.context.allyTowers, this.r.rc.getLocation());

        if(ret.isEmpty()) return false;

        target = ret.get();

        Pathfinder.navigate(this.r, target);

        try {
            while(this.r.rc.canTransferPaint(target, -10)) {
                this.r.rc.transferPaint(target, -10);
            }
        } catch (GameActionException ignored) {}

        return true;
    }
}
