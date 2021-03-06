package Parser;

/**
 * Terminal
 * 保存所有的终极符
 * @author zhangteng
 *
 */
public class Terminal {
	
	public String[] terminal = {
			"else", "if", "int", "return", "void", "while",
			"+", "-", "*", "/", "<", "<=", ">", ">=", "==", "!=", "=",
			";", ",", "(", ")", "[", "]", "{", "}",
			"IDENTIFIER", "NUMBER", "empty" , "EOF"
	};
	
	public int getTerminalId(String s) {
		int ix = -1;
		for(int i = 0;i < 29; ++i) {
			if(terminal[i].equals(s)) {
				ix = i;
				break;
			}
		}
		return ix;
	}
	
}
