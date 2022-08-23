
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    // * 8. Define identificadores(palabras claves).
    static {
        keywords = new HashMap<>();
        keywords.put("and", TokenType.AND);
        keywords.put("class", TokenType.CLASS);
        keywords.put("else", TokenType.ELSE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("fun", TokenType.FUN);
        keywords.put("if", TokenType.IF);
        keywords.put("nil", TokenType.NIL);
        keywords.put("or", TokenType.OR);
        keywords.put("print", TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super", TokenType.SUPER);
        keywords.put("this", TokenType.THIS);
        keywords.put("true", TokenType.TRUE);
        keywords.put("var", TokenType.VAR);
        keywords.put("while", TokenType.WHILE);
    }

    Scanner(String source) {
        // * 1. Entra el source code, cadenas de texto.
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) { // * 2. Revisa si la posición actual es mayor al tamaño de la cadena.
            // * 3. start es el inicio de un lexema. Por ahora siempre será el current.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "lexeme", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        // * 4. scanToken revisa el caracter en current para determinar el token.
        char c = advance(); // * 5. advance devuelve el character en current++ - 1. start pasa a tener un
                            // * valor menos.
        switch (c) {
            case '(':
                // * 6. addToken() crea el token y lo añade a tokens.
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.START);
                break;
            case '!':
                // * 6. Si el valor siguiente es igual a match, añade el token
                // * correspondiente.||
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.GREATER);
                break;
            case '/':
                if (match('/')) {
                    // A comment goes until the end of line.
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else {
                    addToken(TokenType.SLASH);
                }
                break;

            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;

            default:
                // * 7
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    // * Solo importa si es un grupo alphanumerico. No revisa is es válido
                    // * (por ahora)
                    identifier();
                } else {
                    Jami.error(line, "Unexpected character.");
                }

                break;
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        addToken(TokenType.IDENTIFIER);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek()))
            advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek()))
                advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }

        if (isAtEnd()) {
            Jami.error(line, "Unterminated string");
            return;
        }

        advance();

        // * current - 1, ya que advance avanza uno extra después de la comilla.
        // * start + 1, ya que start es la comilla.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    private boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    private char advance() {
        current++;
        return source.charAt(current - 1);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
