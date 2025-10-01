package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Representa um literal de string (texto entre aspas) na Árvore de Sintaxe
 * Abstrata (AST).
 * Este é um nó folha que armazena o valor de texto lido diretamente do
 * código-fonte.
 * Implementa a interface Expression.
 */
public class StringExpression implements Expression {
    private final String value;

    /**
     * Construtor do nó StringExpression.
     * 
     * @param value O valor literal da string (o lexema) conforme fornecido pelo
     *              Lexer.
     */
    public StringExpression(String value) {
        this.value = value;
    }

    /**
     * Obtém o valor de texto armazenado neste nó.
     * Este valor é o próprio dado a ser usado na interpretação (ex: na função
     * 'print' ou concatenação).
     * 
     * @return O valor da string.
     */
    public String getValue() {
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