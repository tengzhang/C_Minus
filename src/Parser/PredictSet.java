package Parser;

import java.util.HashMap;

/**
 * PredictSet
 * 保存所有的predict集
 * @author zhangteng
 *
 */
/**
 * Modified at 2013/4/6
 * @author CuiYuan
 *
 */
public class PredictSet {

	private HashMap<String, Integer> predictSet = new HashMap<String, Integer>();

	public PredictSet() {
		setPredictSet();
	}

	public void setPredictSet() {
        predictSet.put("program int",0);
        predictSet.put("program void",0);
        predictSet.put("program {",0);

        predictSet.put("declaration-list int",1);
        predictSet.put("declaration-list void",1);
        predictSet.put("declaration-list {",1);

        predictSet.put("declaration-list-left int",3);
        predictSet.put("declaration-list-left void",3);
        predictSet.put("declaration-list-left {",3);
        predictSet.put("declaration-list-left EOF",2);

        predictSet.put("declaration int",4);
        predictSet.put("declaration void",4);
        predictSet.put("declaration {",5);

        predictSet.put("type-specifier int",6);
        predictSet.put("type-specifier void",7);

        predictSet.put("declaration-sub ;",8);
        predictSet.put("declaration-sub (",9);
        predictSet.put("declaration-sub [",8);

        predictSet.put("params int",10);
        predictSet.put("params void",11);

        predictSet.put("params-left )",12);
        predictSet.put("params-left IDENTIFIER",13);

        predictSet.put("param-list int",14);
        predictSet.put("param-list void",14);

        predictSet.put("param-list-sub ,",15);
        predictSet.put("param-list-sub )",16);

        predictSet.put("param int",17);
        predictSet.put("param void",17);

        predictSet.put("param-sub ,",18);
        predictSet.put("param-sub )",18);
        predictSet.put("param-sub [",19);

        predictSet.put("compound-stmt {",20);

        predictSet.put("local-declarations if",22);
        predictSet.put("local-declarations int",21);
        predictSet.put("local-declarations return",22);
        predictSet.put("local-declarations void",21);
        predictSet.put("local-declarations while",22);
        predictSet.put("local-declarations ;",22);
        predictSet.put("local-declarations (",22);
        predictSet.put("local-declarations {",22);
        predictSet.put("local-declarations }",22);
        predictSet.put("local-declarations IDENTIFIER",22);
        predictSet.put("local-declarations NUMBER",22);

        predictSet.put("var-declaration int",23);
        predictSet.put("var-declaration void",23);

        predictSet.put("var-declaration-sub ;",24);
        predictSet.put("var-declaration-sub [",25);

        predictSet.put("statement-list if",26);
        predictSet.put("statement-list return",26);
        predictSet.put("statement-list while",26);
        predictSet.put("statement-list ;",26);
        predictSet.put("statement-list (",26);
        predictSet.put("statement-list {",26);
        predictSet.put("statement-list }",27);
        predictSet.put("statement-list IDENTIFIER",26);
        predictSet.put("statement-list NUMBER",26);

        predictSet.put("statement if",30);
        predictSet.put("statement return",32);
        predictSet.put("statement while",31);
        predictSet.put("statement ;",28);
        predictSet.put("statement (",28);
        predictSet.put("statement {",29);
        predictSet.put("statement IDENTIFIER",28);
        predictSet.put("statement NUMBER",28);

        predictSet.put("expression-stmt ;",34);
        predictSet.put("expression-stmt (",33);
        predictSet.put("expression-stmt IDENTIFIER",33);
        predictSet.put("expression-stmt NUMBER",33);

        predictSet.put("expression (",36);
        predictSet.put("expression IDENTIFIER",35);
        predictSet.put("expression NUMBER",37);

        predictSet.put("expression-none +",38);
        predictSet.put("expression-none -",38);
        predictSet.put("expression-none *",38);
        predictSet.put("expression-none /",38);
        predictSet.put("expression-none <",38);
        predictSet.put("expression-none <=",38);
        predictSet.put("expression-none >",38);
        predictSet.put("expression-none >=",38);
        predictSet.put("expression-none ==",38);
        predictSet.put("expression-none !=",38);
        predictSet.put("expression-none ;",38);
        predictSet.put("expression-none ,",38);
        predictSet.put("expression-none )",38);
        predictSet.put("expression-none ]",38);

        predictSet.put("expression-sub +",39);
        predictSet.put("expression-sub -",39);
        predictSet.put("expression-sub *",39);
        predictSet.put("expression-sub /",39);
        predictSet.put("expression-sub <",39);
        predictSet.put("expression-sub <=",39);
        predictSet.put("expression-sub >",39);
        predictSet.put("expression-sub >=",39);
        predictSet.put("expression-sub ==",39);
        predictSet.put("expression-sub !=",39);
        predictSet.put("expression-sub =",39);
        predictSet.put("expression-sub ;",39);
        predictSet.put("expression-sub ,",39);
        predictSet.put("expression-sub (",40);
        predictSet.put("expression-sub )",39);
        predictSet.put("expression-sub [",39);
        predictSet.put("expression-sub ]",39);

        predictSet.put("expression-sub-sub +",42);
        predictSet.put("expression-sub-sub -",42);
        predictSet.put("expression-sub-sub *",42);
        predictSet.put("expression-sub-sub /",42);
        predictSet.put("expression-sub-sub <",42);
        predictSet.put("expression-sub-sub <=",42);
        predictSet.put("expression-sub-sub >",42);
        predictSet.put("expression-sub-sub >=",42);
        predictSet.put("expression-sub-sub ==",42);
        predictSet.put("expression-sub-sub !=",42);
        predictSet.put("expression-sub-sub =",41);
        predictSet.put("expression-sub-sub ;",42);
        predictSet.put("expression-sub-sub ,",42);
        predictSet.put("expression-sub-sub )",42);
        predictSet.put("expression-sub-sub ]",42);

        predictSet.put("var-sub +",43);
        predictSet.put("var-sub -",43);
        predictSet.put("var-sub *",43);
        predictSet.put("var-sub /",43);
        predictSet.put("var-sub <",43);
        predictSet.put("var-sub <=",43);
        predictSet.put("var-sub >",43);
        predictSet.put("var-sub >=",43);
        predictSet.put("var-sub ==",43);
        predictSet.put("var-sub !=",43);
        predictSet.put("var-sub =",43);
        predictSet.put("var-sub ;",43);
        predictSet.put("var-sub ,",43);
        predictSet.put("var-sub )",43);
        predictSet.put("var-sub [",44);
        predictSet.put("var-sub ]",43);

        predictSet.put("simple-expression-sub <",45);
        predictSet.put("simple-expression-sub <=",45);
        predictSet.put("simple-expression-sub >",45);
        predictSet.put("simple-expression-sub >=",45);
        predictSet.put("simple-expression-sub ==",45);
        predictSet.put("simple-expression-sub !=",45);
        predictSet.put("simple-expression-sub ;",46);
        predictSet.put("simple-expression-sub ,",46);
        predictSet.put("simple-expression-sub )",46);
        predictSet.put("simple-expression-sub ]",46);

        predictSet.put("relop <",48);
        predictSet.put("relop <=",47);
        predictSet.put("relop >",49);
        predictSet.put("relop >=",50);
        predictSet.put("relop ==",51);
        predictSet.put("relop !=",52);

        predictSet.put("addictive-expression (",53);
        predictSet.put("addictive-expression IDENTIFIER",53);
        predictSet.put("addictive-expression NUMBER",53);

        predictSet.put("addictive-expression-sub +",54);
        predictSet.put("addictive-expression-sub -",54);
        predictSet.put("addictive-expression-sub <",55);
        predictSet.put("addictive-expression-sub <=",55);
        predictSet.put("addictive-expression-sub >",55);
        predictSet.put("addictive-expression-sub >=",55);
        predictSet.put("addictive-expression-sub ==",55);
        predictSet.put("addictive-expression-sub !=",55);
        predictSet.put("addictive-expression-sub ;",55);
        predictSet.put("addictive-expression-sub ,",55);
        predictSet.put("addictive-expression-sub )",55);
        predictSet.put("addictive-expression-sub ]",55);

        predictSet.put("addop +",56);
        predictSet.put("addop -",57);

        predictSet.put("term (",58);
        predictSet.put("term IDENTIFIER",58);
        predictSet.put("term NUMBER",58);

        predictSet.put("term-sub +",60);
        predictSet.put("term-sub -",60);
        predictSet.put("term-sub *",59);
        predictSet.put("term-sub /",59);
        predictSet.put("term-sub <",60);
        predictSet.put("term-sub <=",60);
        predictSet.put("term-sub >",60);
        predictSet.put("term-sub >=",60);
        predictSet.put("term-sub ==",60);
        predictSet.put("term-sub !=",60);
        predictSet.put("term-sub ;",60);
        predictSet.put("term-sub ,",60);
        predictSet.put("term-sub )",60);
        predictSet.put("term-sub ]",60);

        predictSet.put("mulop *",61);
        predictSet.put("mulop /",62);

        predictSet.put("factor (",63);
        predictSet.put("factor IDENTIFIER",64);
        predictSet.put("factor NUMBER",65);

        predictSet.put("factor-sub +",67);
        predictSet.put("factor-sub -",67);
        predictSet.put("factor-sub *",67);
        predictSet.put("factor-sub /",67);
        predictSet.put("factor-sub <",67);
        predictSet.put("factor-sub <=",67);
        predictSet.put("factor-sub >",67);
        predictSet.put("factor-sub >=",67);
        predictSet.put("factor-sub ==",67);
        predictSet.put("factor-sub !=",67);
        predictSet.put("factor-sub ;",67);
        predictSet.put("factor-sub ,",67);
        predictSet.put("factor-sub (",66);
        predictSet.put("factor-sub )",67);
        predictSet.put("factor-sub [",67);
        predictSet.put("factor-sub ]",67);

        predictSet.put("selection-stmt if",68);

        predictSet.put("selection-stmt-sub else",70);
        predictSet.put("selection-stmt-sub if",69);
        predictSet.put("selection-stmt-sub return",69);
        predictSet.put("selection-stmt-sub while",69);
        predictSet.put("selection-stmt-sub ;",69);
        predictSet.put("selection-stmt-sub (",69);
        predictSet.put("selection-stmt-sub {",69);
        predictSet.put("selection-stmt-sub }",69);
        predictSet.put("selection-stmt-sub IDENTIFIER",69);
        predictSet.put("selection-stmt-sub NUMBER",69);

        predictSet.put("iteration-stmt while",71);

        predictSet.put("return-stmt return",72);

        predictSet.put("return-stmt-sub ;",73);
        predictSet.put("return-stmt-sub (",74);
        predictSet.put("return-stmt-sub IDENTIFIER",74);
        predictSet.put("return-stmt-sub NUMBER",74);

        predictSet.put("args (",75);
        predictSet.put("args )",76);
        predictSet.put("args IDENTIFIER",75);
        predictSet.put("args NUMBER",75);

        predictSet.put("arg-list (",77);
        predictSet.put("arg-list IDENTIFIER",77);
        predictSet.put("arg-list NUMBER",77);

        predictSet.put("arg-list-left ,",78);
        predictSet.put("arg-list-left )",79);
	}

	public int getProductionId(String nonTerminal, String terminal) {
		if(predictSet.get(nonTerminal + " " + terminal) == null) {
			return -1;
		} else {
			return predictSet.get(nonTerminal + " " + terminal);		
		}
	}
}