package Lexer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * MyFile
 * 对File类进行封装，提供方便词法分析的文件操作
 * @author zhangteng
 *
 */
public class MyFile {
	private File file;
	private RandomAccessFile rFile;
	private int lineNum;
	
	public MyFile(File file) {
		setFile(file);
		lineNum = 1;
	}
	
	public void setFile(File newFile) {
		file = newFile;
	}
	
	public void openFile() throws IOException {
		rFile = new RandomAccessFile(file, "r");
	}
	
	public void closeFile() throws IOException {
		rFile.close();
	}
	public Buffer getNextBuffer() {
		String s = getNextString();
		Buffer buffer;
		if(s != null) {
			buffer = new Buffer(s, lineNum);
		} else {
			buffer = null;
		}
		lineNum ++;
		return buffer;
	}
	
	private String getNextString() {
		String tmp = null;
		try {
			tmp = rFile.readLine();
			if(tmp != null) {
				byte[] b = tmp.getBytes("utf-8");
				tmp = new String(b);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return tmp;
	}
} 