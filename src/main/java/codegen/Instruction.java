package codegen;

import assembly.Assembly.*;
public class Instruction {

    public final Mnemonic instruction;
    public final Integer rt;
    public final Integer rs;
    public final Integer rd;
    public final Integer sa;
    public final Integer imm;
    public final Integer iindex;
    private Instruction(Mnemonic instruction, Integer rt, Integer rs, Integer imm) {
        this.instruction = instruction;
        this.rt = rt;
        this.rs = rs;
        this.rd = null;
        this.sa = null;
        this.imm = imm;
        this.iindex = null;
    }

    public Instruction(Mnemonic instruction, Integer rd, Integer rs, Integer rt, Integer sa) {
        this.instruction = instruction;
        this.rt = rt;
        this.rs = rs;
        this.rd = rd;
        this.sa = sa;
        this.imm = null;
        this.iindex = null;
    }

    public Instruction(Mnemonic instruction, Integer iindex) {
        this.instruction = instruction;
        this.rt = null;
        this.rs = null;
        this.rd = null;
        this.sa = null;
        this.imm = null;
        this.iindex = iindex;
    }

    // I type
    public static Instruction lw(int rt, int rs, int imm) {
        return getIType(Mnemonic.LW, rt, rs, imm);
    }

    public static Instruction sw(int rt, int rs, int imm) {
        return getIType(Mnemonic.SW, rt, rs, imm);
    }

    public static Instruction addi(int rt, int rs, int imm) {
        return getIType(Mnemonic.ADDI, rt, rs, imm);
    }

    public static Instruction subi(int rt, int rs, int imm) {
        return getIType(Mnemonic.SUBI, rt, rs, imm);
    }

    public static Instruction addiu(int rt, int rs, int imm) {
        return getIType(Mnemonic.ADDIU, rt, rs, imm);
    }

    public static Instruction slti(int rt, int rs, int imm) {
        return getIType(Mnemonic.SLTI, rt, rs, imm);
    }

    public static Instruction sltiu(int rt, int rs, int imm) {
        return getIType(Mnemonic.SLTIU, rt, rs, imm);
    }

    public static Instruction andi(int rt, int rs, int imm) {
        return getIType(Mnemonic.ANDI, rt, rs, imm);
    }

    public static Instruction ori(int rt, int rs, int imm) {
        return getIType(Mnemonic.ORI, rt, rs, imm);
    }

    public static Instruction xori(int rt, int rs, int imm) {
        return getIType(Mnemonic.XORI, rt, rs, imm);
    }

    public static Instruction lui(int rt, int rs, int imm) {
        return getIType(Mnemonic.LUI, rt, rs, imm);
    }

    public static Instruction bltz(int rs, int imm) {
        return getIType(Mnemonic.BLTZ, null, rs, imm);
    }

    public static Instruction bgez(int rs, int imm) {
        return getIType(Mnemonic.BGEZ, null, rs, imm);
    }

    public static Instruction beq(int rs, int imm) {
        return getIType(Mnemonic.BEQ, null, rs, imm);
    }

    public static Instruction beqz(int rs, int imm) {
        return getBranch("beqz", rs, imm);
    }

    public static Instruction bne(int rs, int imm) {
        return getIType(Mnemonic.BNE, null, rs, imm);
    }

    public static Instruction blez(int rs, int imm) {
        return getIType(Mnemonic.BLEZ, null, rs, imm);
    }

    public static Instruction bgtz(int rs, int imm) {
        return getIType(Mnemonic.BGTZ, null, rs, imm);
    }

    // R Type
    public static Instruction srl(int rd, int rs, int sa) {
        return getRtype(Mnemonic.SRL, rd, rs, null, sa);
    }

    public static Instruction add(int rd, int rs, int rt) {
        return getRtype(Mnemonic.ADD, rd, rs, rt, null);
    }

    public static Instruction addu(int rd, int rs, int rt) {
        return getRtype(Mnemonic.ADDU, rd, rs, rt, null);
    }

    public static Instruction sub(int rd, int rs, int rt) {
        return getRtype(Mnemonic.SUB, rd, rs, rt, null);
    }

    public static Instruction subu(int rd, int rs, int rt) {
        return getRtype(Mnemonic.SUBU, rd, rs, rt, null);
    }

    public static Instruction and(int rd, int rs, int rt) {
        return getRtype(Mnemonic.AND, rd, rs, rt, null);
    }

    public static Instruction or(int rd, int rs, int rt) {
        return getRtype(Mnemonic.OR, rd, rs, rt, null);
    }

    public static Instruction xor(int rd, int rs, int rt) {
        return getRtype(Mnemonic.XOR, rd, rs, rt, null);
    }

    public static Instruction nor(int rd, int rs, int rt) {
        return getRtype(Mnemonic.NOR, rd, rs, rt, null);
    }

    public static Instruction slt(int rd, int rs, int rt) {
        return getRtype(Mnemonic.SLT, rd, rs, rt, null);
    }

    public static Instruction sltu(int rd, int rs, int rt) {
        return getRtype(Mnemonic.SLTU, rd, rs, rt, null);
    }

    public static Instruction jr(int rs) {
        return getRtype(Mnemonic.JR, null, rs, null, null);
    }

    public static Instruction jalr(int rd, int rs) {
        return getRtype(Mnemonic.JALR, rd, rs, null, null);
    }

    public static Instruction sysc() {
        return getRtype(Mnemonic.SYSC, null, null, null, null);
    }

    public static Instruction eret() {
        return getRtype(Mnemonic.ERET, null, 16, null, null);
    }

    public static Instruction movg2s(int rd, int rt) {
        return getRtype(Mnemonic.MOVG2S, null, 4, null, null);
    }

    public static Instruction movs2g(int rd, int rt) {
        return getRtype(Mnemonic.MOVS2G, null, 0, null, null);
    }

    // J Type
    public static Instruction j(int iindex) {
        return getJtype(Mnemonic.J, iindex);
    }

    public static Instruction jal(int iindex) {
        return getJtype(Mnemonic.JAL, iindex);
    }

    // Helpers
    public static Instruction deref(int reg) {
        return lw(reg, reg, 0);
    }

    private static Instruction getIType(Mnemonic instruction, Integer rt, Integer rs, Integer imm) {
        return new Instruction(instruction, rt, rs, imm); // "lw $1 $27 0"
    }

    private static Instruction getRtype(Mnemonic instruction, Integer rd, Integer rs, Integer rt, Integer sa) {
        return new Instruction(instruction, rd, rs, rt, sa);
    }

    private static Instruction getJtype(Mnemonic instruction, Integer iindex){
        return new Instruction(instruction, iindex);
    };
    public String assemble() {
        return instruction.getOpcode() + Integer.parseInt(rt.toString(), 2);
    }
}
