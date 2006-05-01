package org.yajul.io.archiver;

import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

/**
 * Encodes a document id into a file name using URL encoding.
 * User: Joshua Davis<br>
 * Date: Jul 21, 2005<br>
 * Time: 7:56:43 AM<br>
 */
public class URLIdEncoder implements IdEncoder
{
    public static final String DEFAULT_CHARACTER_ENCODING = "UTF-8";

    public String encode(Object id)
    {
        String fileName = id.toString();
        try
        {
            fileName = URLEncoder.encode(fileName,DEFAULT_CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new RuntimeException("Unable to encode document id into a file name!",e);
        }
        return fileName;
    }
}
