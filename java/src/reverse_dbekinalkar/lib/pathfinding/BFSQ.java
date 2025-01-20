package reverse_dbekinalkar.lib.pathfinding;

public class BFSQ {
    private static final int MAX_LENGTH = 500;

    private int head;
    private int tail;
    private int round_tail;

    public int round;

    public int[][][] q;


    public BFSQ() {
        q = new int[MAX_LENGTH][2][2];
    }

    public void setup() {
        head = 0;
        tail = 0;
        round = 0;
        round_tail = 0;
    }

    public void add(int x, int y, int oldx, int oldy) {
        q[tail][0][0] = x;
        q[tail][0][1] = y;
        q[tail][1][0] = oldx;
        q[tail][1][1] = oldy;

        tail++;
        tail %= MAX_LENGTH;
    }

    public void pop(int[][] loc) {
        peek(loc);

        if (head == round_tail) {
            round++;
            round_tail = tail;
        }

        head++;
        head %= MAX_LENGTH;

    }

    public void peek(int[][] loc) {
        loc[0][0] = q[head][0][0];
        loc[0][1] = q[head][0][1];
        loc[1][0] = q[head][1][0];
        loc[1][1] = q[head][1][1];
    }
}
