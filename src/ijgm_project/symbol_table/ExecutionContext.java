package ijgm_project.symbol_table;

import ijgm_project.lexer.TokenType;
import java.util.HashMap;
import java.util.Map;

/**
 * Contexto de Execução (Anteriormente SymbolTable).
 * Gerencia o contexto do programa, armazenando o tipo (para checagem semântica)
 * e o valor de runtime de cada variável.
 */
// InterpreterVisitor, que é o responsável por ela.
public class ExecutionContext {
    private final Map<String, Object> variables = new HashMap<>();
    private final Map<String, TokenType> types = new HashMap<>();

    /**
     * Declara uma nova variável no escopo (checa re-declaração).
     * * @param name Nome da variável.
     * @param type Tipo de token (ex: INT, BOOL).
     */

    public void declare(String name, TokenType type) {
        if (types.containsKey(name)) {
            throw new RuntimeException("Erro Semântico: Variável '" + name + "' já declarada.");
        }
        types.put(name, type);
        if (null != type)
            // Inicialização de valores padrão
        switch (type) {
            case INT -> variables.put(name, 0);
            case FLOAT -> variables.put(name, 0.0f);
            case BOOL -> variables.put(name, false);
            case STRING_TYPE -> variables.put(name, "");
            default -> {
            }
        }
    }

    /**
     * Obtém o tipo estático (declarado) de uma variável.
     * Usado pelo Interpreter para checagem semântica.
     * * @param name Nome da variável.
     * @return O TokenType da variável.
     */
    public TokenType getDeclaredType(String name) {
        if (!types.containsKey(name)) {
            throw new RuntimeException("Erro Semântico: Variável '" + name + "' não declarada.");
        }
        return types.get(name);
    }

    /**
     * Atribui um valor de runtime a uma variável.
     * Este método agora é "burro" e apenas armazena o valor,
     * A checagem de tipo foi movida para o InterpreterVisitor.
     * * @param name  Nome da variável.
     * @param value O valor a ser atribuído.
     */
    public void put(String name, Object value) {
        // A checagem de "não declarada" é feita antes pelo 'getDeclaredType'.
        // Este método agora só armazena.
        variables.put(name, value);
    }

    /**
     * Obtém o valor de uma variável.
     * * @param name Nome da variável.
     * @return O valor armazenado.
     */
    public Object get(String name) {
        if (!variables.containsKey(name)) {
            // Esta checagem ainda é válida para o 'get' (ex: print a;)
            throw new RuntimeException("Erro Semântico: Variável '" + name + "' não encontrada.");
        }
        return variables.get(name);
    }
}