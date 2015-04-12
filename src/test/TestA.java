package test;

import commons.Int;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-5-17
 * Time: 下午12:48
 * To change this template use File | Settings | File Templates.
 */

public class TestA {

    public Int a;

    public TestA() {
        a = new Int(10);
    }

    public void assign(TestA A) {
        this.a = A.a;
    }
}
