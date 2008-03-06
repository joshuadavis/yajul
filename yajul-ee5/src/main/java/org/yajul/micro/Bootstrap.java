package org.yajul.micro;

import org.picocontainer.MutablePicoContainer;

/**
 * Implement this in order to bootstrap a MicroContainer instance.
 * <br>User: Joshua Davis
 * Date: Mar 6, 2008
 * Time: 6:43:47 AM
 */
public interface Bootstrap {
    void addComponentsTo(MutablePicoContainer container);
}
