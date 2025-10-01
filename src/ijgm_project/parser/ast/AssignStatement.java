package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Representa um comando de Atribuição (ex: x = 10 + y;) na Árvore de Sintaxe
 * Abstrata (AST).
 * Este nó de comando é composto por uma variável de destino e uma expressão de
 * valor.
 * Implementa a interface Statement.
 */
public class AssignStatement implements Statement {
    private final String variableName;
    private final Expression expression;

    /**
     * Construtor do nó AssignStatement.
     * 
     * @param variableName O nome (identificador) da variável que receberá o valor.
     * @param expression   A expressão cujo resultado será atribuído à variável.
     */
    public AssignStatement(String variableName, Expression expression) {
        this.variableName = variableName;
        this.expression = expression;
    }

    /**
     * Obtém o nome da variável de destino.
     * Usado pelo InterpreterVisitor para identificar a variável na Tabela de
     * Símbolos.
     * 
     * @return O nome da variável.
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * Obtém a expressão que deve ser avaliada para obter o valor a ser atribuído.
     * 
     * @return O nó Expression do lado direito da atribuição.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Implementação do método accept do Padrão Visitor.
     * Permite que o Visitor (Intérprete) execute a lógica de atribuição,
     * incluindo a checagem de tipo na Tabela de Símbolos.
     * 
     * @param visitor O objeto Visitor que irá processar este nó.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}