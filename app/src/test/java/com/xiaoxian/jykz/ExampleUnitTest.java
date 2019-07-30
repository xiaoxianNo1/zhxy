package com.xiaoxian.jykz;

import com.xiaoxian.jykz.util.okhttputil.HttpUtils;
import com.xiaoxian.jykz.util.okhttputil.OkHttp3Util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {

        assertEquals(4, 2 + 2);
        System.out.println("测试类启动");
        String Url="https://10.url.cn/eth/ajNVdqHZLLAxibwnrOxXSzIxA76ichutwMCcOpA45xjiapneMZsib7eY4wUxF6XDmL2FmZEVYsf86iaw/";
        //OkHttp3Util .download(null,Url,"tzsc");
        HttpUtils.downFile(Url,"/tzsc","test.png");
    }
}