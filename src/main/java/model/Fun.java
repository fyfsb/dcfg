package model;

import exceptions.memory.MemoryStructException;
import exceptions.typedef.TypeDefException;
import exceptions.typedef.TypeNotDefinedException;
import table.MemoryTable;
import table.TypeTable;
import tree.DTE;
import tree.TokenType;

import java.util.LinkedList;
import java.util.List;

public class Fun {
    @Override
    public String toString() {
        return "Function(name=" + name +
                ", returnType= " + returnType.name +
                ", memoryStruct= " + memoryStruct +
                ", numParameters= " + numParameters +
                ", body = " + body.toString().substring(0, 30) + "...";
    }

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

        public Fun build() throws TypeDefException, MemoryStructException {

            int numParameters;
            if (pads == null) {
                numParameters = 0;
            } else {
                numParameters = pads.getChildrenSize();
            }

            List<List<String>> componentPairs = extractComponentPairs();

            VarType.Builder structBuilder;
            VarType structType = null;
            Variable memoryStruct = null;

            if (!componentPairs.isEmpty()) {
                structBuilder = VarType.createStructTypeBuilder(componentPairs, name);
                structType = TypeTable.getInstance().createStructType(structBuilder);
                memoryStruct = new Variable("$" + name, 0, structType, 0);
                MemoryTable.getInstance().addMemory(memoryStruct);
            }


            return new Fun(name, returnType, memoryStruct, numParameters, body);
        }

        public List<List<String>> extractComponentPairs() {
            List<List<String>> result = new LinkedList<>();

            if (pads != null) {
                result.addAll(pads.extractComponentPairs());
            }
            if (vads != null) {
                result.addAll(vads.extractComponentPairs());
            }
            return result;
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

    public static Fun fromDTE(DTE fud) throws Exception {
        if (fud.token.type != TokenType.FuD) {
            throw new IllegalArgumentException("Expected FuD, got " + fud.token.type);
        }

        String returnType = fud.fson.getBorderWord();
        String name = fud.fson.bro.getBorderWord();
        DTE pads = null;
        DTE vads = null;
        DTE body;

        // getting to function parenthesis. If there are no parameters, current tree element is expected to be R_PAREN
        // fud.fson = ty
        // fud.fson.bro = na
        // fud.fson.bro.bro = (
        DTE nextElement = fud.fson.bro.bro.bro;
        if (nextElement.token.type == TokenType.PaDS) {
            pads = nextElement;
            nextElement = nextElement.bro;
        }

        // getting to function closure. If there are no variable declarations, current tree element is expected to be <body>
        nextElement = nextElement.bro.bro;
        if (nextElement.token.type == TokenType.VaDS) {
            vads = nextElement;
            nextElement = nextElement.bro;
        }

        body = nextElement;

        return new Builder()
                .setName(name)
                .setReturnType(returnType)
                .setPads(pads)
                .setVads(vads)
                .setBody(body)
                .build();

    }
}
