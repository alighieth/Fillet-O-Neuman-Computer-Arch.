import java.util.Arrays;

public class Memory {
    public static int counterInst = 0;
    public static int counterData = 0;
    static int[] inst1 = new int[511]; // 0 -> 511
    static int[] inst2 = new int[511]; // 512 -> 1023
    static int[] data1 = new int[511]; // 1024 -> 1535
    static int[] data2 = new int[511]; // 1536 -> 2047

    public static void insertMem(int address, int value) {
        int mul1 = (512);
        int mul2 = (512 * 2);
        int mul3 = (512 * 3);

        if (address > 0 && address <= 511)
            inst1[address] = value;

        if (address >= 512 && address < 1023)
            inst2[address - mul1] = value;

        if (address >= 1024 && address < 1535)
            data1[address - mul2] = value;

        if (address >= 1535 && address < 2047)
            data2[address - mul3] = value;
    }

    public static int accessMem(int execType, int opcode, int valueR2, int imm, int r1) {
        int memFetch = -1;
        if (execType == 1) {
            if (opcode == 10) {
                int adr = valueR2 + imm;
                memFetch = Memory.fetch(adr + 1024);
                System.out.println("a7aaaaaaaaaaaaaaaaaaaa : " + memFetch);
            } else if (opcode == 11) {
                int adr = valueR2 + imm;
                Memory.insertMem(adr + 1024, Processor.generalReg[r1]);
                System.out.println("MEMORY  : ");
                System.out.println("The value in address " + adr + " becomes " + Processor.generalReg[r1]);
            }
        } else {
            System.out.println("Memory Accessed but no effect took place");
        }
        return memFetch;
    }

    public static int fetch(int address) {
        int mul1 = (512);
        int instruction = 0;
        if (address <= 511 && address >= 0)
            instruction = (data1[address]);

        if (address > 511 && address <= 1023)
            instruction = (data2[address - mul1]);
        System.out.println("Fetching Instruction : " + instruction);
        return instruction;
    }

    public static int fetchInst(int address) {
        int mul1 = (512);
        int instruction = 0;
        if (address <= 511 && address >= 0)
            instruction = (inst1[address]);

        if (address > 511 && address <= 1023)
            instruction = (inst1[address - mul1]);
        return instruction;
    }

    public static void addData(int x) {
        if (counterData < 511) {
            Memory.data1[counterData] = Math.toIntExact(x);
        } else {
            Memory.data2[counterData] = Math.toIntExact(x);
        }
        Memory.counterData++;
    }

    public static void addIns(int x) {
        if (counterInst < 511) {
            Memory.inst1[counterInst] = Math.toIntExact(x);
        } else {
            Memory.inst2[counterInst] = Math.toIntExact(x);
        }
        Memory.counterInst++;
    }
}
