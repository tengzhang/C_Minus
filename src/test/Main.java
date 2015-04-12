package test;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-5-20
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */

public class Main {

    public static void main(String[] args) throws IOException {
       Runtime runtime = Runtime.getRuntime();
       runtime.exec("cmd.exe calc");
   }
}

