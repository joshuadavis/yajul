package org.yajul.depcheck;

import org.junit.Test;

/**
 * TODO: Add class level comments.
 * <br>
 * User: josh
 * Date: 1/4/13
 * Time: 8:06 AM
 */
public class CoreSEDependencyTest {
    @Test
    public void noCircularDependencies() throws Exception {
        DependencyAnalyzer dependencyAnalyzer = new DependencyAnalyzer("core-se");
        dependencyAnalyzer.init();
//        dependencyAnalyzer.assertNoCircularDependencies();
    }
}
