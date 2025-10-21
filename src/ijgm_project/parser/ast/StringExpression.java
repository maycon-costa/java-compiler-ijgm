package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

public class StringExpression implements Expression {
    private final String value;

    public StringExpression(String value) {
        this.value = value;
    }

    public String getValue() {
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