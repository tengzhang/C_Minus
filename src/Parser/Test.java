package Parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import commons.Error;
import Lexer.Lexer;


public class Test {
	public static void main(String[] args) throws IOException {
		File file = new File("E:/in.txt");
		Lexer lexer = new Lexer(file);
		lexer.execute();
		Parser parser = new Parser(lexer.tokenList);
		parser.execute();
		ArrayList<Error> errorList = parser.getErrorList();
		ParserTree parserTree = parser.getParserTree();
		if(errorList.size() != 0) {
			for(Error error : errorList) {
				System.out.println("line:" + error.getLineNum() + error.getMsg());
			}
		}
		parserTree.getTreeWidth(parserTree.getNode(0));
		parserTree.getNode(0).setX(parserTree.getNode(0).getWidth()/2-15);
		parserTree.getNode(0).setY(0);
		parserTree.getNode(0).setXmin(0);
		parserTree.getTreeXY(parserTree.getNode(0), 1);
		//parserTree.outPut();
	}
}
