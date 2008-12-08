package org.yajul.micro;

import org.picocontainer.Startable;

import java.util.Set;
import java.util.TreeSet;

/**
 * Container configuring component.
 * <br>
 * User: josh
 * Date: Mar 11, 2008
 * Time: 10:50:51 PM
 */
public class TestConfig implements Configuration, Startable {
    private boolean started;

    public void addComponents(MicroContainer microContainer) {
        microContainer.addComponent(Set.class, TreeSet.class);
        microContainer.addComponent("magicNumber",42L);
    }

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }
}
