package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;
import java.util.List;

/**
 * Representa o comando de repetição WHILE na Árvore de Sintaxe Abstrata (AST).
 * É um nó composto que contém uma expressão (condição) e uma lista de comandos
 * (corpo).
 * Implementa o Padrão Composite.
 */
public class WhileStatement implements Statement {
    private final Expression condition;
    private final List<Statement> body;

    /**
     * Construtor do nó WhileStatement.
     * 
     * @param condition A expressão de controle que será avaliada (deve ser
     *                  booleana).
     * @param body      A lista de comandos (Statement) a serem executados
     *                  repetidamente.
     */
    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    /**
     * Obtém a expressão que define a condição do loop.
     * 
     * @return O nó Expression da condição.
     */
    public Expression getCondition() {
        return condition;
    }

    /**
     * Obtém a lista de comandos que compõem o corpo do loop.
     * 
     * @return A lista de Statement's do corpo.
     */
    public List<Statement> getBody() {
        return body;
    }

    /**
     * Implementação do método accept do Padrão Visitor.
     * Permite que o Visitor (Intérprete ou Printer) processe este nó.
     * 
     * @param visitor O objeto Visitor que irá processar este nó.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}