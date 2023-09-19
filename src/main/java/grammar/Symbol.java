package grammar;

import java.util.HashSet;

public class Symbol {

    public enum Type {
        Terminal, Nonterminal
    }

    private final String content;
    private final Type type;

    public Symbol(String content, Type type) {
        this.content = content;
        this.type = type;
    }

    public boolean isTerminal() {
        return type == Type.Terminal;
    }

    public int length() {
        return content.length();
    }

    @Override
    public String toString() {
        return content;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Symbol symbol = (Symbol) obj;
        return type.equals(symbol.getType()) && content.equals(symbol.getContent());
    }

    @Override
    public int hashCode() {
        int result = content.hashCode();
        result = 31 * result + (type == Type.Terminal ? 1 : 0);
        return result;
    }

    public static Symbol firstSymbolInString(String str, Grammar g) throws IllegalArgumentException {

        HashSet<Symbol> terminals = g.getTerminals();
        HashSet<Symbol> nonterminals = g.getNonterminals();

        for (int i = 0; i < str.length(); i++) {
            String subStr = str.substring(0, str.length() - i);

            Symbol nonterminal = new Symbol(subStr, Symbol.Type.Nonterminal);
            if (nonterminals.contains(nonterminal)) {
                return nonterminal;
            }

            Symbol terminal = new Symbol(subStr, Symbol.Type.Terminal);
            if (terminals.contains(terminal)) {
                return terminal;
            }
        }

        throw new IllegalArgumentException("Can't find the first symbol in this string:  " + str);
    }

    // Return all the symbols that can be derived from the given symbol
    public static HashSet<Symbol> lookaheadsFromSymbol(Symbol symbol, HashSet<Symbol> symbols, Grammar g) {
        HashSet<Symbol> lookaheads = new HashSet<>();
        symbols.add(symbol);

        if (symbol.isTerminal()) {
            lookaheads.add(symbol);
            return lookaheads;
        } else {
            for (Production production : g.getProductions()) {
                if (production.getLeft().equals(symbol)) {
                    Symbol derivedSymbol = production.getRight().get(0);
                    if (!symbols.contains((derivedSymbol))) {
                        lookaheads.addAll((lookaheadsFromSymbol(derivedSymbol, symbols, g)));
                    }
                }
            }
        }

        return lookaheads;
    }

    public String getContent() {
        return content;
    }

    public Type getType() {
        return type;
    }

}
