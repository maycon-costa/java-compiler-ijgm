package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;
import java.util.List;

public class WhileStatement implements Statement {
    private final Expression condition;
    private final List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getBody() {
        return body;
    }

    /**
     * Implementação do método accept do Padrão Visitor (Refatorado).
     */
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}