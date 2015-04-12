package Lexer;

import java.io.*;

public class Main {
	public static void main(String[] args) throws IOException {
		File file = new File("E:\\workspace\\idea\\C_Minus/in.txt");
		Lexer lexer = new Lexer(file);
		lexer.execute();
	}
}
