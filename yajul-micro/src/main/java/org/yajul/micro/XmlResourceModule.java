package org.yajul.micro;

import org.yajul.xml.DOMUtil;
import org.yajul.util.ReflectionUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.net.URL;

import com.google.inject.Module;

/**
 * Loads Guice modules listed in XML files.
 * <br>
 * User: josh
 * Date: Nov 10, 2009
 * Time: 12:05:10 PM
 */
public class XmlResourceModule extends AbstractResourceModule
{
    private static final Logger log = LoggerFactory.getLogger(XmlResourceModule.class);

    private static final String ROOT_ELEMENT_NAME = "modules";

    public XmlResourceModule(String resourceName, ClassLoader classLoader)
    {
        super(resourceName, classLoader);
    }

    public XmlResourceModule(String resourceName)
    {
        super(resourceName);
    }

    protected void configureFromResource(InputStream stream, URL url) throws Exception
    {
        // Parse the XML resource.
        Document doc = DOMUtil.parse(stream);
        // The parent element is <modules>
        if (!ROOT_ELEMENT_NAME.equals(doc.getDocumentElement().getTagName()))
            throw new Exception("Expected <" + ROOT_ELEMENT_NAME + ">, found " + doc.getDocumentElement().getTagName() + " in " + url);

        // Add each Guice module class name.
        List<Element> moduleElements = DOMUtil.getChildElements(doc);
        for (Element moduleElement : moduleElements)
        {
            String moduleClassName = DOMUtil.getChildText(moduleElement);
            Module module = ReflectionUtil.createInstance(moduleClassName,getClassLoader(),Module.class);
            log.info("Configuring with " + module + " from " + url);
            binder().install(module);
        }
    }
}
