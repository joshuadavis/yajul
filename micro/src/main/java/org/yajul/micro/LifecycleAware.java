package org.yajul.micro;

/**
 * Implement this on all InputProcessor, Aggregator or grid singletons to get callbacks
 * when the object is created, and when it is destroyed.
 * <br>User: Joshua Davis
 * Date: Nov 6, 2006
 * Time: 10:07:04 PM
 */
public interface LifecycleAware
{
    void initialize();
    
    void terminate();
}
