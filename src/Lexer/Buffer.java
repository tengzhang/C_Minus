package Lexer;

/**
 * Buffer类
 * 用以存储从文件中读出的一行字符串，并给scanner提供字符
 * @author zhangteng
 *
 */
public class Buffer {
	private String buffer;
	private int lineNum, colNum;
	private int length;
	
	public Buffer() { }
	public Buffer(String buffer,int lineNum) {
		setBuffer(buffer);
		setLineNum(lineNum);
		setColNum(0);
		length = buffer.length();
	}
	
	public void setBuffer(String newBuffer) {
		buffer = newBuffer;
	}
	public String getBuffer() {
		return buffer;
	}
	
	public void setLineNum(int newLineNum) {
		lineNum = newLineNum;
	}
	public int getLineNum() {
		return lineNum;
	}
	
	public void setColNum(int newColNum) {
		colNum = newColNum;
	}
	public int getColNum() {
		return colNum;
	}
	
	public int getLength() {
		return length;
	}
	
	public char getNextChar() {
		int i = getColNum();
		setColNum(i + 1);
		if(i < buffer.length()) {
			return buffer.charAt(i);
		} else {
			return ' ';
		}
	}
	
	public void pushBack() {
		setColNum(getColNum()-1);
	}
	
}
