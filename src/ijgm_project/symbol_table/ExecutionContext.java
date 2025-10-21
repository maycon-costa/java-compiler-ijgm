package ijgm_project.symbol_table;

import ijgm_project.lexer.TokenType;
import java.util.HashMap;
import java.util.Map;

/**
 * Contexto de Execução (Anteriormente SymbolTable).
 * Gerencia o contexto do programa, armazenando o tipo (para checagem semântica)
 * e o valor de runtime de cada variável.
 */
// TODO: Trocar nome disso e funcionamento porque quebra o SOLID, isso não deve
// fazer checagens semanticas
// (NOTA: Mantendo o TODO original, mas renomeando a classe conforme a crítica)
// (NOTA: O contexto de execução permaneceu realizando checagens semânticas, ainda está quebrando o SOLID, o Interpreter que deve tratar isso)
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
        // Inicialização de valores padrão
        if (type == TokenType.INT)
            variables.put(name, 0);
        else if (type == TokenType.FLOAT)
            variables.put(name, 0.0f);
        else if (type == TokenType.BOOL)
            variables.put(name, false);
        else if (type == TokenType.STRING_TYPE)
            variables.put(name, "");
    }

    /**
     * Atribui um valor a uma variável existente (checa tipo e declaração).
     * * @param name  Nome da variável.
     * @param value O valor a ser atribuído.
     */

    public void put(String name, Object value) {
        if (!types.containsKey(name)) {
            throw new RuntimeException("Erro Semântico: Variável '" + name + "' não declarada.");
        }
        TokenType expectedType = types.get(name);

        // Permite atribuição de INT para FLOAT (coerção implícita)
        if (expectedType == TokenType.FLOAT && value instanceof Integer) {
            value = ((Integer) value).floatValue();
        }

        // Checagem de compatibilidade de tipos (Core da Análise Semântica)
        if (expectedType == TokenType.INT && !(value instanceof Integer)) {
            throw new RuntimeException(
                    "Erro Semântico: Esperado INT, mas recebido " + value.getClass().getSimpleName());
        }
        if (expectedType == TokenType.FLOAT && !(value instanceof Float)) {
            throw new RuntimeException(
                    "Erro Semântico: Esperado FLOAT, mas recebido " + value.getClass().getSimpleName());
        }
        if (expectedType == TokenType.BOOL && !(value instanceof Boolean)) {
            throw new RuntimeException(
                    "Erro Semântico: Esperado BOOL, mas recebido " + value.getClass().getSimpleName());
        }
        if (expectedType == TokenType.STRING_TYPE && !(value instanceof String)) {
            throw new RuntimeException(
                    "Erro Semântico: Esperado STRING, mas recebido " + value.getClass().getSimpleName());
        }

        variables.put(name, value);
    }

    /**
     * Obtém o valor de uma variável.
     * * @param name Nome da variável.
     * @return O valor armazenado.
     */

    public Object get(String name) {
        if (!variables.containsKey(name)) {
            throw new RuntimeException("Erro Semântico: Variável '" + name + "' não encontrada.");
        }
        return variables.get(name);
    }
}