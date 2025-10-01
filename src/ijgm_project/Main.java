package ijgm_project;

import ijgm_project.lexer.Lexer;
import ijgm_project.lexer.Token;
import ijgm_project.parser.Parser;
import ijgm_project.parser.ast.Statement;
import ijgm_project.visitor.InterpreterVisitor;
import ijgm_project.visitor.PrintVisitor;

import java.io.IOException;
import java.util.List;

/**
 * Classe principal que orquestra as três fases do compilador:
 * 1. Análise Léxica (Tokenização)
 * 2. Análise Sintática (Construção da AST)
 * 3. Análise Semântica e Execução (Interpretação)
 * * É o ponto de entrada do projeto.
 */
public class Main {
    /**
     * Método principal que inicia o processo de compilação/interpretação.
     * 
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Define o caminho para o arquivo de código-fonte a ser lido.
        // O caminho "input/exemplo.txt" assume que o arquivo está na raiz do projeto.
        String filePath = "input/exemplo.txt";

        try {
            // --- FASE 1: ANÁLISE LÉXICA ---
            System.out.println("--- FASE 1: ANÁLISE LÉXICA ---");
            // 1. Instancia o Lexer e lê o arquivo.
            Lexer lexer = new Lexer(filePath);
            // 2. Converte a entrada em uma lista sequencial de Tokens.
            List<Token> tokens = lexer.tokenize();
            System.out.println("Tokens gerados: " + tokens);

            // --- FASE 2: ANÁLISE SINTÁTICA ---
            System.out.println("\n--- FASE 2: ANÁLISE SINTÁTICA (CONSTRUÇÃO DA AST) ---");
            // 1. Instancia o Parser com a lista de tokens.
            Parser parser = new Parser(tokens);
            // 2. Constrói a Árvore de Sintaxe Abstrata (AST).
            // O parser.parse() também lida com a recuperação de erros sintáticos.
            List<Statement> ast = parser.parse();

            // Exibição da Árvore de Sintaxe Abstrata (AST)
            System.out.println("Representação da Árvore de Sintaxe Abstrata:");
            // 1. Instancia o PrintVisitor (Padrão Visitor para visualização).
            PrintVisitor printVisitor = new PrintVisitor();
            // 2. Percorre a AST para imprimir sua estrutura hierárquica.
            for (Statement statement : ast) {
                statement.accept(printVisitor);
            }

            // --- FASE 3: ANÁLISE SEMÂNTICA E EXECUÇÃO ---
            System.out.println("\n--- FASE 3: ANÁLISE SEMÂNTICA E EXECUÇÃO ---");
            // 1. Instancia o InterpreterVisitor (Padrão Visitor para
            // execução/interpretação).
            InterpreterVisitor visitor = new InterpreterVisitor();
            // 2. Percorre a AST para executar o código.
            // O Visitor realiza a checagem de tipos e executa a lógica do programa.
            for (Statement statement : ast) {
                statement.accept(visitor);
            }

        } catch (IOException e) {
            // Captura erros se o arquivo-fonte não puder ser lido.
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (RuntimeException e) {
            // Captura erros de compilação (Léxicos, Sintáticos, Semânticos/Tipagem)
            // e impede que o programa continue a execução se houver falhas.
            System.err.println("Erro de compilação: " + e.getMessage());
        }
    }
}