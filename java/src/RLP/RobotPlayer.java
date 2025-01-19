package RLP;
import battlecode.common.*;

public class RobotPlayer {
    static final int nStates = 16; // Number of states in the grid world
    static final int nActions = 4; // Number of possible actions (up, down, left, right)
    static final int goalState = 15; // Goal state
    static final double learningRate = 0.8;
    static final double discountFactor = 0.95;
    static final double explorationProb = 0.2;
    static final int epochs = 1000;

    static double[][] QTable = new double[nStates][nActions]; // Initialize Q-table with zeros

    public static void run(RobotController rc) throws GameActionException {
        // Train Q-learning model during setup phase
        trainQLearning();

        // Main robot execution loop
        while (true) {
            try {
                // Use the Q-table to make decisions (example decision-making logic)
                int currentState = getCurrentState(rc);
                int action = selectAction(currentState);

                // Execute the selected action
                performAction(rc, action);

                Clock.yield(); // End the robot's turn
            } catch (Exception e) {
//                System.out.println("Exception: " + e.getMessage());
            }
        }
    }

    private static void trainQLearning() {
        for (int epoch = 0; epoch < epochs; epoch++) {
            int currentState = (int) (Math.random() * nStates); // Start from a random state

            while (currentState != goalState) {
                // Choose action with epsilon-greedy strategy
                int action;
                if (Math.random() < explorationProb) {
                    action = (int) (Math.random() * nActions); // Explore
                } else {
                    action = getBestAction(currentState); // Exploit
                }

                // Simulate the environment (next state logic)
                int nextState = (currentState + 1) % nStates;

                // Define a simple reward function
                double reward = (nextState == goalState) ? 1.0 : 0.0;

                // Update Q-value using the Q-learning update rule
                QTable[currentState][action] += learningRate * (reward +
                        discountFactor * getMaxQValue(nextState) - QTable[currentState][action]);

                currentState = nextState; // Move to the next state
            }
        }
    }

    private static int getCurrentState(RobotController rc) {
        // Example state retrieval logic (replace with actual Battlecode state logic)
        return (int) (Math.random() * nStates);
    }

    private static int selectAction(int state) {
        return getBestAction(state); // Example: always exploit
    }

    private static void performAction(RobotController rc, int action) throws GameActionException {
        // Example action execution logic (replace with actual Battlecode action logic)
        if (action == 0) {
            rc.move(Direction.NORTH);
        } else if (action == 1) {
            rc.move(Direction.SOUTH);
        } else if (action == 2) {
            rc.move(Direction.WEST);
        } else if (action == 3) {
            rc.move(Direction.EAST);
        }
    }

    private static int getBestAction(int state) {
        int bestAction = 0;
        double maxQValue = QTable[state][0];
        for (int i = 1; i < nActions; i++) {
            if (QTable[state][i] > maxQValue) {
                maxQValue = QTable[state][i];
                bestAction = i;
            }
        }
        return bestAction;
    }

    private static double getMaxQValue(int state) {
        double maxQValue = QTable[state][0];
        for (int i = 1; i < nActions; i++) {
            if (QTable[state][i] > maxQValue) {
                maxQValue = QTable[state][i];
            }
        }
        return maxQValue;
    }
}
