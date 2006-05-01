package org.yajul.framework.test;

import org.springframework.core.io.Resource;
import org.yajul.util.FieldPrinter;

/**
 * A Java Bean used to test the ServiceLocator class.
 * User: jdavis
 * Date: Feb 25, 2004
 * Time: 11:47:53 AM
 * @author jdavis
 */
public class SimpleBean
{
    private String exampleProperty;
    private String userName;
    private Object thing;
    private Resource resource;

    public String getExampleProperty()
    {
        return exampleProperty;
    }

    public void setExampleProperty(String exampleProperty)
    {
        this.exampleProperty = exampleProperty;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public Object getThing()
    {
        return thing;
    }

    public void setThing(Object thing)
    {
        this.thing = thing;
    }

    public Resource getResource()
    {
        return resource;
    }

    public void setResource(Resource resource)
    {
        this.resource = resource;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * @return  a string representation of the object.
     */
    public String toString()
    {
        return FieldPrinter.toString(this);
    }
}
