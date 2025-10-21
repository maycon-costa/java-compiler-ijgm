package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

public class NumberExpression implements Expression {
    private final int value;

    public NumberExpression(String value) {
        this.value = Integer.parseInt(value);
    }

    public int getValue() {
        return value;
    }

    /**
     * Implementação do método accept do Padrão Visitor (Refatorado).
     */
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}