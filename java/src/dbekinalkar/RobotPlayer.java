package dbekinalkar;

import battlecode.common.*;
import dbekinalkar.robot.Robot;
import dbekinalkar.tower.Tower;

import java.util.Random;


public class RobotPlayer {
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        Entity e = switch (rc.getType()) {
            case MOPPER, SOLDIER, SPLASHER -> new Robot(rc);
            default -> new Tower(rc);
        };

        while (true) {
            try {
                e.run();
            }
            catch(Exception err) {
                err.printStackTrace();
            }
            finally {
                Clock.yield();
            }


        }
    }
}
