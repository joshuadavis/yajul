
package org.yajul.util;

import java.util.Map;

/**
 * Encapsulates a pair of strings.
 */
public class NameValuePair implements java.io.Serializable
{
    private String name;
    private String value;

    /**
     * Default constructor (name and value will be null).
     */
    public NameValuePair() {}
    
    /**
	 * Construct an initialized name/value pair.
	 * @param n Name string
	 * @param v Value string
	 */
	public NameValuePair(String n,String v)
    {
        name = n;
        value = v;
    }

    /**
	 * Creates a name/value pair from a map entry.
	 * @param e Entry - the key will be the name, and the value will be the value.
	 */
	public NameValuePair(Map.Entry e)
    {
        name = (String)e.getKey();
        value = (String)e.getValue();
    }
    
    /**
     * Returns the name of the name / value pair.
	 * @return The name part of the name / value pair.
	 */
	public String getName() { return name; }
    
    /**
     * Returns the value of the name / value pair.
	 * @return The value part of the name / value pair.
	 */
	public String getValue() { return value; }
    
    /**
     * Sets the name to a new name.
	 * @param n The new name.
	 */
	public void setName(String n) { name = n; }
    
    /**
     * Sets the value to a new value.
	 * @param v The new value.
	 */
	public String setValue(String v) { value = v; return value; }

    /**
     * Default string conversion.
	 * @return String of the form [&lt;name&gt;,&lt;value&gt;]
	 */
	public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("[").append(getName()).append(",").append(getValue()).append("]");
        return buf.toString();
    }

    /**
     * Publically accessible clone method.
     * @return     a clone of this instance.
     * @see java.lang.Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        return new NameValuePair(name,value);
    }
}