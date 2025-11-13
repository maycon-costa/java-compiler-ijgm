package ijgm_project.vm;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um "pedaço" de bytecode compilado.
 * Esta classe é um container de dados (um "cartucho").
 * Ela armazena as instruções (Opcodes) e os dados (constantes)
 * que a VM irá executar.
 */
public class BytecodeChunk {

    // 1. A lista de instruções (Opcodes)
    // O CompilerVisitor escreve aqui, a VM lê daqui.
    private final List<Byte> code;

    // 2. A "Pool de Constantes"
    // Armazena valores literais (números, strings, etc.)
    // que as instruções usam.
    private final List<Object> constants;

    /**
     * Construtor: Inicializa as listas vazias.
     */
    public BytecodeChunk() {
        this.code = new ArrayList<>();
        this.constants = new ArrayList<>();
    }

    // --- MÉTODOS USADOS PELO COMPILADOR (CompilerVisitor) ---

    /**
     * Escreve um único byte (uma instrução ou um operando) no chunk.
     * @param b O byte a ser escrito.
     */
    public void writeByte(byte b) {
        this.code.add(b);
    }

    /**
     * Adiciona um valor (ex: 10, "ola") à pool de constantes.
     * @param value O objeto a ser adicionado.
     * @return O índice (posição) onde o valor foi armazenado.
     *
     * Este índice é crucial. O CompilerVisitor usará este índice
     * como o operando para a instrução OP_PUSH_CONST.
     */
    public int addConstant(Object value) {
        if(this.constants.contains(value)) {
            return this.constants.indexOf(value);
        }
        this.constants.add(value);
        // Retorna o índice do item que acabamos de adicionar
        return this.constants.size() - 1;
    }

    // --- MÉTODOS USADOS PELA MÁQUINA VIRTUAL (VM) ---

    /**
     * Retorna a lista completa de instruções (bytecode).
     * @return A lista de bytes.
     */
    public List<Byte> getCode() {
        return this.code;
    }

    /**
     * Retorna a lista completa de constantes.
     * @return A lista de objetos.
     */
    public List<Object> getConstants() {
        return this.constants;
    }

    /**
     * Helper para a VM buscar um valor da pool de constantes
     * pelo seu índice.
     * @param index O índice do valor.
     * @return O Objeto constante.
     */
    public Object getConstant(int index) {
        return this.constants.get(index);
    }

    @Override
    public String toString() {
        return "BytecodeChunk [code=" + code + ", constants=" + constants + "]";
    }
}