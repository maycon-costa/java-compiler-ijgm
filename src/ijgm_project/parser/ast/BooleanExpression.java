package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

// MUDANÇA: Implementa LiteralExpression em vez de Expression
public class BooleanExpression implements LiteralExpression {
    private final boolean value;

    public BooleanExpression(boolean value) {
        this.value = value;
    }

    @Override // MUDANÇA: Adicionado Override e tipo de retorno Object (autoboxed)
    public Object getValue() {
        return value;
    }

    /**
     * Implementação do método accept do Padrão Visitor (Refatorado).
     * Agora chama o visit(LiteralExpression)
     */
    @Override
    public <R> R accept(Visitor<R> visitor) {
        // Esta chamada agora roteia para visit(LiteralExpression)
        return visitor.visit(this);
    }
}