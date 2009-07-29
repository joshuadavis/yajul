package org.yajul.fix.dictionary;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * TODO: Add class level comments!
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 8:57:03 AM
 */
public class DictionaryLoader {
    public static Dictionary load(InputStream input) throws Exception {
        JAXBContext jaxbContext= JAXBContext.
                         newInstance("org.yajul.fix.dictionary");
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (Dictionary)unmarshaller.unmarshal(input);

    }

    public static Dictionary load(String resource) throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream input = cl.getResourceAsStream(resource);
        if (input != null)
            return load(input);
        else
            throw new Exception("Resource " + resource + " not found.");
    }
}
