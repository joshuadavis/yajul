package org.yajul.micro;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yajul.micro.annotations.Component;

import java.lang.reflect.Method;

/**
 * Test component
 * <br>
 * User: josh
 * Date: Jan 28, 2009
 * Time: 5:50:30 PM
 */
@Component(key="TimeMachine")
public class Delorian implements TimeMachine, MethodWrapper {
    private static final Logger log = LoggerFactory.getLogger(Delorian.class);

    private FluxCapacitor capacitor;
    private int before;
    private int after;
    private int excep;
    private int magic;

    @Inject
    public Delorian(FluxCapacitor capacitor) {
        this.capacitor = capacitor;
    }

    public int getDestinationYear() {
        return 1982 + capacitor.getFuzzFactor();
    }

    public void beforeMethod(Method method) {
        before++;
        if (log.isDebugEnabled())
           log.debug("beforeMethod(" + method + ") : " + before);
    }

    public void afterMethod(Method method, Object returnValue) {
        after++;
        if (log.isDebugEnabled())
           log.debug("afterMethod(" + method + ") : " + after);
    }

    public void onException(Method method, Throwable t) {
        excep++;
        if (log.isDebugEnabled())
           log.debug("afterException() : " + excep);
    }

    public int getBefore() {
        return before;
    }

    public int getAfter() {
        return after;
    }

    public int getExcep() {
        return excep;
    }
}
