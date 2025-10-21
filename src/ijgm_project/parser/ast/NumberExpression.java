package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

// MUDANÇA: Implementa LiteralExpression em vez de Expression
public class NumberExpression implements LiteralExpression {
    private final int value;

    public NumberExpression(String value) {
        this.value = Integer.parseInt(value);
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