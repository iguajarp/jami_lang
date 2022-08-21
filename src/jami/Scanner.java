package jami;

import java.util.ArrayList;
import java.util.List;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

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
        }
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
