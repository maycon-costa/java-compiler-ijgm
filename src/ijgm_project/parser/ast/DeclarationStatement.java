package ijgm_project.parser.ast;

import ijgm_project.lexer.TokenType;
import ijgm_project.visitor.Visitor;

public class DeclarationStatement implements Statement {
    private final TokenType type;
    private final String variableName;

    public DeclarationStatement(TokenType type, String variableName) {
        this.type = type;
        this.variableName = variableName;
    }

    public TokenType getType() {
        return type;
    }

    public String getVariableName() {
        return variableName;
    }

    /**
     * Implementação do método accept do Padrão Visitor (Refatorado).
     */
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}