import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable.java
 * ──────────────────────────────────────────────────────────────────────────────
 * Programda tanımlanan değişkenleri tutan sembol tablosu.
 *
 * Desteklenen kontroller:
 *   1) Duplicate variable  → aynı isimde iki kez bildirim yapılırsa hata
 *   2) Undefined variable  → bildirilmemiş değişken kullanılırsa hata
 *
 * Tablo girdisi: isim → SymbolEntry (tip, değer)
 */
public class SymbolTable {

    // ── Sembol tablosu girdisi ────────────────────────────────────────────────
    public static class SymbolEntry {
        public final String    name;
        public final TokenType type;   // KW_TAM, KW_ONDLK, KW_CML, KW_KRKTR, KW_NMR
        public       String    value;  // atanan değer (opsiyonel, başta null)

        public SymbolEntry(String name, TokenType type) {
            this.name  = name;
            this.type  = type;
            this.value = null;
        }

        @Override
        public String toString() {
            return String.format("%-15s | %-10s | %s", name, type, value == null ? "<atanmadı>" : value);
        }
    }

    // ── İç tablo ──────────────────────────────────────────────────────────────
    private final Map<String, SymbolEntry> table = new HashMap<>();

    // ── PUBLIC API ────────────────────────────────────────────────────────────

    /**
     * Yeni değişken ekler.
     * Aynı isim zaten varsa DUPLICATE hatası fırlatır.
     *
     * @param name  değişken adı
     * @param type  tip keyword token'i (KW_TAM, KW_ONDLK, ...)
     */
    public void declare(String name, TokenType type) {
        if (table.containsKey(name)) {
            throw new SymbolTableException(
                "SEMBOL TABLOSU HATASI: '" + name + "' değişkeni zaten tanımlanmış (duplicate).");
        }
        table.put(name, new SymbolEntry(name, type));
    }

    /**
     * Değişkene değer atar.
     * Değişken tanımlı değilse UNDEFINED hatası fırlatır.
     *
     * @param name  değişken adı
     * @param value atanacak değer (string olarak)
     */
    public void assign(String name, String value) {
        SymbolEntry entry = lookupOrThrow(name);
        entry.value = value;
    }

    /**
     * Değişkeni arar ve döndürür.
     * Tanımlı değilse UNDEFINED hatası fırlatır.
     */
    public SymbolEntry lookup(String name) {
        return lookupOrThrow(name);
    }

    /**
     * Değişkenin tanımlı olup olmadığını kontrol eder (exception yok).
     */
    public boolean isDeclared(String name) {
        return table.containsKey(name);
    }

    /**
     * Tüm tabloyu yazdırır.
     */
    public void printTable() {
        System.out.println("\n=== SEMBOL TABLOSU ===");
        System.out.printf("%-15s | %-10s | %s%n", "İSİM", "TİP", "DEĞER");
        System.out.println("-".repeat(45));
        if (table.isEmpty()) {
            System.out.println("(tablo boş)");
        } else {
            table.values().forEach(System.out::println);
        }
        System.out.println("=".repeat(45));
    }

    // ── YARDIMCI ──────────────────────────────────────────────────────────────

    private SymbolEntry lookupOrThrow(String name) {
        SymbolEntry entry = table.get(name);
        if (entry == null) {
            throw new SymbolTableException(
                "SEMBOL TABLOSU HATASI: '" + name + "' değişkeni tanımlanmamış (undefined).");
        }
        return entry;
    }

    // ── ÖZEL EXCEPTION ───────────────────────────────────────────────────────

    public static class SymbolTableException extends RuntimeException {
        public SymbolTableException(String message) {
            super(message);
        }
    }

    // ── HIZLI TEST ────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SymbolTable st = new SymbolTable();

        System.out.println("== Değişken bildirimi ==");
        st.declare("sayi",  TokenType.KW_TAM);
        st.declare("mesaj", TokenType.KW_CML);
        st.declare("oran",  TokenType.KW_ONDLK);

        System.out.println("== Değer atama ==");
        st.assign("sayi",  "42");
        st.assign("mesaj", "merhaba");

        st.printTable();

        System.out.println("\n== Duplicate testi ==");
        try {
            st.declare("sayi", TokenType.KW_TAM); // hata bekleniyor
        } catch (SymbolTableException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("\n== Undefined testi ==");
        try {
            st.lookup("bilinmeyen"); // hata bekleniyor
        } catch (SymbolTableException e) {
            System.out.println(e.getMessage());
        }
    }
}