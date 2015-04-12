package commons;

import java.util.HashMap;

/**
 * DataType
 * 定义了此法分析中主要用到的常量，TokenType、SemanCode，并提供根据单词查找TokenType、SemanCode
 * @author zhangteng
 *
 */
public class DataType {
    /* 词法分析中用到 */
	public enum TokenType {
		IDENTIFIER,      // 标识符
		KEYWORD,         // 关键字
		NUMBER,          // 数字
		OPERATOR,        // 操作符
		FORMAT,          // 格式符
	}
	
	public enum SemanCode {
		IF, ELSE, INT, RETURN, VOID, WHILE, EOF,    // KEYWORD
		ADD, MINUS, MULITIPLY, DIVISION,            // + - * /
		L, LE, G, GE, EE, UE, ASS,                  // < <= > >= == != =
		COMMA, SEMI, LBRACE, RBRACE,                // , ; { }
		LBRAC, RBRAC, LSBRAC, RSBRAC                // ( ) [ ]
	}

    private HashMap<String, TokenType> tokenTypeMap = new HashMap<String, TokenType>();
    private HashMap<String, SemanCode> semanCodeMap = new HashMap<String, SemanCode>();
    /* 词法分析中用到 */

    /* 语义分析中用到 */
    public enum TypeKind {
        intTy, voidTy, nullTy
    };

    public enum IdKind {
        varKind, arrayKind, procKind, consKind
    };

    public static String[] tyStr = {"int", "void", "null"};
    public static String[] kindStr = {"var", "array", "proc"};

    public static final int INTSIZE = 2;
    public static final int VOIDSIZE = 0;
    /* 语义分析中用到 */

	public void initTokenTypeMap() {
		tokenTypeMap.put(",", TokenType.FORMAT);
		tokenTypeMap.put(";", TokenType.FORMAT);
		tokenTypeMap.put("{", TokenType.FORMAT);
		tokenTypeMap.put("}", TokenType.FORMAT);
		tokenTypeMap.put("+", TokenType.OPERATOR);
		tokenTypeMap.put("-", TokenType.OPERATOR);
		tokenTypeMap.put("*", TokenType.OPERATOR);
		tokenTypeMap.put("/", TokenType.OPERATOR);
		tokenTypeMap.put("<", TokenType.OPERATOR);
		tokenTypeMap.put("<=", TokenType.OPERATOR);
		tokenTypeMap.put(">", TokenType.OPERATOR);
		tokenTypeMap.put(">=", TokenType.OPERATOR);
		tokenTypeMap.put("==", TokenType.OPERATOR);
		tokenTypeMap.put("!=", TokenType.OPERATOR);
		tokenTypeMap.put("=", TokenType.OPERATOR);
		tokenTypeMap.put("[", TokenType.OPERATOR);
		tokenTypeMap.put("]", TokenType.OPERATOR);
		tokenTypeMap.put("(", TokenType.OPERATOR);
		tokenTypeMap.put(")", TokenType.OPERATOR);
		tokenTypeMap.put("if", TokenType.KEYWORD);
		tokenTypeMap.put("else", TokenType.KEYWORD);
		tokenTypeMap.put("int", TokenType.KEYWORD);
		tokenTypeMap.put("return", TokenType.KEYWORD);
		tokenTypeMap.put("while", TokenType.KEYWORD);
		tokenTypeMap.put("void", TokenType.KEYWORD);
		tokenTypeMap.put("EOF", TokenType.KEYWORD);
	}
	public TokenType getTokenType(String word) {
		if(tokenTypeMap.size() == 0) {
			initTokenTypeMap();
		}
		TokenType type = tokenTypeMap.get(word);
		if(type == null) {
            boolean isNum = true;
            for(int i = 0;i < word.length(); ++i) {
                if(!(word.charAt(i)>='0' && word.charAt(i)<='9')) {
                    isNum = false;
                    break;
                }
            }
            if(isNum) {
                type = TokenType.NUMBER;
            } else {
			    type = TokenType.IDENTIFIER;
            }
		}
		return type;
	}
	
	public void initSemanCodeMap() {
		semanCodeMap.put(",", SemanCode.COMMA);
		semanCodeMap.put(";", SemanCode.SEMI);
		semanCodeMap.put("{", SemanCode.LBRACE);
		semanCodeMap.put("}", SemanCode.RBRACE);
		semanCodeMap.put("+", SemanCode.ADD);
		semanCodeMap.put("-", SemanCode.MINUS);
		semanCodeMap.put("*", SemanCode.MULITIPLY);
		semanCodeMap.put("/", SemanCode.DIVISION);
		semanCodeMap.put("<", SemanCode.L);
		semanCodeMap.put("<=", SemanCode.LE);
		semanCodeMap.put(">", SemanCode.G);
		semanCodeMap.put(">=", SemanCode.GE);
		semanCodeMap.put("==", SemanCode.EE);
		semanCodeMap.put("!=", SemanCode.UE);
		semanCodeMap.put("=", SemanCode.ASS);
		semanCodeMap.put("[", SemanCode.LSBRAC);
		semanCodeMap.put("]", SemanCode.RSBRAC);
		semanCodeMap.put("(", SemanCode.LBRAC);
		semanCodeMap.put(")", SemanCode.RBRAC);
		semanCodeMap.put("if", SemanCode.IF);
		semanCodeMap.put("else", SemanCode.ELSE);
		semanCodeMap.put("int", SemanCode.INT);
		semanCodeMap.put("return", SemanCode.RETURN);
		semanCodeMap.put("void", SemanCode.VOID);
		semanCodeMap.put("EOF", SemanCode.EOF);
	}
	public SemanCode getSemanCode(String word) {
		if(semanCodeMap.size() == 0) {
			initSemanCodeMap();
		}
		return semanCodeMap.get(word);
	}
}
