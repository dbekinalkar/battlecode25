package reverse_dbekinalkar;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;

import java.util.Random;

public abstract class Entity {
    public RobotController rc;
    public Random rand;

    public Entity(RobotController rc) {
        this.rc = rc;
        this.rand = new Random(255);
    }

    public abstract void run() throws GameActionException;
}
