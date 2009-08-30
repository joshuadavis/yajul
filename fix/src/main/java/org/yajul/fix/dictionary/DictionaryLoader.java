package org.yajul.fix.dictionary;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yajul.fix.message.ValueType;
import static org.yajul.fix.util.DomHelper.*;
import org.yajul.fix.util.DomHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(DictionaryLoader.class);

    private Document doc;
    private Dictionary dictionary;
    private static final String COMPONENT = "component";
    private static final String FIELD = "field";
    private static final String GROUP = "group";
    private static final String NAME = "name";
    private static final String REQUIRED = "required";
    private static final String FIELDS = "fields";
    private static final String NUMBER = "number";
    private static final String TYPE = "type";
    private static final String MESSAGES = "messages";
    private static final String HEADER = "header";
    private static final String TRAILER = "trailer";
    private static final String COMPONENTS = "components";

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
        Dictionary.ElementList header = readElementList(HEADER);
        if (log.isDebugEnabled())
            log.debug("header : " + header);
        dictionary.setHeader(header);
        Dictionary.ElementList trailer = readElementList(TRAILER);
        if (log.isDebugEnabled())
            log.debug("trailer : " + trailer);
        dictionary.setTrailer(trailer);
        readComponents();
        readMessageTypes();
        return dictionary;
    }

    private void readComponents() {
        NodeList nodeList = getSectionChildren(doc.getDocumentElement(), COMPONENTS);
        // The first past adds empty component definitions.
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!node.getNodeName().equals(COMPONENT))
                    throw new ConfigError("<component> expected, found <" + node.getNodeName() + ">");
                String name = requireAttribute(node, NAME);
                dictionary.addComponentDefinition(name, node.getChildNodes().getLength());
            }
        }
        // Fill in the component definitions in a second pass, so forward references
        // are handled.
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!node.getNodeName().equals(COMPONENT))
                    throw new ConfigError("<component> expected, found <" + node.getNodeName() + ">");
                String name = requireAttribute(node, NAME);
                Dictionary.ComponentDefinition c = dictionary.findComponentDefinition(name);
                readElements(c, node);
                if (log.isDebugEnabled())
                    log.debug("\n" + c);
            }
        }
    }

    private void readElements(Dictionary.ElementList elementList, Node parent) {
        NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            readElement(elementList, node);
        }
    }

    private void readElement(Dictionary.ElementList elementList, Node node) {
        String elementName = node.getNodeName();
        if (FIELD.equals(elementName) || COMPONENT.equals(elementName) || GROUP.equals(elementName)) {
            String name = requireAttribute(node, NAME);
            boolean required = getAttributeYN(node, REQUIRED);
            if (FIELD.equals(elementName)) {
                elementList.addField(name, required);
            } else if (COMPONENT.equals(elementName)) {
                elementList.addComponent(name, required);
            } else if (GROUP.equals(elementName)) {
                // Groups are defined 'inline', the name is actually a field definition.
                Dictionary.FieldDefinition fd = dictionary.requireFieldDefinition(name);
                NodeList groupNodes = node.getChildNodes();
                Dictionary.Group g = dictionary.new Group(fd,elementList, name, groupNodes.getLength());
                // Fill in the group and add it.
                readElements(g, node);
                elementList.addElement(g, required);
            }
        }
    }

    private void readMessageTypes() {
        NodeList nodeList = getSectionChildren(doc.getDocumentElement(), MESSAGES);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (!node.getNodeName().equals("message"))
                    throw new ConfigError("<message> expected, found <" + node.getNodeName() + ">");
                String name = requireAttribute(node, NAME);
                String key = requireAttribute(node,"msgtype");
                Dictionary.MessageType msgType = dictionary.addMessageType(name,
                        key,
                        node.getChildNodes().getLength());
                readElements(msgType,node);
                if (log.isDebugEnabled())
                    log.debug(msgType.toString());
            }
        }

    }

    private Dictionary.ElementList readElementList(String fieldListElementName) {
        Node section = getSection(doc.getDocumentElement(), fieldListElementName);
        return readFieldList(fieldListElementName, section);
    }

    private Dictionary.ElementList readFieldList(String listName, Node node) {

        NodeList childNodes = node.getChildNodes();
        if (childNodes.getLength() == 0) {
            throw new ConfigError("No fields found!");
        }
        Dictionary.ElementList list = dictionary.new ElementList(listName, childNodes.getLength());
        readElements(list, node);
        return list;
    }

    private void readFields() {
        Element documentElement = doc.getDocumentElement();
        // FIELDS
        int count = 0;
        NodeList fieldNodes = getSectionChildren(documentElement, FIELDS);
        for (int i = 0; i < fieldNodes.getLength(); i++) {
            Node fieldNode = fieldNodes.item(i);
            if (fieldNode.getNodeName().equals(FIELD)) {
                String name = requireAttribute(fieldNode, NAME);
                int num = requireIntAttribute(fieldNode, NUMBER);
                ValueType valueType = getValueType(fieldNode, name);
                boolean required = getAttributeYN(fieldNode, REQUIRED);
                Map<String, String> values = readValues(fieldNode);
                dictionary.addFieldDefinition(num, name, required, valueType, values);
                count++;
            } // element is <field>
        } // for
        if (log.isDebugEnabled())
            log.debug("readFields() : " + count);
    }

    private Map<String, String> readValues(Node fieldNode) {
        NodeList valueNodes = fieldNode.getChildNodes();
        int valueCount = valueNodes.getLength();
        Map<String, String> values = new HashMap<String, String>(valueCount);
        for (int j = 0; j < valueNodes.getLength(); j++) {
            Node valueNode = valueNodes.item(j);
            if (valueNode.getNodeName().equals("value")) {
                String e = requireAttribute(valueNode, "enum");
                String description = getAttribute(valueNode, "description");
                values.put(e, description);
            }
        }
        boolean allowOtherValues = getBooleanAttribute(fieldNode, "allowOtherValues", false);
        if (allowOtherValues)
            values.put(Dictionary.ANY_VALUE, null);
        return values;
    }

    private ValueType getValueType(Node fieldNode, String name) {
        String type = requireAttribute(fieldNode, TYPE);
        ValueType valueType;
        try {
            valueType = ValueType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new ConfigError("No such value type: " + type + " for " + name);
        }
        return valueType;
    }

    private NodeList getSectionChildren(Element documentElement, String sectionElementName) {
        Node section = getSection(documentElement, sectionElementName);
        if (section == null)
            return new DomHelper.EmptyNodeList();
        NodeList childNodes = section.getChildNodes();
        if (childNodes.getLength() == 0) {
            throw new ConfigError("<" + sectionElementName + "> section is empty");
        }
        return childNodes;
    }

    private Node getSection(Element documentElement, String sectionElementName) {
        NodeList nodeList = documentElement.getElementsByTagName(sectionElementName);
        if (nodeList.getLength() == 0) {
            return null;
        }
        if (nodeList.getLength() > 1) {
            throw new ConfigError("More than one section: <" + sectionElementName + ">");
        }
        return nodeList.item(0);
    }

    public static int requireIntAttribute(Node node, String attribute) {
        String sval = requireAttribute(node, attribute);
        return Integer.parseInt(sval);
    }

    public static String requireAttribute(Node node, String attribute) {
        String value = getAttribute(node, attribute);
        if (value == null || value.length() == 0) {
            throw new ConfigError("<" + node.getNodeName() + "> does not have attribute '" + attribute + "'");
        }
        return value;
    }

    public static boolean getAttributeYN(Node fieldNode, String attribute) {
        String requiredStr = getAttribute(fieldNode, attribute, "N");
        return "Y".equalsIgnoreCase(requiredStr);
    }

    private void readVersion() {
        Element documentElement = doc.getDocumentElement();
        if (!documentElement.getNodeName().equals("fix")) {
            throw new ConfigError(
                    "Could not parse data dictionary file, or no <fix> node found at root");
        }
        String major = requireAttribute(documentElement, "major");
        String minor = requireAttribute(documentElement, "minor");
        String version = "FIX." + major + "." + minor;
        dictionary = new Dictionary(version);
        if (log.isDebugEnabled())
            log.debug("readVersion() : " + version);
    }
}
