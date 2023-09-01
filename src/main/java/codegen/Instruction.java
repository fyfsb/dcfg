package codegen;

public class Instruction {

    // I type
    public static String lw(int rt, int rs, int imm) {
        return getIType("lw", rt, rs, imm);
    }

    public static String sw(int rt, int rs, int imm) {
        return getIType("sw", rt, rs, imm);
    }

    public static String addi(int rt, int rs, int imm) {
        return getIType("addi", rt, rs, imm);
    }

    public static String subi(int rt, int rs, int imm) {
        return getIType("subi", rt, rs, imm);
    }

    public static String addiu(int rt, int rs, int imm) {
        return getIType("addiu", rt, rs, imm);
    }

    public static String slti(int rt, int rs, int imm) {
        return getIType("slti", rt, rs, imm);
    }

    public static String sltiu(int rt, int rs, int imm) {
        return getIType("sltiu", rt, rs, imm);
    }

    public static String andi(int rt, int rs, int imm) {
        return getIType("andi", rt, rs, imm);
    }

    public static String ori(int rt, int rs, int imm) {
        return getIType("ori", rt, rs, imm);
    }

    public static String xori(int rt, int rs, int imm) {
        return getIType("xori", rt, rs, imm);
    }

    public static String lui(int rt, int rs, int imm) {
        return getIType("lui", rt, rs, imm);
    }

    public static String bltz(int rs, int imm) {
        return getBranch("bltz", rs, imm);
    }

    public static String bgez(int rs, int imm) {
        return getBranch("bgez", rs, imm);
    }

    public static String beq(int rs, int imm) {
        return getBranch("beq", rs, imm);
    }

    public static String beqz(int rs, int imm) {
        return getBranch("beqz", rs, imm);
    }

    public static String bne(int rs, int imm) {
        return getBranch("bne", rs, imm);
    }

    public static String blez(int rs, int imm) {
        return getBranch("blez", rs, imm);
    }

    public static String bgtz(int rs, int imm) {
        return getBranch("bgtz", rs, imm);
    }

    // R Type
    public static String srl(int rd, int rs, int sa) {
        return getRtype("srl", rd, rs, sa);
    }

    public static String add(int rd, int rs, int rt) {
        return getRtype("add", rd, rs, rt);
    }

    public static String addu(int rd, int rs, int rt) {
        return getRtype("addu", rd, rs, rt);
    }

    public static String sub(int rd, int rs, int rt) {
        return getRtype("sub", rd, rs, rt);
    }

    public static String subu(int rd, int rs, int rt) {
        return getRtype("subu", rd, rs, rt);
    }

    public static String and(int rd, int rs, int rt) {
        return getRtype("and", rd, rs, rt);
    }

    public static String or(int rd, int rs, int rt) {
        return getRtype("or", rd, rs, rt);
    }

    public static String xor(int rd, int rs, int rt) {
        return getRtype("xor", rd, rs, rt);
    }

    public static String nor(int rd, int rs, int rt) {
        return getRtype("nor", rd, rs, rt);
    }

    public static String slt(int rd, int rs, int rt) {
        return getRtype("slt", rd, rs, rt);
    }

    public static String sltu(int rd, int rs, int rt) {
        return getRtype("sltu", rd, rs, rt);
    }

    public static String jr(int rs) {
        return "jr " + rs;
    }

    public static String jalr(int rd, int rs) {
        return "jalr " + rd + " " + rs;
    }

    public static String sysc() {
        return "sysc";
    }

    public static String eret() {
        return "eret";
    }

    public static String movg2s(int rd, int rt) {
        return "movg2s " + rd + " " + rt;
    }

    public static String movs2g(int rd, int rt) {
        return "movs2g " + rd + " " + rt;
    }

    // J Type
    public static String j(int iindex) {
        return "j " + iindex;
    }

    public static String jal(String iindex) {
        return "jal " + iindex;
    }

    // Helpers
    public static String deref(int reg) {
        return lw(reg, reg, 0);
    }

    private static String getIType(String name, int rt, int rs, int imm) {
        return name + " $" + rt + " $" + rs + " " + imm; // "lw $1 $27 0"
    }

    private static String getRtype(String name, int rd, int rs, int rt) {
        return name + " $" + rd + " $" + rs + " $" + rt;
    }

    private static String getBranch(String name, int rs, int imm) {
        return name + " " + rs + " " + imm;
    }
}
