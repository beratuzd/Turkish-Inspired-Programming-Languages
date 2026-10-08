import java.util.List;

public class Parser {

    public static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }

    private final List<Token> tokens;

    private int current;

    public Parser(List<Token> tokens) {
        this.tokens  = tokens;
        this.current = 0;
    }

    private Token peek() {
        return tokens.get(current);
    }


    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean check(TokenType type) {
        return peek().getType() == type;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }


    private Token consume(TokenType type, String context) {
        if (check(type)) {
            return advance();
        }
        Token found = peek();
        throw new ParseException(
            String.format("[SÖZDIZIMI HATASI] Satir %d: %s beklendi, ancak '%s' (%s) bulundu.",
                          found.getLine(), context, found.getValue(), found.getType())
        );
    }

    private boolean isAtEnd() {
        return peek().getType() == TokenType.EOF;
    }

    public void parse() {
        System.out.println("║         SÖZDİZİMİ ANALİZİ BAŞLADI              ║");
        program();
        System.out.println("║  ✔ BAŞARILI: Program sözdizimi geçerlidir.      ║");
    }

    private void program() {
        System.out.println("[program] Ayrıştırma başlıyor...");
        while (!isAtEnd()) {
            statement();
        }
        System.out.println("[program] Tüm deyimler başarıyla ayrıştırıldı.");
    }


    private void statement() {
        TokenType type = peek().getType();

        switch (type) {
            case DEGISKEN:
                declaration();
                break;
            case EGER:
                condition();
                break;
            case DONGU:
                loop();
                break;
            case ICIN:
                forLoop();
                break;
            case YAZDIR:
                print();
                break;
            case IDENTIFIER:
                assignment();
                break;
            default:
                throw new ParseException(
                    String.format("[SÖZDIZIMI HATASI] Satir %d: Beklenmedik token '%s' (%s). " +
                                  "Deyim başlangıcı beklendi.",
                                  peek().getLine(), peek().getValue(), peek().getType())
                );
        }
    }


    private void declaration() {
        consume(TokenType.DEGISKEN, "'degisken' anahtar sozcugu");
        Token idToken = consume(TokenType.IDENTIFIER, "degisken ismi (tanımlayıcı)");
        System.out.println("  [bildirim] Değişken: " + idToken.getValue());

        if (match(TokenType.ASSIGN)) {
            System.out.println("  [bildirim] Başlangıç değeri atanıyor...");
            expr();
        }
        consume(TokenType.SEMICOLON, "';' (noktalı virgül)");
        System.out.println("  [bildirim] ✔ Tamamlandı: " + idToken.getValue());
    }


    private void assignment() {
        Token idToken = consume(TokenType.IDENTIFIER, "tanımlayıcı (identifier)");

        // Artırma/Azaltma: x++; veya x--;
        if (match(TokenType.INCREMENT)) {
            consume(TokenType.SEMICOLON, "';' (noktalı virgül)");
            System.out.println("  [atama] ✔ Artırma: " + idToken.getValue() + "++");
            return;
        }
        if (match(TokenType.DECREMENT)) {
            consume(TokenType.SEMICOLON, "';' (noktalı virgül)");
            System.out.println("  [atama] ✔ Azaltma: " + idToken.getValue() + "--");
            return;
        }

        consume(TokenType.ASSIGN, "'=' (atama operatörü)");
        System.out.println("  [atama] Sağ taraf hesaplanıyor: " + idToken.getValue() + " = ...");
        expr();
        consume(TokenType.SEMICOLON, "';' (noktalı virgül)");
        System.out.println("  [atama] ✔ Tamamlandı: " + idToken.getValue());
    }


    private void assignmentNoSemi() {
        Token idToken = consume(TokenType.IDENTIFIER, "tanımlayıcı (identifier)");
        if (match(TokenType.INCREMENT)) {
            System.out.println("  [for-atama] Artırma: " + idToken.getValue() + "++");
            return;
        }
        if (match(TokenType.DECREMENT)) {
            System.out.println("  [for-atama] Azaltma: " + idToken.getValue() + "--");
            return;
        }
        consume(TokenType.ASSIGN, "'=' (atama operatörü)");
        expr();
        System.out.println("  [for-atama] Atama: " + idToken.getValue());
    }


    private void condition() {
        consume(TokenType.EGER, "'eger' anahtar sozcugu");
        System.out.println("  [koşul] 'eger' bloğu işleniyor...");

        consume(TokenType.LPAREN, "'(' (sol parantez)");
        boolExpr();
        consume(TokenType.RPAREN, "')' (sağ parantez)");

        consume(TokenType.LBRACE, "'{' (sol süslü parantez)");
        System.out.println("  [koşul] 'eger' gövdesi ayrıştırılıyor...");
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statement();
        }
        consume(TokenType.RBRACE, "'}' (sağ süslü parantez)");
        System.out.println("  [koşul] ✔ 'eger' gövdesi tamamlandı.");

        // İsteğe bağlı 'yoksa' (else) bloğu
        if (match(TokenType.YOKSA)) {
            System.out.println("  [koşul] 'yoksa' bloğu işleniyor...");
            consume(TokenType.LBRACE, "'{' (sol süslü parantez)");
            while (!check(TokenType.RBRACE) && !isAtEnd()) {
                statement();
            }
            consume(TokenType.RBRACE, "'}' (sağ süslü parantez)");
            System.out.println("  [koşul] ✔ 'yoksa' gövdesi tamamlandı.");
        }
    }


    private void loop() {
        consume(TokenType.DONGU, "'dongu' anahtar sozcugu");
        System.out.println("  [döngü] 'dongu' (while) döngüsü işleniyor...");

        consume(TokenType.LPAREN, "'(' (sol parantez)");
        boolExpr();
        consume(TokenType.RPAREN, "')' (sağ parantez)");

        consume(TokenType.LBRACE, "'{' (sol süslü parantez)");
        System.out.println("  [döngü] Döngü gövdesi ayrıştırılıyor...");
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statement();
        }
        consume(TokenType.RBRACE, "'}' (sağ süslü parantez)");
        System.out.println("  [döngü] ✔ 'dongu' döngüsü tamamlandı.");
    }


    private void forLoop() {
        consume(TokenType.ICIN, "'icin' anahtar sozcugu");
        System.out.println("  [döngü] 'icin' (for) döngüsü işleniyor...");

        consume(TokenType.LPAREN, "'(' (sol parantez)");

        // Başlangıç deyimi (init)
        System.out.println("  [icin] Başlangıç deyimi ayrıştırılıyor...");
        assignmentNoSemi();
        consume(TokenType.SEMICOLON, "';' (ilk noktalı virgül)");

        // Koşul
        System.out.println("  [icin] Koşul ayrıştırılıyor...");
        boolExpr();
        consume(TokenType.SEMICOLON, "';' (ikinci noktalı virgül)");

        // Güncelleme deyimi (update)
        System.out.println("  [icin] Güncelleme deyimi ayrıştırılıyor...");
        assignmentNoSemi();

        consume(TokenType.RPAREN, "')' (sağ parantez)");

        consume(TokenType.LBRACE, "'{' (sol süslü parantez)");
        System.out.println("  [icin] Döngü gövdesi ayrıştırılıyor...");
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            statement();
        }
        consume(TokenType.RBRACE, "'}' (sağ süslü parantez)");
        System.out.println("  [döngü] ✔ 'icin' döngüsü tamamlandı.");
    }


    private void print() {
        consume(TokenType.YAZDIR, "'yazdir' anahtar sozcugu");
        System.out.println("  [yazdir] Çıktı deyimi işleniyor...");
        consume(TokenType.LPAREN, "'(' (sol parantez)");
        expr();
        consume(TokenType.RPAREN, "')' (sağ parantez)");
        consume(TokenType.SEMICOLON, "';' (noktalı virgül)");
        System.out.println("  [yazdir] ✔ Çıktı deyimi tamamlandı.");
    }


    private void boolExpr() {
        if (match(TokenType.DOGRU)) {
            System.out.println("  [bool] Sabit: dogru (true)");
            return;
        }
        if (match(TokenType.YANLIS)) {
            System.out.println("  [bool] Sabit: yanlis (false)");
            return;
        }

        // <expr> <relOp> <expr>
        expr();

        // Karşılaştırma operatörü bekleniyor
        if (!match(TokenType.EQ, TokenType.NEQ, TokenType.LT,
                   TokenType.GT, TokenType.LTE, TokenType.GTE)) {
            throw new ParseException(
                String.format("[SÖZDIZIMI HATASI] Satir %d: " +
                              "Karşılaştırma operatörü (<, >, <=, >=, ==, !=) beklendi, " +
                              "ancak '%s' (%s) bulundu.",
                              peek().getLine(), peek().getValue(), peek().getType())
            );
        }
        System.out.println("  [bool] Karşılaştırma operatörü: " + previous().getValue());
        expr();
    }

    private void expr() {
        term(); // İlk terimi ayrıştır (Sebesta: call term())

        // Sebesta: while (nextToken == PLUS_CODE || nextToken == MINUS_CODE)
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            String op = previous().getValue();
            System.out.println("    [expr] Operatör: " + op);
            term(); // Sonraki terimi ayrıştır
        }
    }


    private void term() {
        factor(); // İlk faktörü ayrıştır (Sebesta: call factor())

        // Sebesta: while (nextToken == MULT_CODE || nextToken == DIV_CODE)
        while (match(TokenType.MULT, TokenType.DIV)) {
            String op = previous().getValue();
            System.out.println("    [term] Operatör: " + op);
            factor(); // Sonraki faktörü ayrıştır
        }
    }


    private void factor() {
        // Parantezli ifade: '(' expr ')'
        if (match(TokenType.LPAREN)) {
            System.out.println("    [factor] Parantezli ifade başlıyor...");
            expr();
            consume(TokenType.RPAREN, "')' (sağ parantez)");
            System.out.println("    [factor] Parantezli ifade tamamlandı.");
        }
        // Tekli eksi: '-' factor
        else if (match(TokenType.MINUS)) {
            System.out.println("    [factor] Tekli eksi (unary minus)");
            factor();
        }
        // Tam sayı sabiti
        else if (match(TokenType.INT_LITERAL)) {
            System.out.println("    [factor] Tam sayı sabiti: " + previous().getValue());
        }
        // Tanımlayıcı
        else if (match(TokenType.IDENTIFIER)) {
            System.out.println("    [factor] Tanımlayıcı: " + previous().getValue());
        }
        // Hiçbiri değilse → sözdizimi hatası
        else {
            throw new ParseException(
                String.format("[SÖZDIZIMI HATASI] Satir %d: " +
                              "İfade (sayı, tanımlayıcı veya '(') beklendi, " +
                              "ancak '%s' (%s) bulundu.",
                              peek().getLine(), peek().getValue(), peek().getType())
            );
        }
    }
}
