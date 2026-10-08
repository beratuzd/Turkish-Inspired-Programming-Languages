
public class Token {

    private final TokenType type;

    private final String value;

    private final int line;

    // ─── Yapılandırıcı / Constructor ──────────────────────────────────────────

    public Token(TokenType type, String value, int line) {
        this.type  = type;
        this.value = value;
        this.line  = line;
    }

    // ─── Erişimciler / Getters ────────────────────────────────────────────────

    public TokenType getType()  { return type;  }
    public String    getValue() { return value; }
    public int       getLine()  { return line;  }

    // ─── String Gösterimi / String Representation ────────────────────────────

    @Override
    public String toString() {
        return String.format("[%-14s | %-12s | satir: %d]", type, "\"" + value + "\"", line);
    }
}
