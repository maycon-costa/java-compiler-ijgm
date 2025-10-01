package ijgm_project.parser.ast;

import ijgm_project.visitor.Visitor;
import ijgm_project.lexer.TokenType;

/**
 * Representa a declaração de uma variável (ex: int x;).
 * Contém o tipo de dado estático e o nome da variável.
 * Este nó é crucial para a fase de Análise Semântica, onde a Tabela de Símbolos
 * é populada.
 * Implementa a interface Statement.
 */
public class DeclarationStatement implements Statement {
    private final TokenType type;
    private final String variableName;

    /**
     * Construtor de Declaração.
     * * @param type O tipo de dado da variável (ex: INT, BOOL, conforme definido no
     * TokenType).
     * 
     * @param variableName O nome da variável (o identificador).
     */
    public DeclarationStatement(TokenType type, String variableName) {
        this.type = type;
        this.variableName = variableName;
    }

    /**
     * Obtém o tipo de dado estático da variável.
     * Usado pela Tabela de Símbolos para registrar o tipo.
     * 
     * @return O TokenType que representa o tipo de dado.
     */
    public TokenType getType() {
        return type;
    }

    /**
     * Obtém o nome (identificador) da variável.
     * 
     * @return O nome da variável.
     */
    public String getVariableName() {
        return variableName;
    }

    /**
     * Implementação do método accept do Padrão Visitor.
     * Permite que o Visitor registre a declaração na Tabela de Símbolos.
     * 
     * @param visitor O objeto Visitor que irá processar este nó.
     */
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}