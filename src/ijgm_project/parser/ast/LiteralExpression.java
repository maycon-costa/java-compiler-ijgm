package ijgm_project.parser.ast;

// NÃO HÁ "import ijgm_project.visitor.Visitor;" (ISSO QUEBRA O CÍRCULO)

/**
 * Interface abstrata para todos os nós de expressão literal (ex: 10, 3.14, "oi").
 * Esta interface é implementada por NumberExpression, FloatExpression, etc.
 * Ela herda o método 'accept' da interface 'Expression'.
 */
public interface LiteralExpression extends Expression {
    /**
     * Retorna o valor literal (será autoboxed para Object).
     */
    Object getValue();

    // O MÉTODO 'accept' FOI REMOVIDO DAQUI.
    // Ele é herdado automaticamente da 'Expression'.
}