package Lexer;

import commons.*;
import commons.Error;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Scanner
 * 主要是各自动机的描述
 * @author zhangteng
 *
 */
public class Scanner {
	
	private String msg = "错误的单词:";
	private MyFile file;
	private Buffer buffer;
	private DataType dataType = new DataType();
	private ArrayList<Token> tokenList = new ArrayList<Token>();
	private ArrayList<commons.Error> errorList = new ArrayList<Error>();
	
	public Scanner() { }
	public Scanner(MyFile f) { 
		file = f;
	}
	
	public void setBuffer(Buffer newBuffer) {
		buffer = newBuffer;
	}
	
	public ArrayList<Token> getTokenList() {
		return tokenList;
	}
	
	public ArrayList<Error> getErrorList() {
		return errorList;
	}
	
	public void scan() throws IOException {
		file.openFile();
		buffer = file.getNextBuffer();
		while(buffer != null) {
			while(buffer.getColNum() < buffer.getLength()) {
				getNextToken();
			}
			buffer = file.getNextBuffer();
		}
		file.closeFile();
	}
	
	public void getNextToken() {
		char c = buffer.getNextChar();
		buffer.pushBack();
		if(c>='0' && c<='9') {
			getNumber();
		} else if(c>='a' && c<='z' || c>='A' && c<='Z') {
			getIdentifier();
		} else if(c==' ' || c=='\n' || c=='\t') {
			ignoreChar();
		} else {
			getOperator();
		}
	}
	
	public void getNumber() {
		String word = "";
		char c;
		for(c = buffer.getNextChar();c>='0' && c<='9';c = buffer.getNextChar()) {
			word += c;
		}
		buffer.pushBack();
		if(c>='a'&&c<='z' || c>='A'&&c<='Z') {
			word += getWrongIdentifier();
			errorList.add(new Error(msg + word, buffer.getLineNum(), buffer.getColNum()+1-word.length()));
		} else {
			tokenList.add(new Token(word, dataType.getTokenType(word), dataType.getSemanCode(word),
						buffer.getLineNum(), buffer.getColNum()+1-word.length()));
		}
	}
	
	public void getIdentifier() {
		String word = "";
		char c;
		for(c = buffer.getNextChar();c>='a'&&c<='z' || c>='A'&&c<='Z';c = buffer.getNextChar())
			word += c;
		buffer.pushBack();
		if(c>='0' && c<='9') {
			word += getWrongIdentifier();
			errorList.add(new Error(msg + word, buffer.getLineNum(), buffer.getColNum()+1-word.length()));
		} else {
			if(word != null)
			tokenList.add(new Token(word, dataType.getTokenType(word), dataType.getSemanCode(word),
					buffer.getLineNum(), buffer.getColNum()+1-word.length()));
		}
	}
	
	public void getOperator() {
		String word = "";
		char c = buffer.getNextChar();
		switch(c) {
		case '(': case ')': case '{': case '}': case '[': case ']':
		case '+': case '-': case '*': case ',': case ';': word += c; break;
		case '/': {
			word += c;
			c = buffer.getNextChar();
			if(c == '*') {
				word += c;
				for(c = buffer.getNextChar(); ; c = buffer.getNextChar()) {
					if(buffer.getColNum() == buffer.getLength())
						buffer = file.getNextBuffer();
					if(c=='*' && buffer.getNextChar()=='/')
						break;
				}
			} else {
				buffer.pushBack();
			}
		} break;
		case '!':{
			word += c;
			c = buffer.getNextChar();
			if(c != '=') {
				buffer.pushBack();
				word += getWrongIdentifier();
				errorList.add(new Error(msg + word, buffer.getLineNum(), buffer.getColNum()+1-word.length()));
			} else {
				word += c;
			}
		} break;
		case '=': case '<': case '>': {
			word += c;
			c = buffer.getNextChar();
			if(c != '=')
				buffer.pushBack();
			else 
				word += c;
		} break;
		default: {
			word += c;
			errorList.add(new Error(msg + word, buffer.getLineNum(), buffer.getColNum()+1-word.length()));
			word = "!";
		}
		}
		if(!word.equals("/*") && !word.equals("!")) {
			tokenList.add(new Token(word, dataType.getTokenType(word), dataType.getSemanCode(word),
					buffer.getLineNum(), buffer.getColNum()+1-word.length()));
		}
	}
	
	public String getWrongIdentifier() {
		String word = "";
		char c;
		for(c = buffer.getNextChar();c>='a' && c<='z' || c>='A' && c<='Z' || c>='0' && c<='9';c = buffer.getNextChar())
			word += c;
		return word;
	}
	
	public void ignoreChar() {
		char c;
		while(true) {
			c = buffer.getNextChar();
			if(c==' ' || c=='\n' || c=='\t')
				continue ;
			else {
				buffer.pushBack();
				break;
			}
		}
	}
}
