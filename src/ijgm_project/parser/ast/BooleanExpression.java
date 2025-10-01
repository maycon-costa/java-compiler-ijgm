package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Representa um literal booleano (true ou false) na Árvore de Sintaxe Abstrata
 * (AST).
 * Este é um nó folha que armazena um valor de verdade.
 * Implementa a interface Expression.
 */
public class BooleanExpression implements Expression {
    private final boolean value;

    /**
     * Construtor do nó BooleanExpression.
     * 
     * @param value O valor booleano (true ou false) a ser armazenado.
     */
    public BooleanExpression(boolean value) {
        this.value = value;
    }

    /**
     * Obtém o valor booleano armazenado neste nó.
     * Este valor é o dado literal a ser usado em condições (IF, WHILE) e operações
     * lógicas.
     * 
     * @return O valor booleano.
     */
    public boolean getValue() {
        return value;
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