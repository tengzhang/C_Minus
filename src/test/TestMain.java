package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-5-17
 * Time: 下午12:49
 * To change this template use File | Settings | File Templates.
 */

public class TestMain {

    public static void main(String[] args) {
        String[][] s = {
                {"xx", "ye"},
                {"re", "ro", "rw"},
                {"ir", "vr"}
        };
        int col = 0;
        for(int i = 0;i < s.length; ++i) {
            if(col < s[i].length) {
                col = s[i].length;
            }
        }
        int a[][] = new int[s.length][col];

        for(int i = 0;i < a.length; ++i) {
            for(int j = 0;j < col; ++j)
            {
                a[i][j] = j+1;
            }
        }

        List<String> ans = fun(a);
        List<String> res = new ArrayList<String>();   //res是最后得到的字符串集合
        for(int i = 0;i < ans.size(); ++i) {
            String tmp = ans.get(i);
            System.out.println(tmp);
            String ss = "";
            for(int j = 0;j < s.length; ++j) {
                if(tmp.charAt(j)-'0'-1 < s[j].length) {
                    ss += s[j][tmp.charAt(j)-'0'-1];
                }
            }
            if(ss.length() == s[0].length*col)
            res.add(ss);
        }

        for(String ss : res) {
            System.out.println(ss);
        }
    }

    public static List<String> fun(int[][] a) {
        List<String> ans = new ArrayList<String>();
        int it = (int) Math.pow((a[0].length),(a.length)) -1;
        System.out.println("it:----" + it);
        String s;
        while(it >= 0){
            s = "";
            int temp = it;
            for(int m = 0 ; m < a.length ; m++){
                if(temp / a[0].length >= 0) {
                    s += a[m][temp % a[0].length];
                    temp /= a[0].length;
                    System.out.println(temp);
                }
            }
            ans.add(s);
            it--;
        }
        Collections.sort(ans);
        return ans;
    }
}
