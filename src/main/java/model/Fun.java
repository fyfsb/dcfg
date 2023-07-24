package model;

import exceptions.typedef.TypeDefException;
import exceptions.typedef.TypeNotDefinedException;
import table.TypeTable;
import tree.DTE;

import java.util.LinkedList;
import java.util.List;

public class Fun { // record of the function, function table
    // function call, inline result destination address
    private final String name;
    private final VarType returnType; //
    // no rda
    private final Variable memoryStruct; // $f = {int a, int b, char c} merge <PaDS> + <VaDS>
    /*
    int f(int a, int b) {
        char c;
        return 1
    }

     */
    private final int numParameters;
    private final DTE body;


    public Fun(String name,
               VarType returnType,
               Variable memoryStruct,
               int numParameters,
               DTE body) {
        this.name = name;
        this.returnType = returnType;
        this.memoryStruct = memoryStruct;
        this.numParameters = numParameters;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public VarType getReturnType() {
        return returnType;
    }

    public Variable getMemoryStruct() {
        return memoryStruct;
    }

    public int getNumParameters() {
        return numParameters;
    }

    public DTE getBody() {
        return body;
    }

    public static class Builder {
        private String name;
        private VarType returnType;

        private DTE pads;
        private DTE vads;
        private DTE body;

        public Fun build() throws TypeDefException {
            int numParameters = pads.getChildrenSize();

            List<List<String>> componentPairs = extractComponentPairs();

            VarType.Builder structBuilder = VarType.createStructTypeBuilder(componentPairs, name);
            VarType structType = TypeTable.getInstance().createStructType(structBuilder);

            Variable memoryStruct = new Variable("$" + name, 0, structType, 0);
            return new Fun(name, returnType, memoryStruct, numParameters, body);
        }

        public List<List<String>> extractComponentPairs() {
            List<List<String>> result = new LinkedList<>();

            extractComponentPairsTo(pads, result);
            extractComponentPairsTo(vads, result);
            return result;
        }

        public void extractComponentPairsTo(DTE dte, List<List<String>> compPairs) {
            List<DTE> fseq = dte.getFlattenedSequence(); // List<VaD>
            for (DTE vad : fseq) {
                System.out.println(pads.fson.fson.token.value);

                String type = vad.fson.token.value;
                String name = vad.fson.bro.token.value;

                assert type != null;
                assert name != null;
                compPairs.add(List.of(name, type));
            }
        }

        public String getName() {
            return name;
        }

        public VarType getReturnType() {
            return returnType;
        }

        public DTE getBody() {
            return body;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setReturnType(String returnType) throws TypeNotDefinedException {
            this.returnType = TypeTable.getInstance().getType(returnType);
            return this;
        }

        public Builder setPads(DTE pads) {
            this.pads = pads;
            return this;
        }

        public Builder setVads(DTE vads) {
            this.vads = vads;
            return this;
        }

        public Builder setBody(DTE body) {
            this.body = body;
            return this;
        }
    }
}
