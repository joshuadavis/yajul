package org.yajul.io.archiver;

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;

/**
 * User: Joshua Davis<br>
 * Date: Jul 21, 2005<br>
 * Time: 8:08:00 AM<br>
 */
public class InputWrapper extends FilterInputStream
{
    private Source source;

    public InputWrapper(Source source, InputStream in)
    {
        super(in);
        this.source = source;
    }

    public void close() throws IOException
    {
        super.close();
        source.afterStreamClosed();
    }
}
