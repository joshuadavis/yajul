package org.yajul.micro;

/**
 * Classes in bootstrap that implement this interface can define any number of components in the microcontainer.
 * The bootstrap class itself will be registered in the microcontainer first, then if it implements this interface
 * it will be immediately instantiated and executed.
 * <br>
 * User: josh
 * Date: Mar 11, 2008
 * Time: 10:39:50 PM
 */
public interface Configuration {
    void addComponents(MicroContainer microContainer);
}
