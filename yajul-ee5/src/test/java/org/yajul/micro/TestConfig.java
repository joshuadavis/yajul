package org.yajul.micro;

import java.util.Set;
import java.util.TreeSet;

/**
 * Container configuring component.
 * <br>
 * User: josh
 * Date: Mar 11, 2008
 * Time: 10:50:51 PM
 */
public class TestConfig implements Configuration {
    public void addComponents(MicroContainer microContainer) {
        microContainer.addComponent(Set.class, TreeSet.class);
        microContainer.addComponent("magicNumber",42L);
    }
}
