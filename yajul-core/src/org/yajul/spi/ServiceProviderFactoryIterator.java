/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002-2003  YAJUL Developers, Joshua Davis, Kent Vogel.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 ******************************************************************************/
package org.yajul.spi;

import org.apache.log4j.Logger;
import org.yajul.util.ObjectFactory;
import org.yajul.util.DetailedRuntimeException;

import java.util.Iterator;
import java.util.Enumeration;
import java.net.URL;
import java.io.IOException;

/**
 * Returns information about implementations of an SPI.
 * User: josh
 * Date: Nov 2, 2003
 * Time: 9:33:55 AM
 */
public class ServiceProviderFactoryIterator implements Iterator
{
    /** A logger for this class. **/
    private static final Logger log = Logger.getLogger(ServiceProviderFactoryIterator.class);

    private final Enumeration resources;
    private final ClassLoader loader;
    private final String key;

    /**
     * Creates a new ServiceProviderFactory iterator for the specified key.
     * @param key The SPI key, typically the abstract class or interface name.
     * @param loader The class loader to use.  If null, the current class loader
     * will be used.
     */
    public ServiceProviderFactoryIterator(String key,ClassLoader loader)
    {
        this.key = key;
        String serviceId = ServiceProviderFactory.getServiceResourceName(key);

        if (loader == null)
            loader = ObjectFactory.getCurrentClassLoader();

        try
        {
            final Enumeration resources = loader.getResources(serviceId);
            this.resources = resources;
            this.loader = loader;
        }
        catch (IOException ioe)
        {
            throw new DetailedRuntimeException("Unexpected IOException "
                    + ioe.getMessage(), ioe);
        }
    }

    /**
     * List available implementations of a particular class.
     * @param key The class or interface.
     * @param loader The class loader to use.
     */
    public ServiceProviderFactoryIterator(Class key,ClassLoader loader)
    {
        this(key.getName(),loader);
    }

    public boolean hasNext()
    {
        return resources.hasMoreElements();
    }

    public Object next()
    {
        URL url = (URL) resources.nextElement();
        if (url == null)
            return null;
        ServiceProviderFactory serviceDescriptor = null;
        try
        {
            serviceDescriptor = new ServiceProviderFactory(key,loader,url);
        }
        catch (IOException e)
        {
            log.warn("Unexpected exception: " + e.getMessage(),e);
            serviceDescriptor = null;
        }
        return serviceDescriptor;
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

}
