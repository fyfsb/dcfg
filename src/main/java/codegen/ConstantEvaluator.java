package codegen;

import config.Configuration;
import grammar.Symbol;
import model.VarReg;
import model.VarType;
import tree.DTE;

import java.util.List;

import static util.TypeUtils.checkTokenType;

public class ConstantEvaluator {
    private static CodeGenerator cg() {
        return CodeGenerator.getInstance();
    }

    public static VarReg evaluateNumberConstant(DTE constant) {
        checkTokenType(constant, "<C>");

        int register = Configuration.getInstance().getFirstFreeRegister();
        String value = constant.getBorderWord();

        int intValue;
        VarType type;

        if (value.charAt(value.length() - 1) != 'u') {
            intValue = Integer.parseInt(value);
            type = VarType.INT_TYPE;
        } else {
            intValue = Integer.parseInt(value.substring(0, value.length() - 1));
            type = VarType.UINT_TYPE;
        }

        String instr = Instruction.addi(register, register, intValue);
        cg().addInstruction(instr);

        return new VarReg(register, type);
    }

    public static VarReg evaluateBooleanConstant(DTE bc) {
        checkTokenType(bc, "<BC>");

        int register = Configuration.getInstance().getFirstFreeRegister();
        int value = bc.getFirstSon().labelContent().equals("true") ? 1 : 0;

        String instr = Instruction.addi(register, register, value);
        cg().addInstruction(instr);

        return new VarReg(register, VarType.BOOL_TYPE);
    }

    public static VarReg evaluateCharacterConstant(DTE charConstant) {
        String value = charConstant.getBorderWord();

        List<String> terminals = cg().g.getTerminals().stream().map(Symbol::getContent).filter(term -> term.length() == 1).toList();
        if (!terminals.contains(value)) {
            throw new IllegalArgumentException("Expected char constant, got " + value);
        }

        int register = Configuration.getInstance().getFirstFreeRegister();

        String instr = Instruction.addi(register, register, value.charAt(0));
        cg().addInstruction(instr);

        return new VarReg(register, VarType.CHAR_TYPE);
    }
}
