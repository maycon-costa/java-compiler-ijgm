package ijgm_project.vm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Tabela de Símbolos do Compilador.
 *
 * Gerencia os nomes das variáveis, seus escopos e seus "endereços" (slots)
 * durante a fase de compilação.
 *
 * Esta classe substitui a necessidade do 'ExecutionContext'
 * em tempo de compilação. Ela rastreia onde uma variável *viverá*,
 * em vez de qual *valor* ela possui.
 */
public class CompilerSymbolTable {

    /**
     * Classe interna que armazena o "endereço" de uma variável resolvida.
     * O Compilador usará isso para saber qual OpCode emitir.
     */
    public static class Symbol {
        /** O índice (slot) da variável local ou o índice da constante para a global. */
        public final int index;
        
        /** Verdadeiro se for uma variável local (na pilha), falso se for global. */
        public final boolean isLocal;

        public Symbol(int index, boolean isLocal) {
            this.index = index;
            this.isLocal = isLocal;
        }
    }

    // 1. Tabela de Globais
    // Mapeia o nome da variável (String) ao seu índice na pool de constantes
    // (Integer).
    private final Map<String, Integer> globals;

    // 2. Pilha de Escopos Locais
    // Uma "pilha" (LinkedList) de Mapas. Cada Mapa representa um bloco de escopo
    // (ex: um 'if' ou 'while' ou o { } do 'parseScope').
    // Mapeia o nome da variável (String) ao seu "slot" na pilha da VM (Integer).
    private final LinkedList<Map<String, Integer>> localsStack;

    // 3. Contador de Slots
    // Conta quantos slots locais estão *atualmente em uso*.
    // Se 'a' é declarada, count=1 (slot 0). Se 'b' é declarada, count=2 (slot 1).
    private int localSlotCount;

    /**
     * Construtor da Tabela de Símbolos do Compilador.
     */
    public CompilerSymbolTable() {
        this.globals = new HashMap<>();
        this.localsStack = new LinkedList<>();
        this.localSlotCount = 0;
        // Não há escopo local no início, apenas global.
    }

    /**
     * Verifica se o compilador está atualmente no escopo global.
     * @return true se estiver no escopo global, false se estiver em um bloco local.
     */
    public boolean isGlobalScope() {
        return localsStack.isEmpty();
    }

    /**
     * Inicia um novo escopo léxico (ex: ao entrar em um bloco '{...}').
     * Adiciona um novo "nível" à pilha de escopos.
     */
    public void beginScope() {
        localsStack.addFirst(new HashMap<>());
    }

    /**
     * Termina o escopo léxico atual (ex: ao sair de um bloco '}').
     * Remove o "nível" mais interno da pilha de escopos.
     *
     * @return O número de variáveis que estavam *apenas* neste escopo.
     * O Compilador usará isso para emitir Opcodes 'OP_POP'
     * e limpar a pilha da VM.
     */
    public int endScope() {
        if (localsStack.isEmpty()) {
            throw new IllegalStateException("Não há escopo local para fechar.");
        }
        
        Map<String, Integer> endedScope = localsStack.removeFirst();
        int poppedVars = endedScope.size();
        
        // Libera os slots que essas variáveis estavam usando.
        this.localSlotCount -= poppedVars;
        
        return poppedVars;
    }

    /**
     * Declara uma nova variável no escopo ATUAL (seja global ou local).
     *
     * @param name O nome da variável (ex: "x").
     * @param chunk O "cartucho" de bytecode, necessário para adicionar o nome
     * da variável à pool de constantes (se for global).
     * @return O Símbolo (endereço) da variável recém-criada.
     */
    public Symbol declare(String name, BytecodeChunk chunk) {
        if (isGlobalScope()) {
            // --- Declaração GLOBAL ---
            if (globals.containsKey(name)) {
                throw new RuntimeException("Erro de Compilação: Variável global '" + name + "' já declarada.");
            }
            
            // Variáveis globais são "endereçadas" por um índice na pool de constantes
            // que armazena o NOME da variável.
            int constantIndex = chunk.addConstant(name);
            globals.put(name, constantIndex);
            return new Symbol(constantIndex, false); // isLocal = false
            
        } else {
            // --- Declaração LOCAL ---
            Map<String, Integer> currentScope = localsStack.peekFirst();
            
            // Checa por re-declaração *apenas no escopo atual*.
            if (currentScope.containsKey(name)) {
                throw new RuntimeException("Erro de Compilação: Variável '" + name + "' já declarada neste escopo.");
            }
            
            // O "endereço" (slot) desta variável é o próximo slot livre.
            int slot = this.localSlotCount++;
            currentScope.put(name, slot);
            return new Symbol(slot, true); // isLocal = true
        }
    }

    /**
     * Resolve (encontra) uma variável, procurando do escopo mais interno
     * para o mais externo (global).
     *
     * @param name O nome da variável a ser encontrada (ex: "x").
     * @return O Símbolo (endereço) da variável.
     * @throws RuntimeException se a variável não for encontrada.
     */
    public Symbol resolve(String name) {
        // 1. Tenta resolver como LOCAL
        // Itera a pilha de escopos de dentro para fora (do 'first' ao 'last')
        for (Map<String, Integer> scope : localsStack) {
            if (scope.containsKey(name)) {
                // Encontrou! Retorna o Símbolo com o slot local.
                return new Symbol(scope.get(name), true); // isLocal = true
            }
        }

        // 2. Se não for local, tenta resolver como GLOBAL
        if (globals.containsKey(name)) {
            // Encontrou! Retorna o Símbolo com o índice da constante global.
            return new Symbol(globals.get(name), false); // isLocal = false
        }
        
        // 3. Não encontrou em lugar nenhum.
        throw new RuntimeException("Erro de Compilação: Variável '" + name + "' não foi declarada.");
    }
}