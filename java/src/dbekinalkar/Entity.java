package dbekinalkar;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

public abstract class Entity {
    private RobotController rc;

    public Entity (RobotController rc) {
        this.rc = rc;
    }

    public abstract void run() throws GameActionException;
}
