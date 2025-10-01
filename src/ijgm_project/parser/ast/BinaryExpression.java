package ijgm_project.parser.ast;

import ijgm_project.lexer.TokenType;
import ijgm_project.visitor.Visitor;

/**
 * Representa uma expressão binária na Árvore de Sintaxe Abstrata (AST)
 * (ex: a + b, x > 5, b == c).
 * É um nó composto que contém dois nós filhos (esquerda e direita) e um
 * operador.
 * Este é o coração da hierarquia de expressões e implementa o Padrão Composite.
 */
public class BinaryExpression implements Expression {
    private final Expression left;
    private final TokenType operator;
    private final Expression right;

    /**
     * Construtor do nó BinaryExpression.
     * 
     * @param left     O nó Expression do operando à esquerda.
     * @param operator O TokenType que representa o operador (ex: PLUS,
     *                 EQUAL_EQUAL).
     * @param right    O nó Expression do operando à direita.
     */
    public BinaryExpression(Expression left, TokenType operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * Obtém o nó da subexpressão à esquerda.
     * Usado para a travessia recursiva do Visitor.
     * 
     * @return O nó Expression esquerdo.
     */
    public Expression getLeft() {
        return left;
    }

    /**
     * Obtém o operador desta expressão.
     * Usado pelo InterpreterVisitor para decidir qual operação executar.
     * 
     * @return O TokenType do operador.
     */
    public TokenType getOperator() {
        return operator;
    }

    /**
     * Obtém o nó da subexpressão à direita.
     * Usado para a travessia recursiva do Visitor.
     * 
     * @return O nó Expression direito.
     */
    public Expression getRight() {
        return right;
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