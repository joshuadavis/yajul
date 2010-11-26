package org.yajul.fix.dictionary;

import org.yajul.fix.message.ValueType;
import org.yajul.fix.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.io.Serializable;

/**
 * A FIX dictionary.  Field, message, and component definitions.
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 9:29:47 AM
 */
public class Dictionary {
    private static final Logger log = LoggerFactory.getLogger(Dictionary.class);

    public static final String ANY_VALUE = "__ANY__";

    private String version;
    private Map<Integer, FieldDefinition> fieldDefByTag = new HashMap<Integer, FieldDefinition>();
    private Map<String, FieldDefinition> fieldDefByName = new HashMap<String, FieldDefinition>();
    private ElementList header;
    private ElementList trailer;
    private Map<String, ComponentDefinition> components = new HashMap<String, ComponentDefinition>();
    private Map<String, MessageType> msgTypes = new HashMap<String, MessageType>();
    private Map<String, MessageType> msgTypesByName = new HashMap<String, MessageType>();

    public Dictionary(String version) {
        this.version = version;
    }

    void addFieldDefinition(int num, String name, boolean required, ValueType valueType, Map<String, String> values) {
        FieldDefinition field = new FieldDefinition(num, name, required, valueType, values);
        if (fieldDefByTag.containsKey(num))
            throw new ConfigError("Duplicate tag number: " + num);
        if (fieldDefByName.containsKey(name))
            throw new ConfigError("Duplicate field name: " + name);
        fieldDefByTag.put(num, field);
        fieldDefByName.put(name, field);
    }

    public ComponentDefinition addComponentDefinition(String name, int initialSize) {
        if (components.containsKey(name))
            throw new ConfigError("Component '" + name + "' already exists!");
        ComponentDefinition def = new ComponentDefinition(name, initialSize);
        components.put(name, def);
        return def;
    }

    public MessageType addMessageType(String name, String key, int length) {
        if (msgTypesByName.containsKey(name))
            throw new ConfigError("MessageType '" + name + "' already exists!");
        if (msgTypes.containsKey(key))
            throw new ConfigError("MessageType '" + key + "' already exists!");
        MessageType msgType = new MessageType(name, key, length);
        msgTypes.put(key, msgType);
        msgTypesByName.put(name,msgType);
        return msgType;
    }

    public FieldDefinition findFieldDefinition(String name) {
        return fieldDefByName.get(name);
    }

    FieldDefinition requireFieldDefinition(String name) {
        FieldDefinition definition = findFieldDefinition(name);
        if (definition == null)
            throw new ConfigError("No field '" + name + "'");
        return definition;
    }

    void setHeader(ElementList header) {
        this.header = header;
    }

    void setTrailer(ElementList trailer) {
        this.trailer = trailer;
    }

    public ElementList getHeader() {
        return header;
    }

    public ElementList getTrailer() {
        return trailer;
    }

    public MessageType findMessageType(String msgType) {
        return msgTypes.get(msgType);
    }

    public FieldDefinition findFieldDefinition(int tag) {
        return fieldDefByTag.get(tag);
    }

    /**
     * The definition of a field used in the 'flat' list of all possible fields in the dictionary.
     */
    public class FieldDefinition {
        private int num;
        private ValueType valueType;
        private Map<String, String> values;
        private String name;
        private boolean required;

        public FieldDefinition(int num, String name, boolean required, ValueType valueType, Map<String, String> values) {
            this.num = num;
            this.valueType = valueType;
            this.name = name;
            this.required = required;
            this.values = values;
        }

        public int getNum() {
            return num;
        }

        public ValueType getValueType() {
            return valueType;
        }

        public boolean isRequired() {
            return required;
        }

        @Override
        public String toString() {
            return "FieldDefinition{" + getFieldName() +
                    ", required=" + required +
                    ", valueType=" + valueType +
                    ", values=" + values +
                    '}';
        }

        private String getFieldName() {
            return name + "<" + num + ">";
        }
    }


    public static enum ElementType  {
        FIELD, COMPONENT, GROUP,
    }
    public interface Element {
        Dictionary getDictionary();

        Serializable getKey();

        void append(int level, StringBuilder sb);

        boolean isRequired();

        boolean matchesTag(int tag);

        ElementType getElementType();
    }

    /**
     * A reference to a field definition.
     */
    public class Field implements Element {
        private FieldDefinition definition;
        private boolean required;

        public Field(FieldDefinition definition, boolean required) {
            this.definition = definition;
            this.required = required;
        }

        public Serializable getKey() {
            return getNum();
        }

        public int getNum() {
            return definition.getNum();
        }

        public ValueType getValueType() {
            return definition.getValueType();
        }

        public boolean isRequired() {
            return required;
        }

        public boolean matchesTag(int tag) {
            return tag == definition.getNum();

        }

        public Dictionary getDictionary() {
            return Dictionary.this;
        }

        public ElementType getElementType() {
            return ElementType.FIELD;

        }

        public void append(int level, StringBuilder sb) {
            sb.append('{').append(definition.getFieldName());
            sb.append(", type=").append(definition.getValueType());
            sb.append(", required=").append(required);
            sb.append('}');
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            append(0, sb);
            return sb.toString();
        }
    }

    public class Component implements Element {
        private ComponentDefinition componentDefinition;
        private boolean required;
        private ElementList parent;

        public Component(ElementList parent, ComponentDefinition componentDefinition, boolean required) {
            this.parent = parent;
            this.componentDefinition = componentDefinition;
            this.required = required;
        }

        public void append(int level, StringBuilder sb) {
            sb.append("{").append(componentDefinition.getName());
            sb.append(", required=").append(required).append(", ");
            componentDefinition.append(level + 1, sb);
            sb.append('}');
        }

        public boolean isRequired() {
            return required;

        }

        public Serializable getKey() {
            return componentDefinition.getKey();
        }

        public boolean matchesTag(int tag) {
            return componentDefinition.containsTag(tag);
        }

        public Dictionary getDictionary() {
            return Dictionary.this;
        }public ElementType getElementType() {
            return ElementType.COMPONENT;
        }
    }

    public class ElementList {
        private String name;
        private LinkedHashMap<Serializable, Element> elements;

        protected ElementList(String name, int initialSize) {
            this.name = name;
            elements = new LinkedHashMap<Serializable, Element>(initialSize);
        }

        public Dictionary getDictionary() {
            return Dictionary.this;
        }
        
        public Serializable getKey() {
            return getName();
        }

        void addField(String name, boolean required) {
            FieldDefinition definition = requireFieldDefinition(name);
            addElement(new Field(definition, required));
        }

        void addComponent(String name, boolean required) {
            ComponentDefinition cd = findComponentDefinition(name);
            if (cd == null)
                throw new ConfigError("No component '" + name + "'");
            addElement(new Component(this, cd, required));
        }

        void addElement(Element e) {
            Serializable key = e.getKey();
            if (elements.containsKey(key))
                throw new ConfigError("Already contains tag " + key);
            elements.put(key, e);
        }

        public Element get(Serializable key) {
            return elements.get(key);
        }

        public Collection<Element> getElements() {
            return elements.values();
        }

        public Serializable getName() {
            return name;
        }

        public void append(int level, StringBuilder sb) {
            appendHeader(sb);
            int i = 0;
            for (Map.Entry<Serializable, Element> entry : elements.entrySet()) {
                sb.append('\n');
                FormatUtil.indent(level + 1, sb);
                sb.append(String.format("%4d) ", ++i));
                entry.getValue().append(level + 1, sb);
            }
        }

        protected void appendHeader(StringBuilder sb) {
            sb.append(this.getClass().getSimpleName()).append(" ").append(name).append(":");
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            append(0, sb);
            return sb.toString();
        }


    }

    ComponentDefinition findComponentDefinition(String name) {
        return components.get(name);
    }

    public class Group extends ElementList implements Element {
        private FieldDefinition fieldDefinition;
        private ElementList parent;

        public boolean matchesTag(int tag) {
            return tag == fieldDefinition.getNum();
        }

        protected Group(FieldDefinition field,ElementList parent,String name, int initialSize) {
            super(name, initialSize);
            this.fieldDefinition = field;
            this.parent = parent;
        }

        public boolean isRequired() {
            return false;
        }

        public ElementType getElementType() {
            return ElementType.GROUP;
        }

        @Override
        protected void appendHeader(StringBuilder sb) {
            sb.append("Group: ").append(fieldDefinition.getFieldName());
        }


    }

    public class ComponentDefinition extends ElementList {
        protected ComponentDefinition(String name, int initialSize) {
            super(name, initialSize);
        }

        public boolean containsTag(int tag) {
            return false;  // TODO: Implement this!
        }
    }

    public class MessageType extends ElementList {
        private String key;

        public MessageType(String name, String key, int initialSize) {
            super(name, initialSize);
            this.key = key;
        }

        @Override
        protected void appendHeader(StringBuilder sb) {
            sb.append(getName()).append('<').append(key).append('>');
        }
    }

}
