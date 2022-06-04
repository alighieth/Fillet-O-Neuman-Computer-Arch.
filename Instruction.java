public class Instruction {
    protected String num;
    protected int opcode;
    protected int r1;
    protected int r2;
    protected int r3;
    protected int r1Value;
    protected int r2Value;
    protected int r3Value;
    protected int shamt;
    protected int imm;
    protected int address;
    protected int rResult;

    public void setOpcode(int opcode) {
        this.opcode = opcode;
    }

    public void setr1Value(int v) {
        this.r1Value = v;
    }

    public void setr2Value(int v) {
        this.r2Value = v;
    }

    public void setr3Value(int v) {
        this.r2Value = v;
    }

    public void setR1(int r1) {
        this.r1 = r1;
    }

    public void setR2(int r2) {
        this.r2 = r2;
    }

    public void setR3(int r3) {
        this.r3 = r3;
    }

    public void setShamt(int shamt) {
        this.shamt = shamt;
    }

    public void setImm(int imm) {
        this.imm = imm;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void printInstruction() {
        System.out.println("Opcode: " + opcode);
        System.out.println("R1: " + r1);
        System.out.println("R2: " + r2);
        System.out.println("R3: " + r3);
        System.out.println("Shamt: " + shamt);
        System.out.println("Immediate: " + imm);
        System.out.println("Address: " + address);
    }
}