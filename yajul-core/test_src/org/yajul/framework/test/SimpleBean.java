package org.yajul.framework.test;

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

}
