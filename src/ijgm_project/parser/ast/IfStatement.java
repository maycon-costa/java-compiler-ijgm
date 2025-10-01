package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;
import java.util.List;

/**
 * Representa a estrutura de comando condicional IF-ELSE na Árvore de Sintaxe
 * Abstrata (AST).
 * É um nó composto de controle que decide qual bloco de código deve ser
 * executado.
 * Implementa o Padrão Composite.
 */
public class IfStatement implements Statement {
    private final Expression condition;
    private final List<Statement> thenBody;
    private final List<Statement> elseBody;

    /**
     * Construtor do IfStatement.
     * * @param condition A expressão que será avaliada (deve ser booleana).
     * 
     * @param thenBody A lista de comandos a serem executados se a condição for
     *                 verdadeira (o bloco 'then').
     * @param elseBody A lista de comandos a serem executados se a condição for
     *                 falsa (o bloco 'else'). Pode ser null se não houver 'else'.
     */
    public IfStatement(Expression condition, List<Statement> thenBody, List<Statement> elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    /**
     * Obtém a expressão que define a condição do comando IF.
     * 
     * @return O nó Expression da condição.
     */
    public Expression getCondition() {
        return condition;
    }

    /**
     * Obtém a lista de comandos do bloco THEN (executado se a condição for
     * verdadeira).
     * 
     * @return A lista de Statement's do bloco THEN.
     */
    public List<Statement> getThenBody() {
        return thenBody;
    }

    /**
     * Obtém a lista de comandos do bloco ELSE (executado se a condição for falsa).
     * 
     * @return A lista de Statement's do bloco ELSE, ou null.
     */
    public List<Statement> getElseBody() {
        return elseBody;
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