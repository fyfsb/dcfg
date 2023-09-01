package codegen;

import config.Configuration;

import java.util.List;

import static util.Context.*;

public class MemoryHelper {
    public static void increaseHeapPointer(int size) {
        List.of(
                "# INCREASING HEAP POINTER",
                Instruction.addi(HPT, HPT, size) + " # start of increasing hpt",
                Instruction.subi(1, HPT, HMAX),
                Instruction.bltz(1, 4),
                "macro: gpr(1) = x",
                Instruction.sysc(),
                Instruction.addi(1, HPT, -size),
                Instruction.addi(2, 0, size / 4),
                "zero(1, 2) # end of increasing hpt"
        ).forEach(CodeGenerator.getInstance()::addInstruction);
    }

    public static void increaseStackPointer(int size) {
        int reg = Configuration.getInstance().getFirstFreeRegister();
        List.of(
                Instruction.addi(reg, SPT, size) + " # start of increasing spt",
                Instruction.subi(reg, reg, SMAX),
                Instruction.blez(reg, 4),
                "macro: gpr(1) = x",
                Instruction.sysc(),
                Instruction.addi(SPT, SPT, size + 4) + " # end of increasing spt"
        ).forEach(CodeGenerator.getInstance()::addInstruction);
        Configuration.getInstance().freeRegister(reg);
    }
}
