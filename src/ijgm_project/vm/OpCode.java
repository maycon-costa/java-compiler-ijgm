package ijgm_project.vm;

/**
 * Define o conjunto de instruções (Bytecode) para a VM.
 *
 * (Refatorado para "boa prática": O enum agora armazena seu próprio
 * valor de byte, em vez de depender de .ordinal() e casts externos).
 */
public enum OpCode {

    // --- 1. Constantes e Literais ---
    OP_PUSH_CONST(0),
    OP_PUSH_TRUE(1),
    OP_PUSH_FALSE(2),

    // --- 2. Aritmética ---
    OP_ADD(3),
    OP_SUBTRACT(4),
    OP_MULTIPLY(5),
    OP_DIVIDE(6),
    OP_NEGATE(7),

    // --- 3. Lógica e Comparação ---
    OP_NOT(8),
    OP_EQUAL(9),
    OP_NOT_EQUAL(10),
    OP_GREATER(11),
    OP_GREATER_EQUAL(12),
    OP_LESS(13),
    OP_LESS_EQUAL(14),
    OP_AND(15),
    OP_OR(16),

    // --- 4. Variáveis e Escopo ---
    OP_DEFINE_GLOBAL(17),
    OP_LOAD_GLOBAL(18),
    OP_STORE_GLOBAL(19),
    OP_LOAD_LOCAL(20),
    OP_STORE_LOCAL(21),

    // --- 5. Controle de Fluxo ---
    OP_JUMP_IF_FALSE(22),
    OP_JUMP(23),

    // --- 6. Auxiliares e Extras ---
    OP_POP(24),
    OP_PRINT(25),
    OP_INCREMENT_LOCAL(26),
    OP_INCREMENT_GLOBAL(27),
    OP_RETURN(28),

    // --- NOVOS OPCODES ---
    OP_DECREMENT_LOCAL(29),
    OP_DECREMENT_GLOBAL(30);

    // --- A Lógica da Boa Prática ---

    private final byte value; // Armazena o valor como byte

    /**
     * O construtor é chamado para cada item acima.
     * Ele lida com a conversão int -> byte UMA VEZ, aqui dentro.
     */
    OpCode(int value) {
        if (value > 255 || value < 0) {
            // Segurança: garante que o valor se encaixa em um byte (0-255)
            throw new IllegalArgumentException("Valor do OpCode fora do intervalo de byte: " + value);
        }
        this.value = (byte) value;
    }

    /**
     * O novo método que o CompilerVisitor usará.
     * 
     * @return O valor de byte exato desta instrução.
     */
    public byte getValue() {
        return this.value;
    }

    /**
     * Permite que a VM converta um byte de volta para um OpCode.
     * (A VM precisará disso).
     */
    public static OpCode fromByte(byte b) {
        // Esta é uma forma rápida de buscar o OpCode pelo seu valor de byte.
        // Assume que os valores são contínuos (0, 1, 2, 3...)
        return OpCode.values()[b];
    }
}