import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Parser {

    private static final int LETTER = 0;
    private static final int DIGIT = 1;
    private static final int UNKNOWN = 99;
    private static final int EOF = -1;

    private static final int INT_LIT = 10;
    private static final int IDENT = 11;
    private static final int ASSIGN_OP = 20;
    private static final int ADD_OP = 21;
    private static final int SUB_OP = 22;
    private static final int MULT_OP = 23;
    private static final int DIV_OP = 24;
    private static final int LEFT_PAREN = 25;
    private static final int RIGHT_PAREN = 26;

    private int charClass;
    private final StringBuilder lexeme = new StringBuilder();
    private char nextChar;
    private int nextToken;
    private InputStream inputStream;

    public static void main(String[] args) {
        String inputExpression = "(sum + 47) / total";
        System.out.println("Analiz Edilen İfade: " + inputExpression + "\n");

        Parser parser = new Parser();
        parser.startParsing(inputExpression);
    }

    public void startParsing(String input) {
        inputStream = new ByteArrayInputStream(input.getBytes());

        getChar();
        do {
            lex();
        } while (nextToken != EOF);
    }

    private int lookup(char ch) {
        switch (ch) {
            case '(':
                addChar();
                nextToken = LEFT_PAREN;
                break;
            case ')':
                addChar();
                nextToken = RIGHT_PAREN;
                break;
            case '+':
                addChar();
                nextToken = ADD_OP;
                break;
            case '-':
                addChar();
                nextToken = SUB_OP;
                break;
            case '*':
                addChar();
                nextToken = MULT_OP;
                break;
            case '/':
                addChar();
                nextToken = DIV_OP;
                break;
            default:
                addChar();
                nextToken = EOF;
                break;
        }
        return nextToken;
    }

    private void addChar() {
        if (lexeme.length() <= 98) {
            lexeme.append(nextChar);
        } else {
            System.out.println("Hata - lexeme çok uzun \n");
        }
    }

    private void getChar() {
        try {
            int next = inputStream.read();
            if (next != -1) {
                nextChar = (char) next;
                if (Character.isLetter(nextChar)) {
                    charClass = LETTER;
                } else if (Character.isDigit(nextChar)) {
                    charClass = DIGIT;
                } else {
                    charClass = UNKNOWN;
                }
            } else {
                charClass = EOF;
                nextChar = ' ';
            }
        } catch (IOException e) {
            System.out.println("Okuma Hatası: " + e.getMessage());
        }
    }

    private void getNonBlank() {
        while (Character.isWhitespace(nextChar) && charClass != EOF) {
            getChar();
        }
    }

    public int lex() {
        lexeme.setLength(0);
        getNonBlank();

        switch (charClass) {
            case LETTER:
                addChar();
                getChar();
                while (charClass == LETTER || charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                nextToken = IDENT;
                break;

            case DIGIT:
                addChar();
                getChar();
                while (charClass == DIGIT) {
                    addChar();
                    getChar();
                }
                nextToken = INT_LIT;
                break;

            case UNKNOWN:
                lookup(nextChar);
                getChar();
                break;

            case EOF:
                nextToken = EOF;
                lexeme.append("EOF");
                break;
        }

        System.out.printf("Sıradaki Token: %d, Sıradaki Lexeme: %s%n", nextToken, lexeme.toString());
        return nextToken;
    }
}
