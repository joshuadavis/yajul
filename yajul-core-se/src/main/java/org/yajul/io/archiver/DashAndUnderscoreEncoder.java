package org.yajul.io.archiver;

/**
 * Encodes document ids by replacing special characters with dashes and whitespace with
 * underscores.
 * User: Joshua Davis<br>
 * Date: Jul 21, 2005<br>
 * Time: 7:53:07 AM<br>
 */
public class DashAndUnderscoreEncoder implements IdEncoder
{
    public String encode(Object id)
    {
        String fileName = id.toString();
        char[] chars = fileName.toCharArray();
        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];
            switch (c)
            {
            case '/':
                chars[i] = '-';
                break;
            case '\\':
                chars[i] = '-';
                break;
            case ' ':
                chars[i] = '_';
                break;
            case '\t':
                chars[i] = '_';
                break;
            case '\r':
                chars[i] = '_';
                break;
            case '\n':
                chars[i] = '_';
                break;
            default:
                if (Character.isISOControl(c))
                    chars[i] = '_';
                break;
            }
        }
        fileName = new String(chars);
        return fileName;
    }
}
