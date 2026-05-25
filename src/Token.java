/**
 * Token.java
 * Bir tokeni temsil eden sınıf.
 * Her token: tip (TokenType) + lexeme (String) içerir.
 */
public class Token {

    private final TokenType type;
    private final String    lexeme;

    public Token(TokenType type, String lexeme) {
        this.type   = type;
        this.lexeme = lexeme;
    }

    public TokenType getType()   { return type;   }
    public String    getLexeme() { return lexeme; }

    @Override
    public String toString() {
        return String.format("Token[%-14s | \"%s\"]", type, lexeme);
    }
}