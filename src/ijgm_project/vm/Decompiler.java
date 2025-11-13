package ijgm_project.vm;

import java.util.List;

/**
 * Classe utilitária para "desmontar" (disassemble) um BytecodeChunk
 * e imprimi-lo no console de forma legível.
 *
 * Isso é extremamente útil para depurar o compilador e a VM.
 */
public class Decompiler {

    /**
     * Ponto de entrada principal. Desmonta e imprime o chunk inteiro.
     * @param chunk O "cartucho" de bytecode a ser lido.
     */
    public void disassemble(BytecodeChunk chunk) {
        if (chunk == null) {
            System.out.println("Chunk nulo.");
            return;
        }

        System.out.println("--- Descompilador ---");

        // 1. Imprimir a Pool de Constantes
        printConstants(chunk);

        // 2. Imprimir o Bytecode
        System.out.println("\n--- Bytecode ---");
        int ip = 0; // "Instruction Pointer" (Ponteiro de Instrução)
        while (ip < chunk.getCode().size()) {
            // O loop avança o ip com base no tamanho da instrução
            ip = disassembleInstruction(chunk, ip);
        }

        System.out.println("--- Fim ---");
    }

    /**
     * Imprime a seção de constantes do chunk.
     */
    private void printConstants(BytecodeChunk chunk) {
        System.out.println("--- Constantes ---");
        List<Object> constants = chunk.getConstants();
        if (constants.isEmpty()) {
            System.out.println("(Vazio)");
            return;
        }

        for (int i = 0; i < constants.size(); i++) {
            Object value = constants.get(i);
            // Formata a saída: 0000: 'Valor' (Tipo)
            System.out.printf("%04d: '%s' (%s)\n",
                    i,
                    value.toString(),
                    value.getClass().getSimpleName()
            );
        }
    }

    /**
     * Desmonta e imprime uma única instrução na posição 'ip'.
     * @param chunk O chunk que está sendo lido.
     * @param ip A posição (índice) da instrução a ser lida.
     * @return O índice da *próxima* instrução.
     */
    private int disassembleInstruction(BytecodeChunk chunk, int ip) {
        byte instructionByte = chunk.getCode().get(ip);
        OpCode op;

        try {
            op = OpCode.fromByte(instructionByte);
        } catch (ArrayIndexOutOfBoundsException e) {
            // Se o byte não for um OpCode válido
            System.out.printf("%04d Erro: OpCode desconhecido (byte: %d)\n", ip, instructionByte);
            return ip + 1;
        }

        // Usa um switch para lidar com instruções de diferentes tamanhos
        switch (op) {
            // --- 1. Instruções Simples (1 byte) ---
            case OP_PUSH_TRUE:
            case OP_PUSH_FALSE:
            case OP_ADD:
            case OP_SUBTRACT:
            case OP_MULTIPLY:
            case OP_DIVIDE:
            case OP_NEGATE:
            case OP_NOT:
            case OP_EQUAL:
            case OP_NOT_EQUAL:
            case OP_GREATER:
            case OP_GREATER_EQUAL:
            case OP_LESS:
            case OP_LESS_EQUAL:
            case OP_AND:
            case OP_OR:
            case OP_POP:
            case OP_PRINT:
            case OP_RETURN:
                return simpleInstruction(op.name(), ip);

            // --- 2. Instruções de Constante (1 byte + 1 operando) ---
            // Usam um índice para a pool de constantes
            case OP_PUSH_CONST:
                return constantInstruction("OP_PUSH_CONST", chunk, ip);

            // --- 3. Instruções de Slot (1 byte + 1 operando) ---
            // Usam um índice para slots de variáveis (locais ou globais)
            case OP_DEFINE_GLOBAL:
            case OP_LOAD_GLOBAL:
            case OP_STORE_GLOBAL:
            case OP_LOAD_LOCAL:
            case OP_STORE_LOCAL:
            case OP_INCREMENT_LOCAL:
            case OP_INCREMENT_GLOBAL:
                return slotInstruction(op.name(), chunk, ip);

            // --- 4. Instruções de Salto (1 byte + 1 operando) ---
            // Usam um operando de "offset" para pular
            case OP_JUMP_IF_FALSE:
            case OP_JUMP:
                return jumpInstruction(op.name(), chunk, ip);

            default:
                // Caso algum OpCode tenha sido esquecido no switch
                System.out.printf("%04d OpCode não tratado: %s\n", ip, op.name());
                return ip + 1;
        }
    }

    // --- Métodos Auxiliares de Formatação ---

    /**
     * Formata uma instrução simples (sem operandos).
     * @param name O nome do OpCode.
     * @param ip O índice da instrução.
     * @return O próximo índice (ip + 1).
     */
    private int simpleInstruction(String name, int ip) {
        System.out.printf("%04d %s\n", ip, name);
        return ip + 1;
    }

    /**
     * Formata uma instrução que usa um índice da pool de constantes.
     * @param name O nome do OpCode.
     * @param chunk O chunk, para acessar as constantes.
     * @param ip O índice da instrução.
     * @return O próximo índice (ip + 2).
     */
    private int constantInstruction(String name, BytecodeChunk chunk, int ip) {
        // O operando é o byte logo após a instrução
        byte constantIndex = chunk.getCode().get(ip + 1);
        Object value = chunk.getConstant(constantIndex);

        // %-18s -> Alinha o nome à esquerda com 18 caracteres
        // %4d   -> Alinha o índice à direita com 4 caracteres
        System.out.printf("%04d %-18s %4d ('%s')\n",
                ip,
                name,
                constantIndex,
                value
        );
        return ip + 2; // Avança 2 bytes (OpCode + Operando)
    }

    /**
     * Formata uma instrução que usa um índice de "slot" (variável).
     * @param name O nome do OpCode.
     * @param chunk O chunk.
     * @param ip O índice da instrução.
     * @return O próximo índice (ip + 2).
     */
    private int slotInstruction(String name, BytecodeChunk chunk, int ip) {
        // O operando é o byte logo após a instrução
        byte slotIndex = chunk.getCode().get(ip + 1);

        System.out.printf("%04d %-18s %4d\n",
                ip,
                name,
                slotIndex
        );
        return ip + 2; // Avança 2 bytes (OpCode + Operando)
    }

    /**
     * Formata uma instrução de salto (JUMP).
     * @param name O nome do OpCode.
     * @param chunk O chunk.
     * @param ip O índice da instrução.
     * @return O próximo índice (ip + 2).
     */
    private int jumpInstruction(String name, BytecodeChunk chunk, int ip) {
        // O operando é o byte logo após a instrução
        // O offset é um byte *assinado* (signed byte)
        byte offset = chunk.getCode().get(ip + 1);
        
        // O salto é relativo ao *fim* da instrução atual (ip + 2)
        int targetAddress = (ip + 2) + offset;

        System.out.printf("%04d %-18s %4d (salta para %04d)\n",
                ip,
                name,
                offset,
                targetAddress
        );
        return ip + 2; // Avança 2 bytes (OpCode + Operando)
    }
}