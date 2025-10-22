package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;
import java.util.List;


public class ScopeStatement implements Statement {

    private final List<Statement> statements;

    public ScopeStatement(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }

    public List<Statement> getStatements() {
        return statements;
    }    
}
