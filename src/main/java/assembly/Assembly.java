package assembly;

public class Assembly {
    public enum Mnemonic {
        // I TYPE
        LW("100011", null, "lw"),
        SW("101011", null, "sw"),
        ADDI("001000", null, "addi"),
        SUBI("001010", null, "subi"),
        ADDIU("001001", null, "addiu"),
        SLTI("001010", null, "slti"),
        SLTIU("001011", null, "sltiu"),
        ANDI("001100", null, "andi"),
        ORI("001101", null, "ori"),
        XORI("001110", null, "xori"),
        LUI("001111", null, "lui"),
        BLTZ("000001", null, "bltz"),
        BGEZ("000001", null, "bgez"),
        BEQ("000100", null, "beq"),
        BNE("000101", null, "bne"),
        BLEZ("000110", null, "blez"),
        BGTZ("000111", null, "bgtz"),

        // R TYPE
        SRL("000000", "000010", "srl"),
        ADD("000000", "100000", "add"),
        ADDU("000000", "100001", "addu"),
        SUB("000000", "100010", "sub"),
        SUBU("000000", "100011", "subu"),
        AND("000000", "100100", "and"),
        OR("000000", "100101", "or"),
        XOR("000000", "100110", "xor"),
        NOR("000000", "100111", "nor"),
        SLT("000000","101010", "slt"),
        SLTU("000000", "101011", "sltu"),
        JR("000000", "001000", "jr"),
        JALR("000000", "001001", "jalr"),
        SYSC("000000", "001100", "sysc"),
        ERET("010000", "011000", "eret"),
        MOVG2S("010000", "000000", "movg2s"),
        MOVS2G("010000", "000000", "movs2g"),

        //J TYPE
        J("000010", null, "j"),
        JAL("000011", null, "jal");
        private final String opcode;
        private final String fun;
        private final String name;

        Mnemonic(String opcode, String fun, String name) {
            this.opcode = opcode;
            this.fun = fun;
            this.name = name;
        }

        public String getOpcode() {
            return this.opcode;
        }

        public String getFun(){
            return this.fun;
        }

        public String getName() {
            return this.name;
        }
        }
}
