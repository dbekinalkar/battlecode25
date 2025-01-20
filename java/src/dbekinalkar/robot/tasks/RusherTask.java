package dbekinalkar.robot.tasks;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import dbekinalkar.lib.task_manager.Task;
import dbekinalkar.robot.Robot;

public class RusherTask extends Task {
    // If in top part of map
    static int spiralStep = 0;
    static int spiralRadius = 1;
    static int stepsTaken = 0;
    static int amountSteps = 1;
    static Direction[] spiralDirections = {Direction.SOUTHEAST, Direction.SOUTHWEST, Direction.NORTHWEST, Direction.NORTHEAST};

    static int layerLoop = 0;
    static int directionCount = 0;

    //Andrew's stupid variables
    static Direction[] lateralDirections = {Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.SOUTH};
    static int counter = 0;
    static int lateralDir = 0;
    static boolean True = true;
    static boolean False = false;

    public RusherTask(Robot r) {
        super(r);
    }

    @Override
    public boolean setup() {
        return true;
    }


    public void run() throws GameActionException {
        //switch ()
        //if (this.r.rc.getLocation()



        //if (this.r.rc.getMapHeight() > 20) {
        //
        //}
//        if (this.r.rc.getRoundNum() > 20) {
//            return;
            if (!this.r.rc.isActionReady()) return;
//            if (!this.r.rc.senseMapInfo(this.r.rc.getLocation()).getPaint().isAlly() && counter == 5) {
//                if (rc.canPaint(rc.getLocation())) {
//                    rc.attack(rc.getLocation());
//                    counter = 0;
//                    System.out.println("Painting");
//                }
//            }
//            else if (counter == 5) {
//                counter = 0;
//                System.out.println("Resetting counter");
//            }
//            if (rc.senseMapInfo(rc.getLocation().add(lateralDirections[lateralDir])).getPaint().isAlly()) {
//                lateralDir = (lateralDir + 1) % 4;
//                counter = 0;
//            }
//            if (rc.canMove(lateralDirections[lateralDir])) {
//                rc.move(lateralDirections[lateralDir]);
//                counter += 1;
//            }
//        else {
//            lateralDir = (lateralDir + 1) % 4;
//            counter = 0;
//            System.out.println("Setting dir to down");
//            System.out.println(lateralDirections[lateralDir].name());
//        }


            // Attack only if the location is unpainted or has enemy paint
            if (!this.r.rc.senseMapInfo(this.r.rc.getLocation()).getPaint().isAlly()) {
                this.r.rc.attack(this.r.rc.getLocation());
            }


            if (this.r.rc.isMovementReady()) {
                //Direction moveDir = [Direction.NORTH];
                Direction moveDir = spiralDirections[spiralStep];
                if (this.r.rc.canMove(moveDir)) {

                    if (directionCount <= layerLoop) {
                        this.r.rc.move(moveDir);

                        directionCount++;
                    }
                    if (directionCount > layerLoop) {
                        directionCount = 0;
                        spiralStep++;
                    }
                    if (spiralStep == 4) {
                        spiralStep = 0;
                        layerLoop++;
                    }

                } else {
                    for (Direction dir : Direction.allDirections()) {
                        if (this.r.rc.canMove(dir)) {
                            this.r.rc.move(dir);
                            //layerLoop--;
                            return;
                        }
                    }
                }
            }
        }
    }


