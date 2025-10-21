package ijgm_project.parser.ast;

import ijgm_project.lexer.TokenType;
import ijgm_project.visitor.Visitor;

public class BinaryExpression implements Expression {
    private final Expression left;
    private final TokenType operator;
    private final Expression right;

    public BinaryExpression(Expression left, TokenType operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public TokenType getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    /**
     * Implementação do método accept do Padrão Visitor (Refatorado).
     */
    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visit(this);
    }
}