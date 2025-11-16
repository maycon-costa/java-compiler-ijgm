package ijgm_project.vm;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * A Máquina Virtual (VM) da linguagem IJGM.
 *
 * (Atualizada para corrigir o Furo #2 - Inconsistência de ++/-- com Floats)
 * (Atualizada para corrigir o Furo #3 - Verificação de "Truthiness" em
 * Jumps)
 */
public class VM {

    // O "cartucho" de programa contendo Opcodes e constantes
    private final BytecodeChunk chunk;

    // A "mesa de trabalho" da VM. Usada para todos os cálculos.
    private final Stack<Object> stack;

    // Armazenamento para variáveis globais.
    private final Map<String, Object> globals;

    // "Instruction Pointer": Aponta para o byte que está prestes a ser lido.
    private int ip;

    /**
     * Enum para sinalizar o resultado da execução.
     */
    public enum InterpretResult {
        OK,
        RUNTIME_ERROR
    }

    /**
     * Construtor da VM.
     * @param chunk O "cartucho" de bytecode compilado pelo CompilerVisitor.
     */
    public VM(BytecodeChunk chunk) {
        this.chunk = chunk;
        this.stack = new Stack<>();
        this.globals = new HashMap<>();
        this.ip = 0; // Começa na primeira instrução
    }

    /**
     * O "coração" da VM. Executa o bytecode em um loop.
     * @return O resultado da interpretação (OK ou RUNTIME_ERROR).
     */
    public InterpretResult run() {
        try {
            while (ip < chunk.getCode().size()) {
                // 1. Fetch & Decode (Buscar e Decodificar)
                byte rawInstruction = readByte();

                OpCode instruction = OpCode.fromByte(rawInstruction);

                // 2. Execute (Executar)
                switch (instruction) {

                    // --- Opcodes de Pilha e Literais ---
                    case OP_PUSH_CONST -> {
                        Object constant = readConstant();
                        push(constant);
                    }
                    case OP_PUSH_TRUE -> push(true);
                    case OP_PUSH_FALSE -> push(false);
                    case OP_POP -> pop();

                    // --- Opcodes de Operações ---
                    case OP_ADD -> binaryAdd();
                    case OP_SUBTRACT -> binaryNumericOp(OpCode.OP_SUBTRACT);
                    case OP_MULTIPLY -> binaryNumericOp(OpCode.OP_MULTIPLY);
                    case OP_DIVIDE -> binaryNumericOp(OpCode.OP_DIVIDE);

                    // --- Opcodes de Comparação e Lógica ---
                    case OP_EQUAL -> {
                        Object b = pop();
                        Object a = pop();
                        if (a == null)
                            push(b == null);
                        else
                            push(a.equals(b));
                    }
                    case OP_NOT_EQUAL -> {
                        Object b = pop();
                        Object a = pop();
                        if (a == null)
                            push(b != null);
                        else
                            push(!a.equals(b));
                    }
                    case OP_GREATER -> binaryNumericOp(OpCode.OP_GREATER);
                    case OP_GREATER_EQUAL -> binaryNumericOp(OpCode.OP_GREATER_EQUAL);
                    case OP_LESS -> binaryNumericOp(OpCode.OP_LESS);
                    case OP_LESS_EQUAL -> binaryNumericOp(OpCode.OP_LESS_EQUAL);

                    case OP_AND -> {
                        Object b = pop();
                        Object a = pop();
                        if (!(a instanceof Boolean) || !(b instanceof Boolean)) {
                            return runtimeError("Operandos para '&&' devem ser booleanos.");
                        }
                        push((Boolean) a && (Boolean) b);
                    }
                    case OP_OR -> {
                        Object b = pop();
                        Object a = pop();
                        if (!(a instanceof Boolean) || !(b instanceof Boolean)) {
                            return runtimeError("Operandos para '||' devem ser booleanos.");
                        }
                        push((Boolean) a || (Boolean) b);
                    }

                    // --- Opcodes de Variáveis Globais ---
                    case OP_DEFINE_GLOBAL -> {
                        String name = readConstantName();
                        globals.put(name, pop());
                    }
                    case OP_LOAD_GLOBAL -> {
                        String name = readConstantName();
                        if (!globals.containsKey(name)) {
                            return runtimeError("Variável global '" + name + "' não definida.");
                        }
                        push(globals.get(name));
                    }
                    case OP_STORE_GLOBAL -> {
                        String name = readConstantName();
                        if (!globals.containsKey(name)) {
                            return runtimeError("Variável global '" + name + "' não definida.");
                        }
                        // Atribuição não "puxa" (pop) o valor, apenas o armazena
                        globals.put(name, peek(0));
                    }

                    // --- Opcodes de Variáveis Locais (na Pilha) ---
                    case OP_LOAD_LOCAL -> {
                        int slot = readByte() & 0xFF; // Converte byte para índice (0-255)
                        push(stack.get(slot));
                    }
                    case OP_STORE_LOCAL -> {
                        int slot = readByte() & 0xFF;
                        // Atribuição não "puxa" (pop) o valor, apenas o armazena
                        stack.set(slot, peek(0));
                    }

                    // --- Opcodes de Controle de Fluxo (Jumps) ---
                    case OP_JUMP -> {
                        short offset = readByte();
                        ip += offset;
                    }

                    // --- CORREÇÃO (FURO #3) ---
                    // Adicionada checagem de tipo antes de chamar isFalsey
                    case OP_JUMP_IF_FALSE -> {
                        short offset = readByte();
                        Object condition = pop(); // Pula e SEMPRE consome a condição

                        // Checagem de tipo estrito
                        if (!(condition instanceof Boolean)) {
                            return runtimeError("Condição do 'if' ou 'while' deve ser um booleano.");
                        }

                        if (isFalsey(condition)) { // isFalsey agora só recebe booleanos
                            ip += offset;
                        }
                    }
                    // --- FIM DA CORREÇÃO #3 ---

                    // --- Opcodes de Comandos ---
                    case OP_PRINT -> {
                        System.out.println("Output: " + pop());
                    }
                    case OP_RETURN -> {
                        return InterpretResult.OK; // Fim da execução
                    }

                    // (Bônus: Opcodes do OpCode.java [from context])
                    case OP_NEGATE -> {
                        Object val = pop();
                        if (val instanceof Integer)
                            push(-(Integer) val);
                        else if (val instanceof Float)
                            push(-(Float) val);
                        else
                            return runtimeError("Operando para '-' deve ser um número.");
                    }
                    case OP_NOT -> {
                        push(isFalsey(pop()));
                    }

                    // --- CORREÇÃO (FURO #2) ---
                    // Lógica de incremento/decremento atualizada para aceitar Float
                    case OP_INCREMENT_LOCAL -> {
                        int slot = readByte() & 0xFF;
                        Object val = stack.get(slot);
                        if (val instanceof Integer) {
                            stack.set(slot, (Integer) val + 1);
                        } else if (val instanceof Float) {
                            stack.set(slot, (Float) val + 1.0f);
                        } else {
                            return runtimeError("Operando '++' deve ser um número (Integer ou Float).");
                        }
                    }

                    case OP_INCREMENT_GLOBAL -> {
                        String name = readConstantName();
                        if (!globals.containsKey(name)) {
                            return runtimeError("Variável global '" + name + "' não definida para '++'.");
                        }
                        Object val = globals.get(name);
                        if (val instanceof Integer) {
                            globals.put(name, (Integer) val + 1);
                        } else if (val instanceof Float) {
                            globals.put(name, (Float) val + 1.0f);
                        } else {
                            return runtimeError("Operando '++' deve ser um número (Integer ou Float).");
                        }
                    }

                    case OP_DECREMENT_LOCAL -> {
                        int slot = readByte() & 0xFF;
                        Object val = stack.get(slot);
                        if (val instanceof Integer) {
                            stack.set(slot, (Integer) val - 1);
                        } else if (val instanceof Float) {
                            stack.set(slot, (Float) val - 1.0f);
                        } else {
                            return runtimeError("Operando '--' deve ser um número (Integer ou Float).");
                        }
                    }

                    case OP_DECREMENT_GLOBAL -> {
                        String name = readConstantName();
                        if (!globals.containsKey(name)) {
                            return runtimeError("Variável global '" + name + "' não definida para '--'.");
                        }
                        Object val = globals.get(name);
                        if (val instanceof Integer) {
                            globals.put(name, (Integer) val - 1);
                        } else if (val instanceof Float) {
                            globals.put(name, (Float) val - 1.0f);
                        } else {
                            return runtimeError("Operando '--' deve ser um número (Integer ou Float).");
                        }
                    }
                    // --- FIM DA CORREÇÃO #2 ---

                    default -> {
                        return runtimeError("Opcode desconhecido: " + instruction);
                    }
                }
            }
        } catch (Exception e) {
            // Pega erros de runtime (ex: ClassCastException, StackOverflow)
            return runtimeError("Erro de VM: " + e.getMessage());
        }
        return InterpretResult.OK; // (Se o loop terminar sem OP_RETURN)
    }

    // --- HELPER METHODS ---

    /** Lê o próximo byte da instrução e avança o ponteiro. */
    private byte readByte() {
        return chunk.getCode().get(ip++);
    }

    /** Lê os próximos dois bytes como um 'short' (para offsets de pulo). */
    private short readShort() {
        ip += 2;
        byte high = chunk.getCode().get(ip - 2);
        byte low = chunk.getCode().get(ip - 1);
        return (short) ((high << 8) | (low & 0xFF));
    }

    /** Lê um operando de constante (o índice) e busca o valor na pool. */
    private Object readConstant() {
        int constIndex = readByte() & 0xFF; // Lê o índice (0-255)
        return chunk.getConstant(constIndex);
    }

    /** Lê um operando de constante e o converte para String (Nome da Variável). */
    private String readConstantName() {
        return (String) readConstant();
    }

    /** Empurra um valor para a pilha. */
    private void push(Object value) {
        stack.push(value);
    }

    /** Puxa (remove) um valor da pilha. */
    private Object pop() {
        if (stack.isEmpty()) {
            throw new RuntimeException("Stack underflow.");
        }
        return stack.pop();
    }

    /**
     * "Espia" um valor na pilha sem removê-lo.
     * peek(0) = topo, peek(1) = segundo item.
     */
    private Object peek(int distance) {
        return stack.get(stack.size() - 1 - distance);
    }

    /** Define quais valores são "falsos" na linguagem (apenas 'false'). */
    private boolean isFalsey(Object condition) {
        // Esta checagem agora é segura, pois 'condition' sempre será um Boolean
        // graças à correção no OP_JUMP_IF_FALSE.
        return (condition instanceof Boolean) && !(Boolean) condition;
    }

    /** Lógica de operação para '+' (que suporta concatenação de string). */
    private void binaryAdd() {
        Object b = pop();
        Object a = pop();

        // Lógica portada do InterpreterVisitor
        if (a instanceof String || b instanceof String) {
            push(String.valueOf(a) + String.valueOf(b));
        } else if (a instanceof Float && b instanceof Float) {
            push((Float) a + (Float) b);
        } else if (a instanceof Integer && b instanceof Integer) {
            push((Integer) a + (Integer) b);
        } else if (a instanceof Float && b instanceof Integer) {
            push((Float) a + ((Integer) b).floatValue());
        } else if (a instanceof Integer && b instanceof Float) {
            push(((Integer) a).floatValue() + (Float) b);
        } else {
            throw new RuntimeException("Operands must be two numbers or two strings for '+'.");
        }
    }

    /** Lógica de operação para todos os outros operadores numéricos. */
    private void binaryNumericOp(OpCode op) {
        Object b = pop();
        Object a = pop();

        // Coerção: int -> float (como no InterpreterVisitor)
        if (a instanceof Integer && b instanceof Float) {
            a = ((Integer) a).floatValue();
        } else if (a instanceof Float && b instanceof Integer) {
            b = ((Integer) b).floatValue();
        }

        if (a instanceof Float && b instanceof Float) {
            float valA = (Float) a;
            float valB = (Float) b;
            switch (op) {
                case OP_SUBTRACT:
                    push(valA - valB);
                    break;
                case OP_MULTIPLY:
                    push(valA * valB);
                    break;
                case OP_DIVIDE:
                    if (valB == 0.0f)
                        throw new RuntimeException("Divisão por zero.");
                    push(valA / valB);
                    break;
                case OP_GREATER:
                    push(valA > valB);
                    break;
                case OP_GREATER_EQUAL:
                    push(valA >= valB);
                    break;
                case OP_LESS:
                    push(valA < valB);
                    break;
                case OP_LESS_EQUAL:
                    push(valA <= valB);
                    break;
                default: // Unreachable
            }
        } else if (a instanceof Integer && b instanceof Integer) {
            int valA = (Integer) a;
            int valB = (Integer) b;
            switch (op) {
                case OP_SUBTRACT:
                    push(valA - valB);
                    break;
                case OP_MULTIPLY:
                    push(valA * valB);
                    break;
                case OP_DIVIDE:
                    if (valB == 0)
                        throw new RuntimeException("Divisão por zero.");
                    push(valA / valB);
                    break;
                case OP_GREATER:
                    push(valA > valB);
                    break;
                case OP_GREATER_EQUAL:
                    push(valA >= valB);
                    break;
                case OP_LESS:
                    push(valA < valB);
                    break;
                case OP_LESS_EQUAL:
                    push(valA <= valB);
                    break;
                default: // Unreachable
            }
        } else {
            throw new RuntimeException("Operands must be two numbers for this operation.");
        }
    }

    /** Helper para reportar erros de runtime. */
    private InterpretResult runtimeError(String message) {
        System.err.println(message);
        // (Opcional: imprimir a linha do erro, se o chunk a armazenar)
        return InterpretResult.RUNTIME_ERROR;
    }
}