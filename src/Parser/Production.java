package Parser;

/**
 * Production
 * 保存所有的产生式
 * @author zhangteng
 *
 */
/**
 * Modified at 2013/4/6
 * @author CuiYuan
 *
 */
public class Production {
	String[][] production = {
			{"program","declaration-list","EOF","-1"},
			{"declaration-list","declaration","declaration-list-left","-1"},
			{"declaration-list-left","empty","-1"},
			{"declaration-list-left","declaration","declaration-list-left","-1"},
			{"declaration","type-specifier","IDENTIFIER","declaration-sub","-1"},
			{"declaration","compound-stmt","-1"},
			{"type-specifier","int","-1"},
			{"type-specifier","void","-1"},
			{"declaration-sub","var-declaration-sub","-1"},
			{"declaration-sub","(","params",")","-1"},
            {"params","int","IDENTIFIER","param-sub","param-list-sub","-1"},
            {"params","void","params-left","-1"},
            {"params-left","empty","-1"},
            {"params-left","IDENTIFIER","param-sub","param-list-sub","-1"},
			{"param-list","param","param-list-sub","-1"},
			{"param-list-sub",",","param","param-list-sub","-1"},
			{"param-list-sub","empty","-1"},
			{"param","type-specifier","IDENTIFIER","param-sub","-1"},
			{"param-sub","empty","-1"},
			{"param-sub","[","]","-1"},
			{"compound-stmt","{","local-declarations","statement-list","}","-1"},
			{"local-declarations","var-declaration","local-declarations","-1"},
			{"local-declarations","empty","-1"},
			{"var-declaration","type-specifier","IDENTIFIER","var-declaration-sub","-1"},
			{"var-declaration-sub",";","-1"},
			{"var-declaration-sub","[","NUMBER","]",";","-1"},
			{"statement-list","statement","statement-list","-1"},
			{"statement-list","empty","-1"},
			{"statement","expression-stmt","-1"},
			{"statement","compound-stmt","-1"},
			{"statement","selection-stmt","-1"},
			{"statement","iteration-stmt","-1"},
			{"statement","return-stmt","-1"},
			{"expression-stmt","expression",";","-1"},
			{"expression-stmt",";","-1"},
			{"expression","IDENTIFIER","expression-sub","-1"},
			{"expression","(","expression",")","expression-none","-1"},
			{"expression","NUMBER","expression-none","-1"},
			{"expression-none","term-sub","addictive-expression-sub","simple-expression-sub","-1"},
			{"expression-sub","var-sub","expression-sub-sub","-1"},
			{"expression-sub","(","args",")","expression-none","-1"},
			{"expression-sub-sub","=","expression","-1"},
			{"expression-sub-sub","expression-none","-1"},
			{"var-sub","empty","-1"},
			{"var-sub","[","expression","]","-1"},
			{"simple-expression-sub","relop","addictive-expression","-1"},
			{"simple-expression-sub","empty","-1"},
			{"relop","<=","-1"},
			{"relop","<","-1"},
			{"relop",">","-1"},
			{"relop",">=","-1"},
			{"relop","==","-1"},
			{"relop","!=","-1"},
			{"addictive-expression","term","addictive-expression-sub","-1"},
			{"addictive-expression-sub","addop","term","addictive-expression-sub","-1"},
			{"addictive-expression-sub","empty","-1"},
			{"addop","+","-1"},
			{"addop","-","-1"},
			{"term","factor","term-sub","-1"},
			{"term-sub","mulop","factor","term-sub","-1"},
			{"term-sub","empty","-1"},
			{"mulop","*","-1"},
			{"mulop","/","-1"},
			{"factor","(","expression",")","-1"},
			{"factor","IDENTIFIER","factor-sub","-1"},
			{"factor","NUMBER","-1"},
			{"factor-sub","(args)","-1"},
			{"factor-sub","var-sub","-1"},
			{"selection-stmt","if","(","expression",")","statement","selection-stmt-sub","-1"},
			{"selection-stmt-sub","empty","-1"},
			{"selection-stmt-sub","else","statement","-1"},
			{"iteration-stmt","while","(","expression",")","statement","-1"},
			{"return-stmt","return","return-stmt-sub","-1"},
			{"return-stmt-sub",";","-1"},
			{"return-stmt-sub","expression",";","-1"},
			{"args","arg-list","-1"},
			{"args","empty","-1"},
			{"arg-list","expression","arg-list-left","-1"},
			{"arg-list-left",",","expression","arg-list-left","-1"},
			{"arg-list-left","empty","-1"}
	};

	public String[] getProduction(int id) {
		return production[id];
	}
}
