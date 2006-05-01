// $Id$
package org.yajul.framework;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.yajul.util.DetailedRuntimeException;

/**
 * A specialized XmlApplicationContext that ServiceLocator uses to get the multi
 * resource loading behavior automatically.
 * @author josh Aug 4, 2004 7:55:54 AM
 */
public class ServiceLocatorApplicationContext extends AbstractXmlApplicationContext
{
    /**
     * A logger for this class. *
     */
    private static Logger log = Logger.getLogger(ServiceLocatorApplicationContext.class);

    private String[] configLocations;

    public ServiceLocatorApplicationContext()
    {
        super();
    }

    public void setResource(String resource)
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ArrayList resourceList = new ArrayList();
        try
        {
            // If there are multiple resources, load 'em!
            Enumeration en = loader.getResources(resource);
            while (en.hasMoreElements())
            {
                URL url = (URL) en.nextElement();
                String path = url.toExternalForm();
                resourceList.add(path);
            }
        }
        catch (IOException e)
        {
            log.error("Unable to load bean definitions due to: " + e);
            throw new DetailedRuntimeException("Unable to create bean factory from resource '" + resource + "' due to: " + e, e);
        }
        if (resourceList.size() > 0)
            configLocations = (String[]) resourceList.toArray(new String[resourceList.size()]);
        else
            configLocations = new String[] { resource };
    }

    protected String[] getConfigLocations()
    {
        return configLocations;
    }
}
