package ijgm_project.parser;

import ijgm_project.lexer.Token;
import ijgm_project.lexer.TokenType;
import ijgm_project.parser.ast.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Analisador Sintático (Parser).
 * Implementa o algoritmo Recursivo-Descendente para construir a AST.
 * Possui lógica de Recuperação de Erros (Panic-Mode).
 */
public class Parser {
    private final Iterator<Token> tokens;
    private Token currentToken;
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
            currentToken = tokens.next();
        } else {
            // Cria um token EOF (End Of File) para sinalizar o fim da análise
            currentToken = new Token(TokenType.EOF, "", 0, 0);
        }
    }

    /**
     * Consome o token atual, verificando se ele corresponde ao tipo esperado.
     * Implementa a lógica de Recuperação de Erros por Pânico.
     * 
     * @param expectedType O TokenType esperado.
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
     * 
     * @param message A mensagem de erro.
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
     * 
     * @return Uma lista de Statement's que compõem o programa.
     */
    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();

        // 1. Analisa as Declarações (devem vir no início)
        while (currentToken.getType() == TokenType.INT || currentToken.getType() == TokenType.FLOAT ||
                currentToken.getType() == TokenType.BOOL || currentToken.getType() == TokenType.STRING_TYPE) {
            statements.add(parseDeclaration());
        }

        // 2. Analisa os Comandos
        while (currentToken.getType() != TokenType.EOF) {
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
        TokenType type = currentToken.getType();
        consume(type);
        String varName = currentToken.getValue();
        consume(TokenType.IDENTIFIER);
        consume(TokenType.SEMICOLON);
        return new DeclarationStatement(type, varName);
    }

    /**
     * Regra: Comando -> Atribuição | Condicional | Repetição | Print
     */
    private Statement parseStatement() {
        return switch (currentToken.getType()) {
            case IDENTIFIER -> parseAssignment();
            case PRINT -> parsePrintStatement();
            case WHILE -> parseWhileStatement();
            case IF -> parseIfStatement();
            default -> throw new RuntimeException("Token inesperado: " + currentToken.getType() +
                    " na linha " + currentToken.getLine() + ", coluna " + currentToken.getColumn());
        };
    }

    /**
     * Regra: Atribuição -> id = Expressao ;
     */
    private Statement parseAssignment() {
        String varName = currentToken.getValue();
        consume(TokenType.IDENTIFIER);
        consume(TokenType.ASSIGN);
        Expression expr = parseExpression();
        consume(TokenType.SEMICOLON);
        return new AssignStatement(varName, expr);
    }

    /**
     * Regra: Print -> print Expressao ;
     */
    private Statement parsePrintStatement() {
        consume(TokenType.PRINT);
        Expression expr = parseExpression();
        consume(TokenType.SEMICOLON);
        return new PrintStatement(expr);
    }

    /**
     * Regra: Repetição (While) -> while (Expressao) Bloco
     */
    private Statement parseWhileStatement() {
        consume(TokenType.WHILE);
        consume(TokenType.OPEN_PAREN);
        Expression condition = parseExpression();
        consume(TokenType.CLOSE_PAREN);
        consume(TokenType.OPEN_BRACE);
        List<Statement> body = new ArrayList<>();
        while (currentToken.getType() != TokenType.CLOSE_BRACE) {
            body.add(parseStatement());
        }
        consume(TokenType.CLOSE_BRACE);
        return new WhileStatement(condition, body);
    }

    /**
     * Regra: Condicional (If-Else) -> if (Expressao) Bloco [else Bloco]
     */
    private Statement parseIfStatement() {
        consume(TokenType.IF);
        consume(TokenType.OPEN_PAREN);
        Expression condition = parseExpression();
        consume(TokenType.CLOSE_PAREN);
        consume(TokenType.OPEN_BRACE);
        List<Statement> thenBody = new ArrayList<>();
        while (currentToken.getType() != TokenType.CLOSE_BRACE) {
            thenBody.add(parseStatement());
        }
        consume(TokenType.CLOSE_BRACE);
        List<Statement> elseBody = null;
        if (currentToken.getType() == TokenType.ELSE) {
            consume(TokenType.ELSE);
            consume(TokenType.OPEN_BRACE);
            elseBody = new ArrayList<>();
            while (currentToken.getType() != TokenType.CLOSE_BRACE) {
                elseBody.add(parseStatement());
            }
            consume(TokenType.CLOSE_BRACE);
        }
        return new IfStatement(condition, thenBody, elseBody);
    }

    // ---------------------------------------------------------
    // Análise de Expressões (Implementação da Precedência)
    // A precedência é resolvida pela hierarquia de chamadas dos métodos:
    // OR (menor) -> AND -> Igualdade -> Comparação -> Adição -> Multiplicação ->
    // Primário (maior)
    // ---------------------------------------------------------

    private Expression parseExpression() {
        return parseLogicalOR(); // Inicia pela menor precedência
    }

    /**
     * Lógica OR (||)
     */
    private Expression parseLogicalOR() {
        Expression left = parseLogicalAND();
        while (currentToken.getType() == TokenType.OR) {
            TokenType operator = currentToken.getType();
            advance();
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
        while (currentToken.getType() == TokenType.AND) {
            TokenType operator = currentToken.getType();
            advance();
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
        while (currentToken.getType() == TokenType.EQUAL_EQUAL || currentToken.getType() == TokenType.NOT_EQUAL) {
            TokenType operator = currentToken.getType();
            advance();
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
        while (currentToken.getType() == TokenType.GREATER_THAN || currentToken.getType() == TokenType.GREATER_EQUAL ||
                currentToken.getType() == TokenType.LESS_THAN || currentToken.getType() == TokenType.LESS_EQUAL) {
            TokenType operator = currentToken.getType();
            advance();
            Expression right = parseAddition();
            left = new BinaryExpression(left, operator, right);
        }
        return left;
    }

    /**
     * Adição e Subtração (+, -)
     */
    private Expression parseAddition() {
        Expression left = parseMultiplication();
        while (currentToken.getType() == TokenType.PLUS || currentToken.getType() == TokenType.MINUS) {
            TokenType operator = currentToken.getType();
            advance();
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
        while (currentToken.getType() == TokenType.MULTIPLY || currentToken.getType() == TokenType.DIVIDE) {
            TokenType operator = currentToken.getType();
            advance();
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
        return switch (currentToken.getType()) {
            case NUMBER -> {
                NumberExpression expr = new NumberExpression(currentToken.getValue());
                consume(TokenType.NUMBER);
                yield expr;
            }
            case FLOAT_LITERAL -> {
                FloatExpression expr = new FloatExpression(currentToken.getValue());
                consume(TokenType.FLOAT_LITERAL);
                yield expr;
            }
            case IDENTIFIER -> {
                VariableExpression expr = new VariableExpression(currentToken.getValue());
                consume(TokenType.IDENTIFIER);
                yield expr;
            }
            case STRING -> {
                StringExpression expr = new StringExpression(currentToken.getValue());
                consume(TokenType.STRING);
                yield expr;
            }
            case TRUE -> {
                consume(TokenType.TRUE);
                yield new BooleanExpression(true);
            }
            case FALSE -> {
                consume(TokenType.FALSE);
                yield new BooleanExpression(false);
            }
            case OPEN_PAREN -> {
                consume(TokenType.OPEN_PAREN);
                Expression expr = parseExpression();
                consume(TokenType.CLOSE_PAREN);
                yield expr;
            }
            default -> throw new RuntimeException("Token inesperado na expressão: " + currentToken.getType() +
                    " na linha " + currentToken.getLine() + ", coluna " + currentToken.getColumn());
        };
    }
}