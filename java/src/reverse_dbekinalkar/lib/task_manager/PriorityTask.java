package reverse_dbekinalkar.lib.task_manager;

import battlecode.common.GameActionException;

public class PriorityTask extends Task {
    public int priority;
    public int taskNo;
    Task t;

    public PriorityTask(Task t, int priority, int taskNo) {
        super(t.r);

        this.priority = priority;
        this.taskNo = taskNo;

        this.t = t;
    }

    public boolean setup() {
        return this.t.setup();
    }

    public void run() throws GameActionException {
        this.t.run();
    }
}