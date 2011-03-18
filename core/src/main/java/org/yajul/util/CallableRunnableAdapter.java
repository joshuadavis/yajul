package org.yajul.util;

import java.util.concurrent.Callable;

/**
* Use this when you have a Runnable, but you need a Callable.
* <br>
* User: josh
* Date: Jan 15, 2010
* Time: 1:55:38 PM
*/
public class CallableRunnableAdapter implements Callable<Object>
{
    private final Runnable r;

    public CallableRunnableAdapter(Runnable r)
    {
        this.r = r;
    }

    public Object call() throws Exception
    {
        r.run();
        return null;
    }
}
