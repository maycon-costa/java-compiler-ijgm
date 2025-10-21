package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Interface base para todos os comandos (statements) da AST.
 * (Refatorada com Genéricos).
 */
public interface Statement {
    /**
     * O método accept faz parte do Padrão Visitor.
     * (Refatorado para aceitar um Visitor<R> e retornar R).
     * * @param visitor O objeto Visitor que irá processar este nó.
     * @return O resultado da visita (tipo R).
     */
    <R> R accept(Visitor<R> visitor);
}