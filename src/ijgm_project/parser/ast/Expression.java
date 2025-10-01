package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Interface base para todas as expressões na Árvore de Sintaxe Abstrata (AST).
 * É a raiz da hierarquia do Padrão Composite para todos os nós que retornam um
 * valor
 * (ex: literais, variáveis, resultados de operações aritméticas ou lógicas).
 */
public interface Expression {

    /**
     * O método accept faz parte do Padrão Visitor.
     * É o ponto de entrada que permite que um objeto Visitor visite e avalie este
     * nó da AST.
     * * @param visitor O objeto Visitor que irá processar este nó (ex:
     * InterpreterVisitor).
     */
    void accept(Visitor visitor);
}