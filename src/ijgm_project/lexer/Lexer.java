package ijgm_project.lexer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Analisador Léxico (Scanner).
 * Implementa um Autômato Finito Determinístico (AFD) manual para transformar
 * o código-fonte em uma sequência de tokens.
 * * (Refatorado para quebrar responsabilidades, centralizar erros e usar
 * helpers).
 */
public class Lexer {
    private final String sourceCode;
    private final List<Token> tokens = new ArrayList<>();
    private int position = 0;
    private int line = 1;
    private int column = 1;
    private int start = 0; // Marca o início do lexema atual

    public Lexer(String filePath) throws IOException {
        this.sourceCode = new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Método principal para a análise léxica.
     * Percorre o código-fonte caractere por caractere e identifica os tokens.
     * * @return Uma lista sequencial de objetos Token.
     */
    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = position; // Marca o início do novo token
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, column));
        return tokens;
    }

    /**
     * Verifica se o analisador chegou ao fim do código-fonte.
     * * @return true se a posição atual for maior ou igual ao tamanho do código.
     */
    private boolean isAtEnd() {
        return position >= sourceCode.length();
    }

    /**
     * Lança uma exceção padronizada para erros léxicos.
     * * @param message A mensagem de erro específica.
     */
    private void lexicalError(String message) {
        throw new RuntimeException(
                "Erro Léxico: " + message + " na linha " + line + ", coluna " + (column - (position - start)));
    }

    /**
     * Avança a posição no código-fonte e retorna o caractere consumido.
     * Também lida com a atualização de linha e coluna.
     * * @return O caractere na posição atual (antes de avançar).
     */
    private char advance() {
        char currentChar = sourceCode.charAt(position);
        position++;

        if (currentChar == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return currentChar;
    }

    /**
     * Adiciona um token à lista.
     * * @param type O tipo de token (TokenType).
     */
    private void addToken(TokenType type) {
        String value = sourceCode.substring(start, position);
        tokens.add(new Token(type, value, line, (column - value.length())));
    }

    /**
     * Adiciona um token à lista (sobrecarga para literais).
     * * @param type  O tipo de token (TokenType).
     * @param value O valor literal (para Strings, Números).
     */
    private void addToken(TokenType type, String value) {
        tokens.add(new Token(type, value, line, (column - (position - start))));
    }

    /**
     * Olha o próximo caractere sem avançar a posição (Lookahead).
     * Este é o 'peek()' do código original.
     * * @return O próximo caractere ou '\0' se estiver no fim do arquivo.
     */

     // TODO: a função olha o caractere atual, a doc está incorreta
    private char peekNext() {
        if (position >= sourceCode.length()) { // Nota: Verificamos 'position', não 'position + 1'
            return '\0';
        }
        return sourceCode.charAt(position); // 'position' é o *próximo* caractere a ser lido
    }

    /**
     * Função 'match' (como solicitado).
     * Se o *próximo* caractere for o esperado, consome-o (avança) e retorna true.
     * Caso contrário, retorna false.
     * * @param expected O caractere esperado no *lookahead*.
     * @return true se o próximo caractere bater e for consumido.
     */
    private boolean match(char expected) {
        if (peekNext() == expected) {
            advance(); // Consome o caractere esperado
            return true;
        }
        return false;
    }

    /**
     * Rotina principal de escaneamento. Despacha para a rotina correta.
     */
    private void scanToken() {
        char c = advance(); // Consome o primeiro caractere do lexema

        switch (c) {
            // Símbolos de um caractere
            case '(':
                addToken(TokenType.OPEN_PAREN);
                break;
            case ')':
                addToken(TokenType.CLOSE_PAREN);
                break;
            case '{':
                addToken(TokenType.OPEN_BRACE);
                break;
            case '}':
                addToken(TokenType.CLOSE_BRACE);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '*':
                addToken(TokenType.MULTIPLY);
                break;

            // Símbolos de um ou dois caracteres (usando match)
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.ASSIGN);
                break;
            case '!':
                if (match('=')) {
                    addToken(TokenType.NOT_EQUAL);
                } else {
                    lexicalError("Caractere inesperado '!'");
                }
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER_THAN);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS_THAN);
                break;
            case '&':
                if (match('&')) {
                    addToken(TokenType.AND);
                } else {
                    lexicalError("Caractere inesperado '&'");
                }
                break;
            case '|':
                if (match('|')) {
                    addToken(TokenType.OR);
                } else {
                    lexicalError("Caractere inesperado '|'");
                }
                break;

            // Comentários ou Divisão
            case '/':
                if (match('/')) { // Comentário de linha (ex: //)
                    // Consome até o fim da linha
                    while (peekNext() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else if (match('*')) { // Comentário de bloco (ex: /* ... */)
                    skipBlockComment();
                } else {
                    addToken(TokenType.DIVIDE);
                }
                break;

            // Literais de String
            case '"':
                scanString();
                break;


            // Como advance() já cuidou de line++, esses dois cases podem ser unidos em um só
            // Espaços em branco (ignorados)
            case ' ', '\r', '\t':
                break; // Ignora
            case '\n':
                // 'advance()' já cuidou de 'line' e 'column'
                break;

            // Default: Números, Identificadores ou Erros
            default:
                if (Character.isDigit(c)) {
                    scanNumber();
                } else if (Character.isLetter(c)) {
                    scanIdentifier();
                } else {
                    lexicalError("Caractere inválido: '" + c + "'");
                }
                break;
        }
    }

    /**
     * Consome um comentário de bloco (ex: /* ... * /).
     */
    private void skipBlockComment() {
        // O '/*' já foi consumido por advance() e match()
        while (!(peekNext() == '*' && position + 1 < sourceCode.length() && sourceCode.charAt(position + 1) == '/')
                && !isAtEnd()) {
            advance(); // advance() lida com quebras de linha
        }

        if (isAtEnd()) {
            lexicalError("Bloco de comentário não terminado.");
            return;
        }

        // Consome os dois caracteres finais '*/'
        advance(); // Consome o '*'
        advance(); // Consome o '/'
    }

    /**
     * Consome um literal de string (ex: "texto").
     */
    private void scanString() {
        // O '"' inicial foi consumido por advance()
        StringBuilder sb = new StringBuilder();

        while (peekNext() != '"' && !isAtEnd()) {
            if (peekNext() == '\n') {
                lexicalError("String literal não fechada na linha.");
                return; // Sai para a exceção ser lançada
            }
            sb.append(advance());
        }

        if (isAtEnd()) {
            lexicalError("String literal não terminada.");
            return;
        }

        advance(); // Consome o '"' final

        // Adiciona o token usando o valor do StringBuilder
        addToken(TokenType.STRING, sb.toString());
    }

    /**
     * Consome um literal numérico (int ou float).
     */
    private void scanNumber() {
        // O primeiro dígito já foi consumido por advance()
        StringBuilder numberBuilder = new StringBuilder();
        numberBuilder.append(sourceCode.charAt(start)); // Adiciona o primeiro dígito

        // Lê a parte inteira
        while (Character.isDigit(peekNext())) {
            numberBuilder.append(advance());
        }

        // Verifica e lê a parte decimal (Float)
        if (peekNext() == '.' && position + 1 < sourceCode.length()
                && Character.isDigit(sourceCode.charAt(position + 1))) {
            numberBuilder.append(advance()); // Consome o '.'

            while (Character.isDigit(peekNext())) {
                numberBuilder.append(advance());
            }
            addToken(TokenType.FLOAT_LITERAL, numberBuilder.toString());
        } else {
            // Se não for float, é um inteiro
            addToken(TokenType.NUMBER, numberBuilder.toString());
        }
    }

    /**
     * Consome um identificador ou palavra-reservada.
     */
    private void scanIdentifier() {
        // A primeira letra já foi consumida por advance()
        StringBuilder idBuilder = new StringBuilder();
        idBuilder.append(sourceCode.charAt(start)); // Adiciona a primeira letra

        while (Character.isLetterOrDigit(peekNext())) {
            idBuilder.append(advance());
        }

        String id = idBuilder.toString();
        // Mapeamento para Palavras-Reservadas e Tipos
        TokenType type = switch (id) {
            case "while" -> TokenType.WHILE;
            case "print" -> TokenType.PRINT;
            case "if" -> TokenType.IF;
            case "else" -> TokenType.ELSE;
            case "int" -> TokenType.INT;
            case "float" -> TokenType.FLOAT;
            case "bool" -> TokenType.BOOL;
            case "string" -> TokenType.STRING_TYPE;
            case "true" -> TokenType.TRUE;
            case "false" -> TokenType.FALSE;
            default -> TokenType.IDENTIFIER;
        };
        addToken(type); // Adiciona o token (o 'value' é pego pelo substring)
    }
}