package commons;

/**
 * Error
 * int 封装类
 * @author zhangteng
 *
 */

public class Int {
    public int value;

    public Int(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void assign(Int a) {
        setValue(a.getValue());
    }
}
