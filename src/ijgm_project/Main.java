package ijgm_project;

import ijgm_project.lexer.Lexer;
import ijgm_project.lexer.Token;
import ijgm_project.parser.Parser;
import ijgm_project.parser.ast.Statement;
import ijgm_project.visitor.PrintVisitor;
// import ijgm_project.visitor.InterpreterVisitor; // <-- Motor antigo (não mais usado)

// --- NOVAS IMPORTAÇÕES ---
import ijgm_project.vm.CompilerVisitor;
import ijgm_project.vm.BytecodeChunk;
import ijgm_project.vm.Decompiler;
import ijgm_project.vm.VM;
// --- FIM DAS NOVAS IMPORTAÇÕES ---

import java.io.IOException;
import java.util.List;

/**
 * Classe principal que orquestra as fases do compilador:
 * 1. Análise Léxica (Tokenização)
 * 2. Análise Sintática (Construção da AST)
 * 3. Compilação (AST -> Bytecode)
 * 4. Execução (VM)
 * É o ponto de entrada do projeto.
 */
public class Main {
    /**
     * Método principal que inicia o processo de compilação/interpretação.
     * * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Define o caminho para o arquivo de código-fonte a ser lido.
        String filePath = "input/teste.txt";

        try {
            // --- FASE 1: ANÁLISE LÉXICA (Sem mudança) ---
            System.out.println("--- FASE 1: ANÁLISE LÉXICA ---");
            Lexer lexer = new Lexer(filePath);
            List<Token> tokens = lexer.tokenize();
            System.out.println("Tokens gerados: " + tokens);

            // --- FASE 2: ANÁLISE SINTÁTICA (Sem mudança) ---
            System.out.println("\n--- FASE 2: ANÁLISE SINTÁTICA (CONSTRUÇÃO DA AST) ---");
            Parser parser = new Parser(tokens);
            List<Statement> ast = parser.parse();

            // Exibição da Árvore de Sintaxe Abstrata (AINDA ÚTIL PARA DEBUG)
            System.out.println("Representação da Árvore de Sintaxe Abstrata:");
            PrintVisitor printVisitor = new PrintVisitor();
            for (Statement statement : ast) {
                statement.accept(printVisitor);
            }

            // --- FASE 3: COMPILAÇÃO (Novo) ---
            System.out.println("\n--- FASE 3: COMPILAÇÃO (AST -> Bytecode) ---");
            
            // 1. Instancia o novo CompilerVisitor
            CompilerVisitor compiler = new CompilerVisitor();
            
            // 2. Compila a AST e obtém o "cartucho" de bytecode
            BytecodeChunk chunk = compiler.compile(ast);

            Decompiler decompiler = new Decompiler();
            decompiler.disassemble(chunk);

            System.out.println(chunk);

            // 3. Verifica se a compilação falhou
            if (chunk == null) {
                System.err.println("Falha na compilação. Execução abortada.");
                return; // Para a execução
            }

            // --- FASE 4: EXECUÇÃO NA VM (Novo) ---
            System.out.println("\n--- FASE 4: EXECUÇÃO (VM) ---");
            
            // 1. Instancia a nova VM com o "cartucho"
            VM vm = new VM(chunk);
            
            // 2. Roda a VM
            vm.run();

            
            /* --- CÓDIGO DO MOTOR ANTIGO (DESATIVADO) ---
             * System.out.println("\n--- FASE 3: ANÁLISE SEMÂNTICA E EXECUÇÃO ---");
             * InterpreterVisitor visitor = new InterpreterVisitor();
             * for (Statement statement : ast) {
             * statement.accept(visitor);
             * }
             * --- FIM DO CÓDIGO ANTIGO --- */

        } catch (IOException e) {
            // Captura erros se o arquivo-fonte não puder ser lido.
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (RuntimeException e) {
            // Captura erros de compilação (Léxicos, Sintáticos, Compilação)
            // ou erros de Runtime da VM.
            System.err.println("Erro: " + e.getMessage());
            // Opcional: imprimir o stack trace para depuração
            // e.printStackTrace(); 
        }
    }
}