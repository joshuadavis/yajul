package org.yajul.io;

import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamClass;

/**
 * An ObjectInputStream adapter that uses a class resolving delegate to
 * resolve classes found in the underlying input stream.  This
 * is useful for reading old versions of serialized objects.
 * User: jdavis
 * Date: Nov 3, 2003
 * Time: 6:57:49 PM
 * @author jdavis
 */
public class ClassResolverObjectInputStream extends ObjectInputStream
{
    private ClassResolver resolver;
    /**
     * Create an ObjectInputStream that reads from the specified InputStream.
     * The stream header containing the magic number and version number
     * are read from the stream and verified. This method will block
     * until the corresponding ObjectOutputStream has written and flushed the
     * header.
     *
     * @param in  the underlying <code>InputStream</code> from which to read
     * @exception StreamCorruptedException The version or magic number are
     * incorrect.
     * @exception IOException An exception occurred in the underlying stream.
     */
    public ClassResolverObjectInputStream(InputStream in,ClassResolver resolver)
            throws IOException, StreamCorruptedException
    {
        super(in);
        this.resolver = resolver;
    }

    /**
     * Load the local class equivalent of the specified stream class description.
     *
     * Subclasses may implement this method to allow classes to be
     * fetched from an alternate source.
     *
     * The corresponding method in ObjectOutputStream is
     * annotateClass.  This method will be invoked only once for each
     * unique class in the stream.  This method can be implemented by
     * subclasses to use an alternate loading mechanism but must
     * return a Class object.  Once returned, the serialVersionUID of the
     * class is compared to the serialVersionUID of the serialized class.
     * If there is a mismatch, the deserialization fails and an exception
     * is raised. <p>
     *
     * By default the class name is resolved relative to the class
     * that called readObject. <p>
     *
     * @param v  an instance of class ObjectStreamClass
     * @return a Class object corresponding to <code>v</code>
     * @exception IOException Any of the usual Input/Output exceptions.
     * @exception ClassNotFoundException If class of
     * a serialized object cannot be found.
     */
    protected Class resolveClass(ObjectStreamClass v)
            throws IOException, ClassNotFoundException
    {

        Class c = resolver.resolveClass(v);
        if (c == null)
            c = super.resolveClass(v);
        return c;
    }


}
