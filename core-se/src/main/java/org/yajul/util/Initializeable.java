/*******************************************************************************
 * $Id:Initializeable.java 396 2007-09-11 12:39:05Z pgmjsd $
 * $Author:pgmjsd $
 * $Date:2007-09-11 08:39:05 -0400 (Tue, 11 Sep 2007) $
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
package org.yajul.util;

import java.util.Properties;

/**
 * Defines the general behavior of an abstract factory.  ObjectFactory
 * uses this interface to initialize objects that it creates if the class
 * implements Initializeable.
 * User: josh
 * Date: Oct 26, 2003
 * Time: 10:17:58 AM
 */
public interface Initializeable
{
    /**
     * The implementation will initialize itself from the properties object.
     * @param properties The initialization properties.
     * @throws InitializationException if the object cannot be initialized.
     */
    void initialize(Properties properties) throws InitializationException;
}
