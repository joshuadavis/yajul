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
package org.yajul.servlet.util;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;

/**
 * Provides JSP/Servlet JavaBean utility methods.
 * User: josh
 * Date: Jan 17, 2004
 * Time: 8:42:56 AM
 */
public class BeanUtil
{
    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(BeanUtil.class);

    /** Specifies web application scope. **/
    public static final int SCOPE_APPLICATION = 0;
    /** Specifies HttpSession scope. **/
    public static final int SCOPE_SESSION = 1;
    /** Specifies request scope. **/
    public static final int SCOPE_REQUEST = 2;

    public static Object useBean(HttpServletRequest request,
                                        int scope,String name,
                                        Class beanClass)
            throws InstantiationException, IllegalAccessException
    {
        Object bean = findBean(request, scope, name);
        if (bean != null)
            return bean;

        if (log.isDebugEnabled())
            log.debug("useBean() : Instantiating " + beanClass.getName() + "...");
        bean = beanClass.newInstance();

        setBean(request, scope, name, bean);
        return bean;
    }

    public static void setBean(HttpServletRequest request, int scope, String name, Object bean)
    {
        switch (scope)
        {
            case SCOPE_APPLICATION:
                request.getSession().getServletContext().setAttribute(name,bean);
                break;
            case SCOPE_SESSION:
                request.getSession().setAttribute(name,bean);
                break;
            case SCOPE_REQUEST:
                request.setAttribute(name,bean);
                break;
            default:
                throw new IllegalArgumentException("Unknown scope value: " + scope);
        }
    }

    public static Object findBean(HttpServletRequest request, int scope, String name)
    {
        Object bean = null;
        switch (scope)
        {
            case SCOPE_APPLICATION:
                if (request.getSession(false) == null)
                    bean = null;
                else
                    bean = request.getSession().getServletContext().getAttribute(name);
                break;
            case SCOPE_SESSION:
                if (request.getSession(false) == null)
                    bean = null;
                else
                    bean = request.getSession().getAttribute(name);
                break;
            case SCOPE_REQUEST:
                bean = request.getAttribute(name);
                break;
            default:
                throw new IllegalArgumentException("Unknown scope value: " + scope);
        }
        return bean;
    }

}
