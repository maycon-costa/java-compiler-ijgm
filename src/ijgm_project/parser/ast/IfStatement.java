package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;
import java.util.List;

public class IfStatement implements Statement {
    private final Expression condition;
    private final List<Statement> thenBody;
    private final List<Statement> elseBody;

    public IfStatement(Expression condition, List<Statement> thenBody, List<Statement> elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getThenBody() {
        return thenBody;
    }

    public List<Statement> getElseBody() {
        return elseBody;
    }

    /**
     * Implementação do método accept do Padrão Visitor (Refatorado).
     */
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}