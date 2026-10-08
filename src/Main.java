import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;


public class Main {

    private static final String[] TEST_FILES = {
            "tests/ArithmeticExample.txt",
            "tests/ConditionalExample.txt",
            "tests/LoopExample.txt",
            "tests/VariableDeclarationExample.txt",
            "tests/InvalidArithmeticExample.txt",
            "tests/InvalidConditionalExample.txt",
            "tests/InvalidLoopExample.txt",
            "tests/InvalidVariableDeclarationExample.txt"

    };

    public static void main(String[] args) {

        printBanner();

        Scanner scanner = new Scanner(System.in);

        while (true) {

            printSection("TEST DOSYASI SEÇİMİ");

            System.out.println("Lütfen çalıştırmak istediğiniz test dosyasını seçin:\n");

            for (int i = 0; i < TEST_FILES.length; i++) {

                String fileName =
                        Paths.get(TEST_FILES[i]).getFileName().toString();

                System.out.printf("%d - %s%n", i + 1, fileName);
            }

            System.out.println("0 - Programdan Çık");

            System.out.print("\nSeçiminiz: ");

            int choice;

            try {

                choice = Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("\n❌ Geçersiz giriş! Lütfen sayı girin.\n");
                continue;
            }

            if (choice == 0) {

                System.out.println("\nProgram kapatılıyor...");
                break;
            }

            if (choice < 1 || choice > TEST_FILES.length) {

                System.out.println("\n❌ Geçersiz seçim!\n");
                continue;
            }

            String filePath = TEST_FILES[choice - 1];

            System.out.println("\n📂 Kaynak dosya: " + filePath);

            // ── Kaynak Kodu Oku ──────────────────────────────────────────────

            String source;

            try {

                try (var inputStream =
                             Main.class.getClassLoader().getResourceAsStream(filePath)) {

                    if (inputStream == null) {
                        throw new IOException("Dosya bulunamadı.");
                    }

                    source = new String(
                            inputStream.readAllBytes(),
                            StandardCharsets.UTF_8
                    );
                }

            } catch (IOException e) {

                System.err.println("❌ HATA: Dosya okunamadı: " + filePath);
                System.err.println("   → " + e.getMessage());

                continue;
            }

            printSection("KAYNAK KOD");

            System.out.println(source);

            // ── Sembol Tablosu Oluştur ───────────────────────────────────────

            SymbolTable symbolTable = new SymbolTable();

            // ── Lexical Analysis ─────────────────────────────────────────────

            printSection("SÖZCÜKSEL ANALİZ (LEXICAL ANALYSIS)");

            System.out.println(
                    "Lexer başlatılıyor... [Sebesta Ch.4 lex() temelli]"
            );

            List<Token> tokens;

            try {

                Lexer lexer = new Lexer(source, symbolTable);

                tokens = lexer.tokenize();

            } catch (Lexer.LexerException e) {

                System.err.println(
                        "\n❌ SÖZCÜKSEL HATA: " + e.getMessage()
                );

                continue;
            }

            // Token listesini yazdır

            System.out.println("\nToken Listesi:");

            System.out.println(
                    "┌──────────────────────────────────────────────────────┐"
            );

            for (int i = 0; i < tokens.size(); i++) {

                System.out.printf(
                        "│  %3d. %s%n",
                        i + 1,
                        tokens.get(i)
                );
            }

            System.out.println(
                    "└──────────────────────────────────────────────────────┘"
            );

            System.out.printf(
                    "Toplam %d token üretildi.%n",
                    tokens.size()
            );

            // ── Sembol Tablosunu Yazdır ─────────────────────────────────────

            printSection("SEMBOL TABLOSU (SYMBOL TABLE)");

            symbolTable.printTable();

            // ── Syntax Analysis ──────────────────────────────────────────────

            printSection("SÖZDİZİMİ ANALİZİ (SYNTAX ANALYSIS)");

            System.out.println(
                    "Parser başlatılıyor... [Sebesta Ch.4 Recursive-Descent temelli]"
            );

            try {

                Parser parser = new Parser(tokens);

                parser.parse();

            } catch (Parser.ParseException e) {

                System.err.println(
                        "\n❌ SÖZDIZIMI HATASI: " + e.getMessage()
                );

                continue;
            }

            // ── Özet ────────────────────────────────────────────────────────

            printSection("ÖZET");

            System.out.println(
                    "✔ Sözcüksel Analiz : BAŞARILI (" +
                            tokens.size() +
                            " token)"
            );

            System.out.println(
                    "✔ Sözdizimi Analizi: BAŞARILI"
            );

            System.out.println(
                    "✔ Sembol Tablosu   : " +
                            symbolTable.getIdentifiers().size() +
                            " tanımlayıcı kayıtlı"
            );

            System.out.println(
                    "\n──────────────────────────────────────────────────────"
            );

            System.out.println(
                    "Yeni bir test çalıştırabilirsiniz."
            );

            System.out.println(
                    "──────────────────────────────────────────────────────\n"
            );
        }

        scanner.close();
    }

    private static void printBanner() {

        System.out.println("      TürkDil — Türkçe İlhamlı Programlama Dili       ");
        System.out.println(" Sözcüksel Çözümleyici & Özyinelemeli İniş Ayrıştırıcı ");
        System.out.println(" Kaynak: Sebesta, Concepts of PLs, 10th Ed., Ch. 4    ");

        System.out.println();
    }

    private static void printSection(String title) {

        System.out.println();
        System.out.println("======================================================");
        System.out.println("  " + title);
        System.out.println("======================================================");
    }
}
