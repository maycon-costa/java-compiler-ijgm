package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Representa um literal de ponto flutuante (ex: 10.5, 3.14) na Árvore de
 * Sintaxe Abstrata (AST).
 * Este é um nó folha que armazena o valor numérico decimal.
 * Implementa a interface Expression.
 */
public class FloatExpression implements Expression {
    private final Float value;

    /**
     * Construtor do nó FloatExpression.
     * Converte o lexema (valor em formato String) para um valor Float de Java.
     * 
     * @param value O lexema (string) do número de ponto flutuante fornecido pelo
     *              Lexer.
     */
    public FloatExpression(String value) {
        // Usa Float.parseFloat para realizar a conversão do literal de string.
        this.value = Float.parseFloat(value);
    }

    /**
     * Obtém o valor de ponto flutuante armazenado neste nó.
     * Este é o dado literal a ser usado em cálculos pelo InterpreterVisitor.
     * 
     * @return O valor Float.
     */
    public Float getValue() {
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