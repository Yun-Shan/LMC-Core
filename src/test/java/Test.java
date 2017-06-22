/*
 * Author: Yun-Shan
 * Date: 2017/06/22
 */

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * //TODO
 */
public class Test {
    
    @org.junit.Test
    public void test() throws Exception {
        System.out.println(Paths.get("").resolve(Paths.get("../w/../b/c/./d").normalize()));
    }
}
