package Lexer;

import commons.Error;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Lexer
 * 定义了此法分析的主要操作
 * @author zhangteng
 *
 */
public class Lexer {
	private Scanner scanner;
	MyFile file;
	public ArrayList<Token> tokenList = new ArrayList<Token>();
	public ArrayList<commons.Error> errorList = new ArrayList<Error>();
	
	public Lexer(File f) {
		file = new MyFile(f);
	}
	
	public void execute() throws IOException {
		scanner = new Scanner(file);
		scanner.scan();
		tokenList = scanner.getTokenList();
		errorList = scanner.getErrorList();
		if(errorList.size() != 0) {
			System.out.println("There are some errors:");
			Error error;
			for(int i = 0;i < errorList.size(); ++i) {
				error = errorList.get(i);
				System.out.println("Line:" + error.getLineNum() + ",Col:" + error.getColNum() + ":" + error.getMsg());
			}
		}

        for(Token token : tokenList) {
            System.out.println("Line: " + token.getLineNum() + ", Col: " + token.getColNum() + " word: " + token.getWord() + ", " + token.getTokenType());
        }
	}
}
