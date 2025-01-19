package reverse_dbekinalkar.lib.task_manager;

import battlecode.common.GameActionException;
import reverse_dbekinalkar.robot.Robot;

public abstract class Task {

    protected Robot r;

    public Task(Robot r) {
        this.r = r;
    }

    public abstract boolean setup();

    public abstract void run() throws GameActionException;
}
