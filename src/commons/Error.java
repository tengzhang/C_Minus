package commons;
/**
 * Error
 * 错误节点的定义，词法和语法都回用到，设计时要考虑通用
 * @author zhangteng
 *
 */
public class Error {
	String msg;
	int lineNum, colNum;
	
	public Error() { }
	public Error(String msg, int lineNum, int colNum) {
		setMsg(msg);
		setLineNum(lineNum);
		setColNum(colNum);
	}
	
	public void setMsg(String newmsg) {
		msg = newmsg;
	}
	public String getMsg() {
		return msg;
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
}
