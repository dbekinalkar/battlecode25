package dbekinalkar.robot.tasks;

import battlecode.common.*;
import dbekinalkar.lib.task_manager.Task;
import dbekinalkar.robot.Robot;
//import battlecode.common.MapLocation;

public class BuilderTask extends Task {
    public BuilderTask(Robot r) {
        super(r);
    }

    @Override
    public boolean setup() {
        return true;
    }

    // get location
    // if rc.getLocation()
    public void run() throws GameActionException {
        try {
            MapLocation myLocation = this.r.rc.getLocation();
            Direction[] directions = Direction.values();
            int[] weights = new int[directions.length];

            // Step 1: Assign weights to each direction
            for (int i = 0; i < directions.length; i++) {
                Direction dir = directions[i];
                MapLocation newLocation = myLocation.add(dir);

                // Default weight
                int weight = 100;

                // Increase weight for impassable or occupied tiles
                if (!this.r.rc.canMove(dir)) {
                    weight += 1000; // High penalty for invalid moves
                }

                // Add weight for proximity to enemies
                RobotInfo[] nearbyEnemies = this.r.rc.senseNearbyRobots(newLocation, this.r.rc.getType().actionRadiusSquared, this.r.rc.getTeam().opponent());
                weight += nearbyEnemies.length * 50;

                // Add weight for tiles further from a target (e.g., enemy tower or ruin)
                //MapLocation target = findTarget(this.r.rc); // Example: custom method to find a target
                MapLocation target = myLocation.add(dir);
                if (target != null) {
                    weight += newLocation.distanceSquaredTo(target);
                }

                // Store the weight
                weights[i] = weight;
            }

            // Step 2: Choose the direction with the lowest weight
            int minWeight = Integer.MAX_VALUE;
            Direction bestDirection = null;

            for (int i = 0; i < directions.length; i++) {
                if (weights[i] < minWeight) {
                    minWeight = weights[i];
                    bestDirection = directions[i];
                }
            }

            // Step 3: Move in the best direction
            if (bestDirection != null && this.r.rc.canMove(bestDirection)) {
                this.r.rc.move(bestDirection);
                System.out.println("Moved to: " + myLocation.add(bestDirection));
            } else {
                System.out.println("No valid move found.");
            }
        } catch (GameActionException e) {
            System.err.println("Exception occurred: " + e.getMessage());
        } finally {
            Clock.yield(); // End the turn
        }
    }
}


