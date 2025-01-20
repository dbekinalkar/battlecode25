package dbekinalkar.lib.message;

public class EncodeDecode {
    
    final static int posInfoOpCode = 0b0000;
    final static int buildUnitsOpCode = 0b0001;
    final static int queueAttackOpCode = 0b0004;
    /*
    Opcodes
    1 Position Opcodes
        [opcode (4) | x (6) | y (6) | data (16)]
        0000 - Position Info
        0001 - Build Units
        0010 - 
        0011 - 
        0100 - Queue Attack

     */
    static public int encodeQueueAttack(int x, int y, int round){
        return encodeP(posInfoOpCode, x, y, round);
    }

    static Message decodeBytes(int bytes){
        Message msg = new Message();
        msg.opcode = bytes >>> 28;
        if (msg.opcode < 0b0101){
            msg.x = (bytes >>> 22) & 0x3F;
            msg.y = (bytes >>> 22) & 0x3F;
            msg.rounds = bytes & 0xFFFF;
        }
        return msg;
    }

    static private int encodeP(int opcode, int x, int y, int data){
        int msg = opcode << 28 | x << 22 | y << 16 | data;
        return msg;
    }
}
