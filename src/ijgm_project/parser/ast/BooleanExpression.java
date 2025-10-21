package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

public class BooleanExpression implements Expression {
    private final boolean value;

    public BooleanExpression(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
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