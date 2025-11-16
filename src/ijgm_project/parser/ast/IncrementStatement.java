package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

public class IncrementStatement implements Statement {
    private final String variableName;

    public IncrementStatement(String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

    /**
     * Implementação do método accept do Padrão Visitor.
     */
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}