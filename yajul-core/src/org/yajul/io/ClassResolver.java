package org.yajul.io;

import java.io.ObjectStreamClass;
import java.io.IOException;

/**
 * An interface used by ClassResolverObjectInputStream to
 * resolve classes found in the stream.
 * User: jdavis
 * Date: Nov 3, 2003
 * Time: 7:00:53 PM
 * @author jdavis
 */
public interface ClassResolver
{
    /**
     * Returns the local class local class equivalent of the
     * specified stream class description, or <i>null</i> if this
     * resolver does not process this class.
     * @param v  an instance of class ObjectStreamClass
     * @return a Class object corresponding to <code>v</code> or Null if
     * this resolver wants the default behavior.
     * @exception java.io.IOException Any of the usual Input/Output
     * exceptions.
     */
    Class resolveClass(ObjectStreamClass v)
            throws IOException;
}
