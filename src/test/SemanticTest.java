package test;

import Lexer.Lexer;
import commons.Error;
import Parser.*;
import Semantic.Semantic;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-5-27
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */

public class SemanticTest {
    public static void main(String[] args) throws IOException {
        File file = new File("E:/in.txt");
        Lexer lexer = new Lexer(file);
        lexer.execute();
        Parser parser = new Parser(lexer.tokenList);
        parser.execute();
        ParserTree parserTree = parser.getParserTree();
        //parserTree.output();
        List<Error> errorList = parser.getErrorList();
        if(!errorList.isEmpty()) {
            for(Error error : errorList) {
                System.out.println(error.getLineNum() + ":" + error.getMsg());
            }
        }
        Semantic semantic = new Semantic();
        semantic.init(parser.getParserTree());
        semantic.scanner();
        if(!semantic.errorList.isEmpty()) {
            for(Error error : semantic.errorList) {
                System.out.println(error.getLineNum() + ":" + error.getMsg());
            }
        }
        semantic.print();
    }
}
