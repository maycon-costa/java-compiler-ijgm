package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Representa um literal numérico inteiro (ex: 10, 42) na Árvore de Sintaxe
 * Abstrata (AST).
 * Este é um nó folha que armazena o valor inteiro diretamente.
 * Implementa a interface Expression.
 */
public class NumberExpression implements Expression {
    private final int value;

    /**
     * Construtor do nó NumberExpression.
     * Converte o lexema (valor em formato String) para um valor inteiro (int).
     * 
     * @param value O lexema (string) do número inteiro fornecido pelo Lexer.
     */
    public NumberExpression(String value) {
        this.value = Integer.parseInt(value);
    }

    /**
     * Obtém o valor inteiro armazenado neste nó.
     * Este é o dado literal a ser usado em cálculos pelo InterpreterVisitor.
     * 
     * @return O valor inteiro.
     */
    public int getValue() {
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