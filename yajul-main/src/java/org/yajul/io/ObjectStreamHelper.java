/*******************************************************************************
 * $Id$
 * $Author$
 * $Date$
 *
 * Copyright 2002 - YAJUL Developers, Joshua Davis, Kent Vogel.
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
/*
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Oct 12, 2002
 * Time: 11:13:20 AM
 */
package org.yajul.io;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Provides utility methods that are usefull when dealing with Java Object
 * Serialization.
 * @author Joshua Davis
 */
public class ObjectStreamHelper
{
    /**
     * Serializes an object into an array of bytes.
     * @param obj       The object to be serialized.
     * @return  byte[]  The array of bytes that is the object in serialized
     * form.
     * @throws IOException  if there is a problem serializing the object.
     */
    public static final byte[] serialize(Object obj)
            throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.flush();
        return baos.toByteArray();
    }

    /**
     * De-serializes (reads) an object from a byte array in Java Serialization
     * Format.
     * @param bytes     The byte array that contains the object.
     * @return Object   The object, deserialized from the byte array.
     * @throws IOException if there was a problem deserializing the object.
     * @throws ClassNotFoundException An object in the stream references a
     * class that is not in the current classpath or class loader.
     */
    public static final Object deserialize(byte[] bytes)
            throws IOException, ClassNotFoundException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }
}
