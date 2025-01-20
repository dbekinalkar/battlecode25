package dbekinalkar.lib.message;

public class Message {
    static int opcode;
    static int x;
    static int y;
    static int data;
    
    static int rounds;

    public Message() {
        opcode = 0;
        x = 0;
        y = 0;
        data = 0;
        rounds = 0;
    }

}