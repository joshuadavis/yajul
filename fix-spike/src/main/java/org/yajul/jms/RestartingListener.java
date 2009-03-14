package org.yajul.jms;

import org.yajul.util.Lifecycle;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * A JMS exception listener that restarts the lifecycle component on any exception.
 * <br>User: Josh
 * Date: Mar 3, 2009
 * Time: 6:08:03 AM
 */
public class RestartingListener implements ExceptionListener {
    private final static Logger log = LoggerFactory.getLogger(RestartingListener.class);

    private Lifecycle lifecycle;

    public RestartingListener(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void onException(JMSException e) {
        log.error("Restarting due to exception: " + e,e);
        lifecycle.stop();
        try {
            lifecycle.start();
        } catch (Exception startException) {
            log.error("*** UNABLE TO START " + lifecycle + " DUE TO: " + e + " ***",e);
        }
    }
}
