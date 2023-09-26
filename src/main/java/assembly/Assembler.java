package assembly;

import codegen.Instruction;

import java.util.List;
import java.util.Map;

public class Assembler {
    public static void assemble( Map<String, List<Instruction>> functionInstructions){
        functionInstructions.forEach((key, value) -> {
//            if (!key.equals("main")) {
//                System.out.println("_" + key);
                for(Instruction instruction : value){
                    System.out.println(instruction.assemble());
                }
//                System.out.println();
//            }
        });
    }
}
