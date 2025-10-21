package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

public class AssignStatement implements Statement {
    private final String variableName;
    private final Expression expression;

    public AssignStatement(String variableName, Expression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    public String getVariableName() {
        return variableName;
    }

    public Expression getExpression() {
        return expression;
    }

    /**
     * Implementação do método accept do Padrão Visitor (Refatorado).
     */
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}