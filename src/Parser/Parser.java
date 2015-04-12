package Parser;

import java.util.ArrayList;
import java.util.Stack;

import commons.DataType;
import commons.Error;
import Lexer.Token;

/**
 * Parser
 * 语法分析的过程，LL（1）文法及驱动程序
 * @author zhangteng
 *
 */
public class Parser {
	
	private ArrayList<Token> tokenList;
	public ArrayList<Error> errorList = new ArrayList<Error>();
	private ParserTree parserTree = new ParserTree();
	private NonTerminal nonTerminal = new NonTerminal();
	private Terminal terminal = new Terminal();
	private Production production = new Production();
	private PredictSet predictSet = new PredictSet();
	private Stack<ParserTreeNode> symbolStack = new Stack<ParserTreeNode>();
	
	public Parser(ArrayList<Token> tokenList) {
		this.tokenList = tokenList;
		
		// 加上一个EOF节点，相当于在token链后加“#”，主要是为了完成LL（1）文法
		tokenList.add(new Token("EOF", null, null, 0, 0));
	}
	
	public void execute() {
		int tokenIndex = 0;
		int tokenSize = tokenList.size();
		if(tokenSize == 0)
			return ;
		ParserTreeNode node = new ParserTreeNode(-1, "", "program", 0, -1);
        node.setId(0);
		node.setId(parserTree.Size());
		parserTree.addNode(node);
		symbolStack.push(node);
		Token token = tokenList.get(tokenIndex);
		while(!symbolStack.empty() && tokenIndex != tokenSize) {
			node = symbolStack.peek();
			token = tokenList.get(tokenIndex);
			String tokenContent = token.getWord();
            System.out.println(token.getTokenType() + " " + token.getWord());
			if(token.getTokenType() == DataType.TokenType.IDENTIFIER) {
				tokenContent = "IDENTIFIER";
			}
			if(token.getTokenType() == DataType.TokenType.NUMBER) {
				tokenContent = "NUMBER";
			}
            System.out.println(node.getType() + " " + tokenContent);
			if(nonTerminal.getNonTerminalId(node.getType()) != -1) {
				int productionId = predictSet.getProductionId(node.getType(), tokenContent);
				if(productionId != -1) {
					symbolStack.pop();
					pushIntoSymbolStack(productionId, node, token);
				} else {
					errorList.add(new Error("语法错误:错误终极符:" + tokenContent, token.getLineNum(), token.getColNum()));
					symbolStack.pop();
					tokenIndex ++;
				}
			} else {
				if(node.getType().equals(tokenContent)) {
					node.setValue(token.getWord());
					parserTree.setNode(node.getId(), node);
					symbolStack.pop();
					tokenIndex ++;
				} else {
					errorList.add(new Error("语法错误:错误token:" + token.getWord(), token.getLineNum(), token.getColNum()));
					symbolStack.pop();
				}
			}
		}
        outPut(parserTree.getNode(0));
	}
	public void pushIntoSymbolStack(int productionId, ParserTreeNode parent, Token token) {
		int len = 1;
		String[] proc = production.getProduction(productionId);
		while(!proc[len].equals("-1")) {
			if(!proc[len].equals("empty")) {
				ParserTreeNode node = new ParserTreeNode(parent.getId(), "", proc[len], token.getLineNum(), productionId);
				node.setId(parserTree.Size());
				parserTree.addNode(node);
				parent.addChild(node);
			}
			len ++;
		}
		int i = parent.getChildNum()-1;
		//ParserTreeNode node = parserTree.getNode(parent.getChildNode(i));
		for( ;i >= 0; --i) {
		    symbolStack.push(parserTree.getNode(parent.getChildNode(i)));
		}
		parserTree.setNode(parent.getId(), parent);
	}
	
	public ParserTree getParserTree() {
		return parserTree;
	}
	
	public ArrayList<Error> getErrorList() {
		return errorList;
	}

    public void outPut(ParserTreeNode node) {
        if(node.getId() == 0) {
            System.out.println(node.getId() + " " + node.getParentId() + " " + node.getType() + " " + node.getValue());
        }
        for(int i = 0;i < node.getChildNum(); ++i) {
            ParserTreeNode tmp = parserTree.getNode(node.getChildNode(i));
            System.out.println(tmp.getId() + " " + tmp.getParentId() + " " + tmp.getType() + " " + tmp.getValue());
            outPut(tmp);
        }
    }
}
