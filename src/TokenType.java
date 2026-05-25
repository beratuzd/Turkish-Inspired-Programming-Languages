/**
 * TokenType.java
 * Sebesta Chapter 4 int sabitlerinin enum karşılığı.
 * Grubumuzun Türkçe dil tasarımına göre genişletildi.
 */
public enum TokenType {

    // ── Literaller & Tanımlayıcılar ──────────────────────────────────────────
    INT_LIT,        // Tamsayı sabiti       örn: 42
    FLOAT_LIT,      // Ondalık sabiti       örn: 3.14
    STRING_LIT,     // String sabiti        örn: "merhaba"
    CHAR_LIT,       // Karakter sabiti      örn: 'a'
    IDENT,          // Değişken/fonksiyon adı

    // ── Tip Anahtar Kelimeleri ────────────────────────────────────────────────
    KW_TAM,         // "tam"    → int
    KW_NMR,         // "nmr"    → int (alternatif)
    KW_ONDLK,       // "ondlk"  → double
    KW_CML,         // "cml"    → String
    KW_KRKTR,       // "krktr"  → char

    // ── Kontrol Akışı Anahtar Kelimeleri ─────────────────────────────────────
    KW_EGER,        // "eger"   → if
    KW_DEGILSE,     // "degilse"→ else
    KW_DONGU,       // "dongu"  → while
    KW_HER,         // "her"    → for

    // ── Diğer Anahtar Kelimeler ───────────────────────────────────────────────
    KW_YZDR,        // "yzdr"   → print
    KW_VE,          // "ve"     → &&
    KW_VEYA,        // "veya"   → ||

    // ── Atama & Karşılaştırma Operatörleri ───────────────────────────────────
    ASSIGN_OP,      // =
    EQ_OP,          // ?=   (eşit mi?)
    LT_OP,          // <
    GT_OP,          // >
    LE_OP,          // <=
    GE_OP,          // >=
    NEQ_OP,         // !?=  (eşit değil mi?) -- opsiyonel

    // ── Aritmetik Operatörler ─────────────────────────────────────────────────
    ADD_OP,         // +
    SUB_OP,         // -
    MULT_OP,        // *
    DIV_OP,         // /
    MOD_OP,         // kalan  (%)

    // ── Ayraçlar ─────────────────────────────────────────────────────────────
    LEFT_PAREN,     // )   ← açar  (ters parantez tasarımı)
    RIGHT_PAREN,    // (   ← kapatır
    LEFT_BRACE,     // [[  ← blok açar
    RIGHT_BRACE,    // ]]  ← blok kapatır
    SEMICOLON,      // :   ← satır sonu
    COMMA,          // ,

    // ── Özel ─────────────────────────────────────────────────────────────────
    EOF,
    UNKNOWN
}