package org.yajul.serialization;

import java.io.ObjectInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ObjectInput;

/**
 * Resolves objects from in input stream.  Useful for implementing Externalizable, where the write
 * method uses ObjectReplacingInputStream.
 * <br>
 * User: josh
 * Date: Sep 9, 2009
 * Time: 12:05:54 PM
 */
public class ObjectResolvingInputStream extends ObjectInputStream {
    private ObjectResolver resolver;

    public ObjectResolvingInputStream(InputStream in, ObjectResolver resolver) throws IOException {
        super(in);
        this.resolver = resolver;
        enableResolveObject(true);
    }

    public ObjectResolvingInputStream(ObjectInput in, ObjectResolver resolver) throws IOException {
        this(new ObjectInputStreamAdapter(in),resolver);
    }

    @Override
    protected Object resolveObject(Object obj) throws IOException {
        if (resolver != null)
            return resolver.resolveObject(obj);
        else
            return super.resolveObject(obj);
    }
}
