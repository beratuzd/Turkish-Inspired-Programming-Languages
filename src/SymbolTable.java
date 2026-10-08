import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class SymbolTable {

    // ─── Tablolar / Tables ────────────────────────────────────────────────────

    private final Map<String, TokenType> keywordTable;

    private final Map<String, TokenType> identifierTable;

    // ─── Yapılandırıcı / Constructor ──────────────────────────────────────────

    public SymbolTable() {
        keywordTable     = new LinkedHashMap<>();
        identifierTable  = new LinkedHashMap<>();
        loadKeywords();
    }

    private void loadKeywords() {
        keywordTable.put("degisken", TokenType.DEGISKEN);
        keywordTable.put("eger",     TokenType.EGER);
        keywordTable.put("yoksa",    TokenType.YOKSA);
        keywordTable.put("dongu",    TokenType.DONGU);
        keywordTable.put("icin",     TokenType.ICIN);
        keywordTable.put("yazdir",   TokenType.YAZDIR);
        keywordTable.put("dogru",    TokenType.DOGRU);
        keywordTable.put("yanlis",   TokenType.YANLIS);
        keywordTable.put("ve",       TokenType.VE);
        keywordTable.put("veya",     TokenType.VEYA);
        keywordTable.put("degil",    TokenType.DEGIL);
    }

    // ─── Temel İşlemler / Core Operations ────────────────────────────────────

    public TokenType lookup(String name) {
        // Anahtar sözcük tablosunu önce kontrol et
        if (keywordTable.containsKey(name)) {
            return keywordTable.get(name);
        }
        // Sonra tanımlayıcı tablosunu kontrol et
        if (identifierTable.containsKey(name)) {
            return identifierTable.get(name);
        }
        return null; // tabloda yok
    }


    public boolean install(String name) {
        if (keywordTable.containsKey(name)) {
            return false; // keyword'ü identifier olarak ekleyemezsin
        }
        identifierTable.put(name, TokenType.IDENTIFIER);
        return true;
    }


    public boolean isKeyword(String name) {
        return keywordTable.containsKey(name);
    }


    public boolean isIdentifier(String name) {
        return identifierTable.containsKey(name);
    }

    // ─── Tablo İçeriği / Table Content ───────────────────────────────────────


    public Set<String> getKeywords() {
        return keywordTable.keySet();
    }


    public Set<String> getIdentifiers() {
        return identifierTable.keySet();
    }


    public void printTable() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         SEMBOL TABLOSU / SYMBOL TABLE    ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  [ANAHTAR SÖZCÜKLER / KEYWORDS]          ║");
        System.out.println("╠══════════════════════════════════════════╣");
        for (Map.Entry<String, TokenType> entry : keywordTable.entrySet()) {
            System.out.printf("║  %-20s → %-16s ║%n", entry.getKey(), entry.getValue());
        }
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  [TANIMLAYICILAR / IDENTIFIERS]          ║");
        System.out.println("╠══════════════════════════════════════════╣");
        if (identifierTable.isEmpty()) {
            System.out.println("║  (henüz tanımlayıcı bulunamadı)          ║");
        } else {
            for (Map.Entry<String, TokenType> entry : identifierTable.entrySet()) {
                System.out.printf("║  %-20s → %-16s ║%n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("╚══════════════════════════════════════════╝\n");
    }
}
