package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Representa o comando de saída 'print' na Árvore de Sintaxe Abstrata (AST).
 * Este é um nó de comando que contém uma única expressão, cujo valor deve ser
 * exibido.
 * Implementa a interface Statement.
 */
public class PrintStatement implements Statement {
    private final Expression expression;

    /**
     * Construtor do nó PrintStatement.
     * 
     * @param expression A expressão cujo valor (resultado) será impresso no
     *                   console.
     */
    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    /**
     * Obtém a expressão que deve ser avaliada e impressa.
     * 
     * @return O nó Expression filho.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Implementação do método accept do Padrão Visitor.
     * Permite que o Visitor (Intérprete) execute a lógica de saída.
     * 
     * @param visitor O objeto Visitor que irá processar este nó.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}