// $Id$
package org.yajul.framework.test;

import org.apache.log4j.Logger;

/**
 * TODO: Add class javadoc
 *
 * @author josh Aug 3, 2004 9:59:46 PM
 */
public class ResourceEditor extends org.springframework.core.io.ResourceEditor
{
    /**
     * A logger for this class. *
     */
    private static Logger log = Logger.getLogger(ResourceEditor.class);

    public void setAsText(String text)
    {
        super.setAsText(text);
    }
}
