package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

// MUDANÇA: Implementa LiteralExpression em vez de Expression
public class FloatExpression implements LiteralExpression {
    private final Float value;

    public FloatExpression(String value) {
        this.value = Float.valueOf(value);
    }

    @Override // MUDANÇA: Adicionado Override e tipo de retorno Object
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