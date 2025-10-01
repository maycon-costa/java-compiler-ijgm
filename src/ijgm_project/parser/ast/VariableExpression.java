package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Representa uma expressão que é simplesmente o nome de uma variável (um
 * identificador)
 * na Árvore de Sintaxe Abstrata (AST).
 * * Este nó é usado para obter o valor atual da variável na Tabela de Símbolos.
 */
public class VariableExpression implements Expression {
    private final String name;

    /**
     * Construtor do nó VariableExpression.
     * 
     * @param name O nome (lexema) da variável conforme lido pelo Lexer.
     */
    public VariableExpression(String name) {
        this.name = name;
    }

    /**
     * Obtém o nome da variável.
     * Este nome será usado pelo InterpreterVisitor para buscar o valor
     * correspondente na Tabela de Símbolos.
     * 
     * @return O nome da variável.
     */
    public String getName() {
        return name;
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