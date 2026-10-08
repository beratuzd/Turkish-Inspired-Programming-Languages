
public enum TokenType {

    // ─── Anahtar Sözcükler (Keywords) ─────────────────────────────────────────
    DEGISKEN,   // degisken  → variable declaration
    EGER,       // eger      → if
    YOKSA,      // yoksa     → else
    DONGU,      // dongu     → while
    ICIN,       // icin      → for
    YAZDIR,     // yazdir    → print
    DOGRU,      // dogru     → boolean true
    YANLIS,     // yanlis    → boolean false
    VE,         // ve        → logical AND
    VEYA,       // veya      → logical OR
    DEGIL,      // degil     → logical NOT

    // ─── Değişmezler / Literals ───────────────────────────────────────────────
    INT_LITERAL,    // 0, 1, 42, 100, ...

    // ─── Tanımlayıcı / Identifier ─────────────────────────────────────────────
    IDENTIFIER,     // user-defined names

    // ─── Arithmetic Operators ────────────────────────
    PLUS,       // +
    MINUS,      // -
    MULT,       // *
    DIV,        // /

    // ─── Relational Operators ────────────────────────
    EQ,         // ==
    NEQ,        // !=
    LT,         // <
    GT,         // >
    LTE,        // <=
    GTE,        // >=

    // ─── Assignment ───────────────────────────────────────────────────
    ASSIGN,     // =

    // ─── Increase / Decrease ────────────────────────────────────────────────────
    INCREMENT,  // ++
    DECREMENT,  // --

    // ─── Separators ─────────────────────────────────────────────
    LPAREN,     // (
    RPAREN,     // )
    LBRACE,     // {
    RBRACE,     // }
    SEMICOLON,  // ;
    COMMA,      // ,

    // ─── Special ───────────────────────────────────────────────────────
    EOF,        // end of file
    UNKNOWN     // unrecognized character → lexical error
}
