package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

public class FloatExpression implements Expression {
    private final Float value;

    public FloatExpression(String value) {
        this.value = Float.parseFloat(value);
    }

    public Float getValue() {
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