package org.yajul.micro;

import org.yajul.micro.annotations.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * for testing
* <br>
* User: josh
* Date: Sep 11, 2008
* Time: 7:57:14 AM
*/
@Component
public class TestThing {
    public static AtomicInteger counter = new AtomicInteger();

    public TestThing() {
        counter.incrementAndGet();
    }
}
