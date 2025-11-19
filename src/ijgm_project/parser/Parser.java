package ijgm_project.parser;

import ijgm_project.lexer.Token;
import ijgm_project.lexer.TokenType;
import static ijgm_project.lexer.TokenType.*;
import ijgm_project.parser.ast.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Analisador Sintático (Parser).
 * Implementa o algoritmo Recursivo-Descendente para construir a AST.
 * Possui lógica de Recuperação de Erros (Panic-Mode).
 * (Refatorado para usar consume() corretamente nas regras de expressão).
 */
public class Parser {
    private final Iterator<Token> tokens;
    private Token currentToken;
    private Token previousToken;
    private boolean hadError = false;

    public Parser(List<Token> tokens) {
        this.tokens = tokens.iterator();
        advance();
    }

    /**
     * Avança para o próximo token no fluxo.
     */
    private void advance() {
        if (tokens.hasNext()) {
            previousToken = currentToken;
            currentToken = tokens.next();
        } else {
            // Cria um token EOF (End Of File) para sinalizar o fim da análise
            currentToken = new Token(TokenType.EOF, "", 0, 0);
        }
    }

    /**
     * Consome o token atual, verificando se ele corresponde ao tipo esperado.
     * Implementa a lógica de Recuperação de Erros por Pânico.
     * * @param expectedType O TokenType esperado.
     */
    private void consume(TokenType expectedType) {
        if (currentToken.getType() == expectedType) {
            advance();
        } else {
            // 1. Reporta o erro
            reportError("Esperado " + expectedType + ", mas encontrado " + currentToken.getType());
            // 2. Tenta se recuperar para continuar a análise
            synchronize();
        }
    }

    /**
     * Imprime a mensagem de erro sintático e marca que um erro ocorreu.
     * * @param message A mensagem de erro.
     */
    private void reportError(String message) {
        System.err.println("Erro Sintático: " + message + " na linha " + currentToken.getLine() + ", coluna "
                + currentToken.getColumn());
        this.hadError = true;
    }

    /**
     * Estratégia de Recuperação de Erros por Pânico.
     * Pula tokens até encontrar um ponto de sincronização seguro (principalmente
     * ';').
     */
    private void synchronize() {
        while (currentToken.getType() != TokenType.EOF) {
            // Ponto de sincronização forte: Fim de um comando (;)
            if (currentToken.getType() == TokenType.SEMICOLON) {
                advance();
                return;
            }

            // Ponto de sincronização fraco: Início de um novo comando ou declaração
            switch (currentToken.getType()) {
                case WHILE, PRINT, IF, ELSE, INT, FLOAT, BOOL, STRING_TYPE -> {
                    return; // Encontrou um ponto seguro para continuar
                }
                default -> {
                    // Para todos os outros tokens (números, operadores, parênteses, etc.),
                    // simplesmente não faz nada e o loop principal continua o avanço.
                }
            }
            // AQUI OCORRE O AVANÇO APÓS A VERIFICAÇÃO DO SWITCH
            advance(); // Pula o token atual
        }
    }

    /**
     * Regra principal: Programa -> Declarações Comandos
     * * @return Uma lista de Statement's que compõem o programa.
     */
    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();

        // 1. Analisa as Declarações (devem vir no início)
        while (check(INT, FLOAT, STRING, BOOL)) {
            statements.add(parseDeclaration());
        }

        // 2. Analisa os Comandos
        while (!check(EOF)) {
            statements.add(parseStatement());
        }

        // Lança exceção final se houver erros para impedir a execução do Interpreter
        if (this.hadError) {
            throw new RuntimeException("Análise sintática falhou. Corrija os erros acima.");
        }

        return statements;
    }

    /**
     * Regra: Declaração -> tipo id ;
     */
    private DeclarationStatement parseDeclaration() {
        // O loop 'parse()' já garantiu que currentToken é um tipo (INT, FLOAT, etc.)
        TokenType type = currentToken.getType();
        advance(); // Consome o token de tipo (ex: 'int')

        String varName = currentToken.getValue();
        consume(TokenType.IDENTIFIER); // Consome e valida o nome da variável
        consume(TokenType.SEMICOLON); // Consome e valida o ';'
        return new DeclarationStatement(type, varName);
    }

    /**
     * Regra: Comando -> Atribuição | Condicional | Repetição | Print
     */
    private Statement parseStatement() {

        if (match(IDENTIFIER))
            return parseAssignmentOrIncrementOrDecrement();
        if (match(OPEN_BRACE))
            return parseScope();
        if (match(PRINT))
            return parsePrintStatement();
        if (match(WHILE))
            return parseWhileStatement();
        if (match(IF))
            return parseIfStatement();

        // Erro: Token inesperado.
        reportError("Token inesperado no início de um comando: " + currentToken.getType());
        // Tenta pular para o próximo comando
        synchronize();
        // Lança exceção para parar a avaliação desta regra
        throw new RuntimeException("Token inesperado: " + currentToken.getType() +
                " na linha " + currentToken.getLine() + ", coluna " + currentToken.getColumn());

    }

    private Statement parseScope() {
        List<Statement> statements = new ArrayList<>();
        while (!check(CLOSE_BRACE, EOF)) {
            statements.add(parseStatement());
        }

        if (currentToken.getType() == TokenType.EOF) {
            reportError("Escopo não fechado. Esperado '}' mas encontrou Fim de Arquivo.");
        } else {
            consume(TokenType.CLOSE_BRACE); // Consome o '}'
        }
        return new ScopeStatement(statements);
    }

    /**
     * Regra: Bloco -> { (Declaração | Comando)* }
     * (Helper para 'if' e 'while')
     */
    private List<Statement> parseBlock() {
        consume(TokenType.OPEN_BRACE);
        List<Statement> statements = new ArrayList<>();
        while (currentToken.getType() != TokenType.CLOSE_BRACE && currentToken.getType() != TokenType.EOF) {
            // O parser original só permitia comandos dentro de blocos (não declarações)
            statements.add(parseStatement());
        }

        if (currentToken.getType() == TokenType.EOF) {
            reportError("Bloco não fechado. Esperado '}' mas encontrou Fim de Arquivo.");
        } else {
            consume(TokenType.CLOSE_BRACE); // Consome o '}'
        }
        return statements;
    }

    /**
     * Regra: Atribuição -> id = Expressao ;
     * OU
     * Regra: Incremento -> id ++ ;
     * OU
     * Regra: Decremento -> id -- ;
     */
    private Statement parseAssignmentOrIncrementOrDecrement() {
        String varName = previous().getValue();
        if (null == currentToken.getType()) {
            reportError("Esperado '=', '++' ou '--' após o identificador '" + varName + "'");
            synchronize();
            throw new RuntimeException("Sintaxe de atribuição, incremento ou decremento inválida.");
        }

        if (match(ASSIGN)) {
            Expression expr = parseExpression();
            consume(TokenType.SEMICOLON);
            return new AssignStatement(varName, expr);
        }

        if (match(INCREMENT)) {
            consume(TokenType.SEMICOLON);
            return new IncrementStatement(varName);
        }

        if (match(DECREMENT)) {
            consume(TokenType.SEMICOLON);
            return new DecrementStatement(varName);
        }

        reportError("Esperado '=', '++' ou '--' após o identificador '" + varName + "'");
        synchronize();
        throw new RuntimeException("Sintaxe de atribuição, incremento ou decremento inválida.");
    }

    /**
     * Regra: Print -> print Expressao ;
     */
    private Statement parsePrintStatement() {
        Expression expr = parseExpression();
        consume(TokenType.SEMICOLON);
        return new PrintStatement(expr);
    }

    /**
     * Regra: Repetição (While) -> while (Expressao) Bloco
     */
    private Statement parseWhileStatement() {
        consume(TokenType.OPEN_PAREN);
        Expression condition = parseExpression();
        consume(TokenType.CLOSE_PAREN);
        List<Statement> body = parseBlock(); // Usa a função helper parseBlock
        return new WhileStatement(condition, body);
    }

    /**
     * Regra: Condicional (If-Else) -> if (Expressao) Bloco [else Bloco]
     */
    private Statement parseIfStatement() {
        consume(TokenType.OPEN_PAREN);
        Expression condition = parseExpression();
        consume(TokenType.CLOSE_PAREN);
        List<Statement> thenBody = parseBlock(); // Usa a função helper parseBlock
        List<Statement> elseBody = null;
        if (currentToken.getType() == TokenType.ELSE) {
            consume(TokenType.ELSE);
            // O parser original permitia 'else if' ou 'else { ... }'
            // Este parser refatorado só permite 'else { ... }'
            elseBody = parseBlock(); // Usa a função helper parseBlock
        }
        return new IfStatement(condition, thenBody, elseBody);
    }

    // ---------------------------------------------------------
    // Análise de Expressões (Implementação da Precedência)
    // CORREÇÃO APLICADA: Trocado 'advance()' por 'consume(operator)'
    // ---------------------------------------------------------

    private Expression parseExpression() {
        return parseLogicalOR(); // Inicia pela menor precedência
    }

    /**
     * Lógica OR (||)
     */
    private Expression parseLogicalOR() {
        Expression left = parseLogicalAND();
        while (match(OR)) {
            TokenType operator = previous().getType();
            Expression right = parseLogicalAND();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * Lógica AND (&&)
     */
    private Expression parseLogicalAND() {
        Expression left = parseEquality();
        while (match(AND)) {
            TokenType operator = previous().getType();
            Expression right = parseEquality();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * Igualdade (==, !=)
     */
    private Expression parseEquality() {
        Expression left = parseComparison();
        while (match(EQUAL_EQUAL, NOT_EQUAL)) {
            TokenType operator = previous().getType();
            Expression right = parseComparison();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * Comparação (>, <, >=, <=)
     */

    private Expression parseComparison() {
        Expression left = parseAddition();

        // Troca o 'while' (que permite repetição) por um 'if' (que permite no máximo
        // uma vez)
        if (match(GREATER_EQUAL, GREATER_THAN, LESS_EQUAL, LESS_THAN)) {
            TokenType operator = previous().getType();
            Expression right = parseAddition();
            left = new BinaryExpression(left, operator, right);

            // Se houver mais tokens de comparação, eles serão processados pela próxima
            // chamada
            // de parseExpression() (ou causarão um erro dependendo da regra superior).
            // No caso de 'a > b > c', o segundo '>' causaria um erro sintático aqui.
        }
        return left;
    }

    /**
     * Adição e Subtração (+, -)
     */
    private Expression parseAddition() {
        Expression left = parseMultiplication();
        while (match(PLUS, MINUS)) {
            TokenType operator = previous().getType();
            Expression right = parseMultiplication();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * Multiplicação e Divisão (*, /)
     */
    private Expression parseMultiplication() {
        Expression left = parsePrimary();
        while (match(MULTIPLY, DIVIDE)) {
            TokenType operator = previous().getType();
            Expression right = parsePrimary();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * Primário: Literais, Identificadores ou Expressões entre parênteses. (Maior
     * Precedência)
     */
    private Expression parsePrimary() {
        if (match(NUMBER))
            return new NumberExpression(previous().getValue());
        if (match(FLOAT_LITERAL))
            return new FloatExpression(previous().getValue());
        if (match(IDENTIFIER))
            return new VariableExpression(previous().getValue());
        if (match(STRING))
            return new StringExpression(previous().getValue());
        if (match(TRUE))
            return new BooleanExpression(true);
        if (match(FALSE))
            return new BooleanExpression(false);
        if (match(OPEN_PAREN)) {
            return groupExpression();
        }

        // Erro: Token inesperado.
        reportError("Token inesperado na expressão: " + currentToken.getType());
        // Tenta sincronizar para continuar a análise
        synchronize();
        // Lança exceção para parar a avaliação desta regra
        throw new RuntimeException("Expressão primária inválida: " + currentToken.getType() +
                " na linha " + currentToken.getLine() + ", coluna " + currentToken.getColumn());
    }

    private Expression groupExpression() {
        Expression expr = parseExpression();
        consume(TokenType.CLOSE_PAREN);
        return expr;
    }

    private boolean check(TokenType... types) {
        for (TokenType type : types) {
            if (currentToken.getType() == type)
                return true;
        }

        return false;
    }

    private boolean match(TokenType... types) {
        if (check(types)) {
            advance();
            return true;
        }

        return false;
    }

    Token previous() {
        return previousToken;
    }
}