package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;

/**
 * Interface base para todos os comandos (statements) da AST.
 * É a raiz da hierarquia do Padrão Composite para comandos executáveis.
 */

public interface Statement {
    /**
     * O método accept faz parte do Padrão Visitor.
     * Ele permite que o Visitor (Intérprete, Printer, etc.) visite este nó.
     * 
     * @param visitor O objeto Visitor que irá processar este nó.
     */
    void accept(Visitor visitor);
}