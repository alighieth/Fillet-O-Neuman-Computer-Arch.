import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Processor {
    public static int[] generalReg;
    public static int pc;
    static Memory mem = new Memory();
    boolean f = true;
    public static LinkedList<String[]> arr = new LinkedList<>();
    public static int numOfInstructions = 0;
    public final int r0 = 0;

    public Processor() {

        generalReg = new int[31];
        generalReg[r0] = r0;
        Processor.pc = 0;
    }

    public int fetch() {
        int val = Memory.fetchInst(pc);
        return val;
    }

    public static int execute(int opcode) {
        int res = -1;
        if (opcode == 0 || opcode == 1 || opcode == 2 || opcode == 5 || opcode == 8 || opcode == 9) {
            res = 0;
        }
        if (opcode == 3 || opcode == 4 || opcode == 10 || opcode == 11 || opcode == 6) {
            res = 1;
        }
        if (opcode == 7) {
            res = 2;
        }

        return res;
    }

    public static Instruction decodeCycle1(int instruction) {

        Instruction in = new Instruction();

        int opcode = (instruction & 0b11110000000000000000000000000000) >> 28;

        if (opcode == -1) {
            opcode = 8;
            instruction = -instruction;
        }
        if (opcode == -2) {
            opcode = 9;
            instruction = -instruction;
        }
        if (opcode == -3) {
            opcode = 10;
            instruction = -instruction;
        }
        if (opcode == -4) {
            opcode = 11;
            instruction = -instruction;
        }
        int r1 = (instruction & 0b00001111100000000000000000000000) >> 23;
        int r2 = (instruction & 0b00000000011111000000000000000000) >> 18;
        int r3 = (instruction & 0b00000000000000111110000000000000) >> 13;
        int shamt = (instruction & 0b00000000000000000001111111111111);
        int imm = (instruction & 0b00000000000000111111111111111111);
        int address = (instruction & 0b00001111111111111111111111111111);

        in.setOpcode(opcode);
        in.setR1(r1);
        in.setR2(r2);
        in.setR3(r3);
        in.setShamt(shamt);
        in.setImm(imm);
        in.setAddress(address);
        return in;

    }

    public static int[] decodeCycle2(Instruction s) {
        int valueR1 = generalReg[s.r1];
        int valueR2 = generalReg[s.r2];

        int[] a = { valueR1, valueR2 };

        s.setr1Value(valueR1);
        s.setr2Value(valueR2);
        return a;

    }

    public static void writeBack(int result, int rd) {
        if (rd == 0) {
            System.out.println("you cannot save in R0");
            return;
        }
        generalReg[rd] = result;
        System.out.println("Register : " + rd + " value changed to : " + generalReg[rd]);

    }

    public static int execRtype(int r1, int r2, int r3, int shamt, int opcode) {
        int result = 0;

        if (opcode == 0) {
            result = generalReg[r2] + generalReg[r3];
            // Processor.generalReg[r1]
        }
        if (opcode == 1) {
            result = generalReg[r2] - generalReg[r3];
        }
        if (opcode == 2) {
            result = generalReg[r2] * generalReg[r3];
        }
        if (opcode == 5) {
            result = generalReg[r2] & generalReg[r3];
        }
        if (opcode == 8) {
            result = generalReg[r2] << shamt;
        }
        if (opcode == 9) {
            result = generalReg[r2] >> shamt;
        }
        return result;
    }

    public static int execItype(int r1, int r2, int imm, int opcode) {
        int result = -1;
        if (opcode == 3) {
            // Processor.generalReg[r1]
            result = imm;
        }
        if (opcode == 4) {
            if (generalReg[r1] == generalReg[r2]) {
                Processor.pc = Processor.pc + imm;
                result = -1;
            }
        }
        if (opcode == 6) {
            result = generalReg[r2] ^ imm;
        }
        if (opcode == 10) {

            result = generalReg[r2] + imm;
        }
        if (opcode == 11) {
            result = generalReg[r2] + imm;
        }
        System.out.println("immediate = " + imm);
        return result;

    }

    public static void execJtype(int opcode, int address) {
        Processor.pc = address;
    }

    public void readFile(String filePath) {
        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;

            while ((line = br.readLine()) != null) {
                Processor.numOfInstructions++;
                String[] a = line.split(" ");
                sb.append(line);
                sb.append("\n");
                arr.add(a);
            }
            fr.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void parse(String[] decode) {

        System.out.println(Arrays.toString(decode));
        String result = "";
        switch (decode[0].toUpperCase()) {
            case ("ADD"):
                // add r1 r2 r3
                result += "0000";
                this.findRtype(result, decode, 0);
                break;
            case "SUB":
                // sub r1 r2 r3
                result += "0001";
                this.findRtype(result, decode, 0);
                break;
            case "MUL":
                // mul r1 r2 r3
                result += "0010";
                this.findRtype(result, decode, 0);
                break;

            case "MOVI":
                // movi r1 imm
                result += "0011";
                this.findItype(result, decode, 0);
                break;

            case "JEQ":
                // jeq r1 r2 imm
                result += "0100";
                this.findItype(result, decode, 1);
                break;

            case "AND":
                // AND r1 r2 r3
                result += "0101";
                this.findRtype(result, decode, 0);
                break;
            case "XORI":
                // xori r1 r2 imm
                result += "0110";
                this.findItype(result, decode, 1);
                break;
            case "JMP":
                // jmp add
                result += "0111";
                this.findJtyper(result, decode);
                break;
            case "LSL":
                // LSL r1 r2 shamt
                result += "-000";
                this.findRtype(result, decode, 1);
                break;
            case "LSR":
                // LS r r1 r2 shamt
                result += "-001";
                this.findRtype(result, decode, 1);
                break;
            case "MOVR":
                // movr r1 r2 imm
                result += "-010";
                this.findItype(result, decode, 1);
                break;
            case "MOVM":
                // movr r1 r2 imm
                result += "-011";
                this.findItype(result, decode, 1);
                break;
        }

        // we need to change the if condition based on the type ,i,j
    }

    public String register(String r, int type) {
        String reg = "";
        if (type == 0) {
            int length = r.length();
            reg += Integer.toBinaryString(Integer.parseInt(r.substring(1, length)));
        } else if (type == 1) {
            reg += Integer.toBinaryString(Integer.parseInt(r));
        }
        return reg;
    }

    public void findRtype(String result, String[] decode, int x) {

        if (x == 0) {
            // doesnt have shamt but have r3
            for (int i = 1; i < decode.length; i++) {
                String length = register(decode[i], 0);
                while (length.length() < 5) {
                    length = "0" + length;
                }

                result += length;
            }
            result += "0000000000000";

        }
        if (x == 1) {
            // doesnt have r3 but has shamt
            for (int i = 1; i < decode.length - 1; i++) {
                String length = register(decode[i], 0);
                while (length.length() < 5) {
                    length = "0" + length;
                }
                result += length;
            }
            result += "00000";
            int shamt = Integer.parseInt(decode[3]);
            String shamtstr = Integer.toBinaryString(shamt);
            while (shamtstr.length() < 13)
                shamtstr = "0" + shamtstr;
            result += shamtstr;
        }

        System.out.println(result);
        System.out.println("------------------------------------------------------");

        Memory.addIns(Integer.parseInt(result, 2));
    }

    public void findItype(String result, String[] decode, int x) {
        String registerValue = register(decode[1], 0);
        String imm = "";
        while (registerValue.length() < 5) {
            registerValue = "0" + registerValue;
        }
        result += registerValue;
        if (x == 0) {
            result += "00000";
            imm = register(decode[2], 1);
        }
        if (x == 1) {
            String registerValue1 = register(decode[2], 0);

            while (registerValue1.length() < 5) {
                registerValue1 = "0" + registerValue1;
            }
            result += registerValue1;
            imm = register(decode[3], 1);
        }

        while (imm.length() < 18) {
            imm = "0" + imm;
        }

        result += imm;

        System.out.println(result);
        System.out.println("------------------------------------------------------");

        Memory.addIns(Integer.parseInt(result, 2));
    }

    public void findJtyper(String result, String[] decode) {
        int address = Integer.parseInt(decode[1]);
        String addressStr = Integer.toBinaryString(address);
        while (addressStr.length() < 28) {
            addressStr = "0" + addressStr;
        }

        result += addressStr;

        System.out.println(result);
        System.out.println("------------------------------------------------------");
        Memory.addIns(Integer.parseInt(result, 2));
    }

    public void pipeline() {
        ArrayList<String> stages = new ArrayList<>();
        int cycle = 1;
        int totalNumOfCycles = (7 + ((Processor.numOfInstructions - 1) * 2));
        int counterInsructions = Processor.numOfInstructions;
        int execType = 0;
        int fetchedInstruction = 0;
        int rResult = 0;
        int decodeCounter = 0;
        int executeCOunter = 0;
        int memoryCounter = 0;
        int wbCounter = 0;
        int fetchCounter = 1;

        int instNum = 1;

        ArrayList<Instruction> q = new ArrayList<>();

        for (int i = 0; i < totalNumOfCycles; i++) {
            System.out.println("-----------------------------------------------------------------");
            System.out.println("Cycle " + cycle);

            System.out.println("PC for this instruction : " + Processor.pc);
            if ((cycle - 1) % 2 == 0 && counterInsructions > 0) {
                fetchedInstruction = fetch();
                counterInsructions--;
                if (fetchedInstruction != 0) {
                    stages.add("Fetched instruction : " + fetchCounter);
                    fetchCounter++;
                    Processor.pc++;
                }
            }
            if ((cycle - 7) % 2 == 0 && (cycle - 7) >= 0) {
                Instruction finalinstr = q.get(wbCounter);
                if (execType == 0) {
                    Processor.writeBack(finalinstr.rResult, finalinstr.r1);
                    stages.add(" Doing writeBack on instruction with opcode " + finalinstr.opcode);
                } else if (execType == 1) {
                    if (rResult >= 0) {
                        Processor.writeBack(finalinstr.rResult, finalinstr.r1);
                        stages.add("Doing writeBack on instruction with opcode " + finalinstr.opcode);
                    } else {
                        System.out.println("Doing writeBack on instruction with opcode " + finalinstr.opcode
                                + "but it doesnt need writeBack");
                    }
                } else {
                    System.out.println("Doing writeBack on instruction with opcode " + finalinstr.opcode
                            + "but it doesnt need writeBack");
                }
                wbCounter++;
            }
            if ((cycle - 6) % 2 == 0 && (cycle - 6) >= 0) {

                Instruction inst = q.get(memoryCounter);
                int x = Memory.accessMem(execType, inst.opcode, generalReg[inst.r2], inst.imm, inst.r1);
                if (x != -1) {
                    q.get(memoryCounter).rResult = x;
                }
                int newS = executeCOunter - 1;
                stages.add("Memory Access for instruction with opcode " + q.get(newS).opcode);
                memoryCounter++;

            }
            if ((cycle - 4) % 2 == 1 && (cycle - 4) >= 0) {
                Instruction r = q.get(executeCOunter);
                if (execType == 0) {
                    rResult = execRtype(r.r1, r.r2, r.r3, r.shamt, r.opcode);
                    q.get(executeCOunter).rResult = rResult;
                } else if (execType == 1) {

                    rResult = execItype(r.r1, r.r2, r.imm, r.opcode);
                    q.get(executeCOunter).rResult = rResult;

                } else {

                    execJtype(r.opcode, r.address);
                }

                stages.add("execute cycle 2 for instruction with opcode " + q.get(executeCOunter).opcode);
                executeCOunter++;

            }
            if ((cycle - 4) % 2 == 0 && (cycle - 4) >= 0) {
                execType = execute(q.get(executeCOunter).opcode);

                stages.add("execute cycle 1 for instruction with opcode " + q.get(executeCOunter).opcode);
            }
            if ((cycle - 2) % 2 == 1 && (cycle - 2) >= 0) {
                Instruction s = q.get(decodeCounter);
                decodeCycle2(s);

                stages.add("decode cycle 2 for instruction with opcode " + q.get(decodeCounter).opcode);

                decodeCounter++;
            }
            if ((cycle - 2) % 2 == 0 && (cycle - 2) >= 0) {
                q.add(decodeCycle1(fetchedInstruction));
                stages.add("decode cycle 1 for instruction with opcode " + q.get(decodeCounter).opcode);
                q.get(decodeCounter).printInstruction();
            }

            // m,ovi r3 2

            // only used for MOVR READ, MOVM WRITE
            // int[] a = { opcode, r1, r2, r3, shamt, imm, address };
            // int[] a = { valueR1, valueR2 };

            for (String ele : stages) {
                System.out.println(ele);
            }

            stages.clear();

            cycle++;

            System.out.println("GENERAL REGISTERS : " + Arrays.toString(Processor.generalReg));
            System.out.println("PC for next instruction : " + Processor.pc);
        }
        System.out.println("MEMORY FOR INSTRUCTIONS => ");
        System.out.println();
        System.out.println(Arrays.toString(Memory.inst1));
        System.out.println(Arrays.toString(Memory.inst2));
        System.out.println();
        System.out.println("MEMORY FOR DATA => ");
        System.out.println();
        System.out.println(Arrays.toString(Memory.data1));
        System.out.println(Arrays.toString(Memory.data2));

    }

    public static void main(String[] args) {

        Processor p = new Processor();
        p.readFile("/home/alighieth/CA");
        for (String[] element : Processor.arr) {
            p.parse(element);
        }
        p.pipeline();

    }
}