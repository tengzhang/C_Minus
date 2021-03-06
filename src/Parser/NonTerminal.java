package Parser;

/**
 * NonTerminal
 * 保存所有的非终极符
 * @author zhangteng
 *
 */
/**
 * Modified at 2013/4/6
 * @author CuiYuan
 *
 */
public class NonTerminal {

	public String[] nonTerminal = {
			"program",
			"declaration-list",
			"declaration-list-left",
			"declaration",
			"type-specifier",
			"declaration-sub",
			"params",
            "params-left",
			"param-list",
			"param-list-sub",
			"param",
			"param-sub",
			"compound-stmt",
			"local-declarations",
			"var-declaration",
			"var-declaration-sub",
			"statement-list",
			"statement",
			"expression-stmt",
			"expression",
			"expression-none",
			"expression-sub",
			"expression-sub-sub",
			"var-sub",
			"simple-expression-sub",
			"relop",
			"addictive-expression",
			"addictive-expression-sub",
			"addop",
			"term",
			"term-sub",
			"mulop",
			"factor",
			"factor-sub",
			"selection-stmt",
			"selection-stmt-sub",
			"iteration-stmt",
			"return-stmt",
			"return-stmt-sub",
			"args",
			"arg-list",
			"arg-list-left"
	};
	
	public int getNonTerminalId(String s) {
		int ix = -1;
		for(int i = 0;i < 42; ++i) {
			if(nonTerminal[i].equals(s)) {
				ix = i;
				break;
			}
		}
		return ix;
	}
}
