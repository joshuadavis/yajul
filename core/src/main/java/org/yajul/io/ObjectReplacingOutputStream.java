package org.yajul.io;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;

/**
 * Replaces objects using an ObjectReplacer.
 * <br>
 * User: josh
 * Date: Sep 9, 2009
 * Time: 11:58:50 AM
 */
public class ObjectReplacingOutputStream extends ObjectOutputStream {
    private ObjectReplacer replacer;

    public ObjectReplacingOutputStream(OutputStream out,ObjectReplacer replacer) throws IOException {
        super(out);
        this.replacer = replacer;
        enableReplaceObject(true);
    }

    public ObjectReplacingOutputStream(ObjectOutput out, ObjectReplacer replacer) throws IOException {
        this(new ObjectOutputStreamAdapter(out),replacer);
    }

    @Override
    protected Object replaceObject(Object obj) throws IOException {
        if (replacer != null)
            return replacer.replaceObject(obj);
        else
            return super.replaceObject(obj);
    }
}
