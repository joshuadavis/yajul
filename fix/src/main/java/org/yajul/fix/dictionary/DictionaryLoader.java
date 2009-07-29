package org.yajul.fix.dictionary;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yajul.fix.ValueType;
import static org.yajul.fix.util.DomHelper.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads Dictionaries from resources or files.
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 8:57:03 AM
 */
public class DictionaryLoader {
    private Document doc;
    private Dictionary dictionary;

    public DictionaryLoader(Document doc) {
        this.doc = doc;
    }

    public static Dictionary load(String resource) throws Exception {
        Document doc = parseResource(resource);
        return load(doc);
    }

    public static Dictionary load(Document doc) {
        DictionaryLoader loader = new DictionaryLoader(doc);
        return loader.load();
    }

    private Dictionary load() {
        readVersion();
        readFields();
        return dictionary;
    }

    private void readFields() {
        Element documentElement = doc.getDocumentElement();
        // FIELDS
        NodeList fieldsNode = documentElement.getElementsByTagName("fields");
        if (fieldsNode.getLength() == 0) {
            throw new ConfigError("<fields> section not found in data dictionary");
        }

        NodeList fieldNodes = fieldsNode.item(0).getChildNodes();
        if (fieldNodes.getLength() == 0) {
            throw new ConfigError("No fields defined");
        }

        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Node fieldNode = fieldNodes.item(i);
            if (fieldNode.getNodeName().equals("field")) {
                String name = getAttribute(fieldNode, "name");
                if (name == null) {
                    throw new ConfigError("<field> does not have a name attribute");
                }

                int num = getIntAttribute(fieldNode,"number",-1);
                if (num == -1) {
                    throw new ConfigError("<field> " + name + " does not have a number attribute");
                }

                String type = getAttribute(fieldNode, "type");
                ValueType valueType = ValueType.valueOf(type);
                if (valueType == null) {
                    throw new ConfigError("<field> " + name + " does not have a valid type attribute");
                }

                NodeList valueNodes = fieldNode.getChildNodes();
                int valueCount = valueNodes.getLength();
                Map<String,String> values = new HashMap<String,String>(valueCount);
                for (int j = 0; j < valueNodes.getLength(); j++) {
                    Node valueNode = valueNodes.item(j);
                    if (valueNode.getNodeName().equals("value")) {
                        String e = getAttribute(valueNode, "enum");
                        if (e == null) {
                            throw new ConfigError("<value> does not have enum attribute in field "
                                    + name);
                        }
                        String description = getAttribute(valueNode, "description");
                        values.put(e,description);
                    }
                }
                boolean allowOtherValues = getBooleanAttribute(fieldNode,"allowOtherValues",false);
                if (allowOtherValues)
                    values.put(Dictionary.ANY_VALUE,null);
                dictionary.addField(num,valueType,values);
            } // element is <field>
        } // for
    }

    private void readVersion() {
        Element documentElement = doc.getDocumentElement();
        if (!documentElement.getNodeName().equals("fix")) {
            throw new ConfigError(
                    "Could not parse data dictionary file, or no <fix> node found at root");
        }

        if (!documentElement.hasAttribute("major")) {
            throw new ConfigError("major attribute not found on <fix>");
        }

        if (!documentElement.hasAttribute("minor")) {
            throw new ConfigError("minor attribute not found on <fix>");
        }

        String version = "FIX." + documentElement.getAttribute("major") + "."
                + documentElement.getAttribute("minor");
        dictionary = new Dictionary(version);
    }
}
