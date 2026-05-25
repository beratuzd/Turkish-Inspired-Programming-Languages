import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Lexer.java
 * ──────────────────────────────────────────────────────────────────────────────
 * Sebesta "Concepts of Programming Languages" 10th Ed., Chapter 4 (s.172)
 * örneğindeki lex(), getChar(), addChar(), getNonBlank(), lookup() yapısı
 * korunarak Türkçe dil tasarımımıza göre genişletildi.
 *
 * DEĞİŞTİRİLENLER (textbook → bizim tasarım):
 *   - int sabitleri  → TokenType enum
 *   - Tek karakter lookup → operatör çiftleri (?=, <=, >=, [[, ]]) eklendi
 *   - Türkçe karakter desteği (ğ,ş,ç,ı,ö,ü) identifier içinde geçerli
 *   - Keyword tablosu (HashMap) eklendi
 *   - String ve char literal tanıma eklendi
 *
 * DİL SÖZDİZİMİ NOTLARI:
 *   )   → sol parantez (açar)
 *   (   → sağ parantez (kapatır)
 *   [[  → blok başı
 *   ]]  → blok sonu
 *   :   → satır sonu (semicolon)
 *   =   → atama
 *   ?=  → eşitlik karşılaştırması
 */
public class Lexer {

    // ── Karakter sınıfları (Sebesta s.172 ile aynı) ──────────────────────────
    private static final int CLS_LETTER  = 0;
    private static final int CLS_DIGIT   = 1;
    private static final int CLS_UNKNOWN = 99;
    private static final int CLS_EOF     = -1;

    // ── Durum ─────────────────────────────────────────────────────────────────
    private int           charClass;
    private char          nextChar;
    private final StringBuilder lexeme = new StringBuilder();
    private Token         currentToken;
    private final Reader  reader;

    // ── Keyword tablosu ───────────────────────────────────────────────────────
    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();
    static {
        // Tip bildirimleri
        KEYWORDS.put("tam",      TokenType.KW_TAM);
        KEYWORDS.put("nmr",      TokenType.KW_NMR);
        KEYWORDS.put("ondlk",    TokenType.KW_ONDLK);
        KEYWORDS.put("cml",      TokenType.KW_CML);
        KEYWORDS.put("krktr",    TokenType.KW_KRKTR);
        // Kontrol akışı
        KEYWORDS.put("eger",     TokenType.KW_EGER);
        KEYWORDS.put("degilse",  TokenType.KW_DEGILSE);
        KEYWORDS.put("dongu",    TokenType.KW_DONGU);
        KEYWORDS.put("her",      TokenType.KW_HER);
        // Diğer
        KEYWORDS.put("yzdr",     TokenType.KW_YZDR);
        KEYWORDS.put("ve",       TokenType.KW_VE);
        KEYWORDS.put("veya",     TokenType.KW_VEYA);
    }

    // ── Constructor ───────────────────────────────────────────────────────────
    public Lexer(String sourceCode) {
        this.reader = new StringReader(sourceCode);
        getChar(); // İlk karakteri oku
    }

    public Lexer(File file) throws FileNotFoundException {
        this.reader = new BufferedReader(new FileReader(file));
        getChar();
    }

    // ── PUBLIC API ────────────────────────────────────────────────────────────

    /**
     * Bir sonraki token'i döndürür.
     * Sebesta'nın lex() fonksiyonunun genişletilmiş hali.
     */
    public Token nextToken() {
        lexeme.setLength(0);
        getNonBlank();

        switch (charClass) {

            // ── İdentifier veya keyword ───────────────────────────────────────
            case CLS_LETTER:
                return readIdentOrKeyword();

            // ── Sayı literal ──────────────────────────────────────────────────
            case CLS_DIGIT:
                return readNumber();

            // ── EOF ───────────────────────────────────────────────────────────
            case CLS_EOF:
                currentToken = new Token(TokenType.EOF, "EOF");
                return currentToken;

            // ── Operatör / ayraç / string / char ─────────────────────────────
            default:
                return readSymbol();
        }
    }

    /** Mevcut token'i döndürür (tekrar okumadan). */
    public Token getCurrentToken() { return currentToken; }

    // ── IDENTIFIER / KEYWORD PARSING ─────────────────────────────────────────

    /**
     * Harf ile başlayan lexeme'i okur.
     * Türkçe harfler (ğ ş ç ı ö ü) identifier içinde geçerlidir.
     * Sebesta s.172: identifier döngüsü → keyword kontrolü eklendi.
     */
    private Token readIdentOrKeyword() {
        addChar();
        getChar();
        while (charClass == CLS_LETTER || charClass == CLS_DIGIT || isTurkishLetter(nextChar)) {
            addChar();
            getChar();
        }
        String word = lexeme.toString();
        // Keyword tablosuna bak; yoksa IDENT
        TokenType type = KEYWORDS.getOrDefault(word, TokenType.IDENT);
        currentToken = new Token(type, word);
        return currentToken;
    }

    // ── NUMBER PARSING ────────────────────────────────────────────────────────

    /**
     * Rakamla başlayan lexeme'i okur.
     * Nokta görürse FLOAT_LIT, aksi halde INT_LIT döner.
     */
    private Token readNumber() {
        addChar();
        getChar();
        while (charClass == CLS_DIGIT) {
            addChar();
            getChar();
        }
        // Ondalık kısım var mı?
        if (nextChar == '.' ) {
            addChar();
            getChar();
            while (charClass == CLS_DIGIT) {
                addChar();
                getChar();
            }
            currentToken = new Token(TokenType.FLOAT_LIT, lexeme.toString());
        } else {
            currentToken = new Token(TokenType.INT_LIT, lexeme.toString());
        }
        return currentToken;
    }

    // ── OPERATOR / SEPARATOR PARSING ─────────────────────────────────────────

    /**
     * Tek veya çift karakterli semboller.
     * Sebesta'nın lookup() fonksiyonunun genişletilmiş hali.
     * Eklenenler: ?=  <=  >=  [[  ]]  :  string/char literal
     */
    private Token readSymbol() {
        TokenType type;

        switch (nextChar) {

            // ── Parantezler (ters tasarım) ────────────────────────────────────
            case ')': addChar(); getChar(); type = TokenType.LEFT_PAREN;  break;
            case '(': addChar(); getChar(); type = TokenType.RIGHT_PAREN; break;

            // ── Blok ayraçları [[ ve ]] ───────────────────────────────────────
            case '[':
                addChar(); getChar();
                if (nextChar == '[') { addChar(); getChar(); type = TokenType.LEFT_BRACE; }
                else                 { type = TokenType.UNKNOWN; }
                break;
            case ']':
                addChar(); getChar();
                if (nextChar == ']') { addChar(); getChar(); type = TokenType.RIGHT_BRACE; }
                else                 { type = TokenType.UNKNOWN; }
                break;

            // ── Satır sonu ────────────────────────────────────────────────────
            case ':': addChar(); getChar(); type = TokenType.SEMICOLON; break;

            // ── Virgül ────────────────────────────────────────────────────────
            case ',': addChar(); getChar(); type = TokenType.COMMA; break;

            // ── Atama  =  ─────────────────────────────────────────────────────
            case '=': addChar(); getChar(); type = TokenType.ASSIGN_OP; break;

            // ── Eşitlik  ?=  ──────────────────────────────────────────────────
            case '?':
                addChar(); getChar();
                if (nextChar == '=') { addChar(); getChar(); type = TokenType.EQ_OP; }
                else                 { type = TokenType.UNKNOWN; }
                break;

            // ── Küçük / küçük-eşit ───────────────────────────────────────────
            case '<':
                addChar(); getChar();
                if (nextChar == '=') { addChar(); getChar(); type = TokenType.LE_OP; }
                else                 { type = TokenType.LT_OP; }
                break;

            // ── Büyük / büyük-eşit ───────────────────────────────────────────
            case '>':
                addChar(); getChar();
                if (nextChar == '=') { addChar(); getChar(); type = TokenType.GE_OP; }
                else                 { type = TokenType.GT_OP; }
                break;

            // ── Eşit değil  !=  ───────────────────────────────────────────────
            case '!':
                addChar(); getChar();
                if (nextChar == '=') { addChar(); getChar(); type = TokenType.NEQ_OP; }
                else                 { type = TokenType.UNKNOWN; }
                break;

            // ── Aritmetik operatörler ─────────────────────────────────────────
            case '+': addChar(); getChar(); type = TokenType.ADD_OP;  break;
            case '-': addChar(); getChar(); type = TokenType.SUB_OP;  break;
            case '*': addChar(); getChar(); type = TokenType.MULT_OP; break;
            case '/': addChar(); getChar(); type = TokenType.DIV_OP;  break;
            case '%': addChar(); getChar(); type = TokenType.MOD_OP;  break;

            // ── String literal  "..."  ────────────────────────────────────────
            case '"':
                type = readStringLiteral();
                break;

            // ── Char literal  '.'  ────────────────────────────────────────────
            case '\'':
                type = readCharLiteral();
                break;

            // ── Bilinmeyen karakter ───────────────────────────────────────────
            default:
                addChar(); getChar();
                type = TokenType.UNKNOWN;
                System.out.printf("LEXER HATASI: Tanınmayan karakter '%s'%n",
                                  lexeme.toString());
                break;
        }

        currentToken = new Token(type, lexeme.toString());
        return currentToken;
    }

    // ── STRING LITERAL ────────────────────────────────────────────────────────

    private TokenType readStringLiteral() {
        addChar(); // açılış "
        getChar();
        while (charClass != CLS_EOF && nextChar != '"') {
            addChar();
            getChar();
        }
        if (nextChar == '"') { addChar(); getChar(); } // kapanış "
        else { System.out.println("LEXER HATASI: String kapatılmadı"); }
        return TokenType.STRING_LIT;
    }

    // ── CHAR LITERAL ──────────────────────────────────────────────────────────

    private TokenType readCharLiteral() {
        addChar(); // açılış '
        getChar();
        if (charClass != CLS_EOF && nextChar != '\'') {
            addChar(); getChar(); // tek karakter
        }
        if (nextChar == '\'') { addChar(); getChar(); } // kapanış '
        else { System.out.println("LEXER HATASI: Karakter literal kapatılmadı"); }
        return TokenType.CHAR_LIT;
    }

    // ── YARDIMCI METODLAR (Sebesta s.172 yapısı korundu) ─────────────────────

    /** Lexeme'e mevcut karakteri ekler. */
    private void addChar() {
        if (lexeme.length() < 99) {
            lexeme.append(nextChar);
        } else {
            System.out.println("LEXER HATASI: Lexeme çok uzun");
        }
    }

    /**
     * Bir sonraki karakteri okur ve karakter sınıfını belirler.
     * Türkçe harfler LETTER olarak sınıflandırılır.
     */
    private void getChar() {
        try {
            int ch = reader.read();
            if (ch == -1) {
                charClass = CLS_EOF;
                nextChar  = 0;
            } else {
                nextChar = (char) ch;
                if (Character.isLetter(nextChar) || isTurkishLetter(nextChar)) {
                    charClass = CLS_LETTER;
                } else if (Character.isDigit(nextChar)) {
                    charClass = CLS_DIGIT;
                } else {
                    charClass = CLS_UNKNOWN;
                }
            }
        } catch (IOException e) {
            System.out.println("LEXER HATASI - Okuma: " + e.getMessage());
            charClass = CLS_EOF;
        }
    }

    /** Boşluk, tab, yeni satır karakterlerini atlar. */
    private void getNonBlank() {
        while (charClass != CLS_EOF && Character.isWhitespace(nextChar)) {
            getChar();
        }
    }

    /**
     * Türkçe özel harfleri kontrol eder.
     * Büyük ve küçük harf formları dahil.
     */
    private boolean isTurkishLetter(char c) {
        return "çÇğĞıİöÖşŞüÜ".indexOf(c) >= 0;
    }

    // ── HIZLI TEST ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        String testCode =
            "tam sayi = 10 :\n" +
            "tam toplam = sayi + 5 :\n" +
            "eger )sayi > 3( [[\n" +
            "    yzdr \"büyük\" :\n" +
            "]] degilse [[\n" +
            "    yzdr \"küçük\" :\n" +
            "]]\n" +
            "dongu )toplam > 0( [[\n" +
            "    toplam = toplam - 1 :\n" +
            "]]\n";

        System.out.println("=== LEXER TEST ===");
        System.out.println("Kaynak kod:\n" + testCode);
        System.out.println("--- Tokenler ---");

        Lexer lexer = new Lexer(testCode);
        Token tok;
        do {
            tok = lexer.nextToken();
            System.out.println(tok);
        } while (tok.getType() != TokenType.EOF);
    }
}