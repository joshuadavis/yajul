package org.yajul.fix.dictionary;

import org.yajul.fix.message.ValueType;
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
    private Map<Integer,FieldDefinition> fieldDefByTag = new HashMap<Integer,FieldDefinition>();
    private Map<String,FieldDefinition> fieldDefByName = new HashMap<String,FieldDefinition>();
    private ElementList header;
    private ElementList trailer;
    private Map<String,ComponentDefinition> components = new HashMap<String,ComponentDefinition>();
    private Map<String,ElementList> msgTypes = new HashMap<String,ElementList>();
    
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
        fieldDefByName.put(name,field);
    }

    public ComponentDefinition addComponentDefinition(String name, int initialSize) {
        if (components.containsKey(name))
            throw new ConfigError("Component '" + name + "' already exists!");
        ComponentDefinition def = new ComponentDefinition(name,initialSize);
        components.put(name,def);
        return def;
    }


    public FieldDefinition findField(String name) {
        return fieldDefByName.get(name);
    }

    void setHeader(ElementList header) {
        this.header = header;
    }

    void setTrailer(ElementList trailer) {
        if (log.isDebugEnabled())
            log.debug("setTrailer() : " + trailer);
        this.trailer = trailer;
    }

    public ElementList getHeader() {
        return header;
    }

    public ElementList getTrailer() {
        return trailer;
    }

    public ElementList findMessageType(String msgType) {
        return null;  // TODO: Implement this!
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

    public interface Element {
        Serializable getKey();

        void append(int level, StringBuilder sb);

        boolean isRequired();
    }

    /**
     * A reference to a field definition.
     */
    public class Field implements Element {
        private FieldDefinition definition;
        private boolean required;

        public Field(FieldDefinition definition,boolean required) {
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

        public void append(int level, StringBuilder sb) {
            sb.append('{').append(definition.getFieldName());
            sb.append(", type=").append(definition.getValueType());
            sb.append(", required=").append(required);
            sb.append('}');
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            append(0,sb);
            return sb.toString();
        }
    }

    public class Component implements Element {
        private ComponentDefinition componentDefinition;
        private boolean required;

        public Component(ComponentDefinition componentDefinition,boolean required) {
            this.componentDefinition = componentDefinition;
            this.required = required;
        }

        public void append(int level, StringBuilder sb) {
            sb.append("{").append(componentDefinition.getName());
            sb.append(", required=").append(required);
            componentDefinition.append(level+1,sb);
            sb.append('}');
        }

        public boolean isRequired() {
            return required;

        }

        public Serializable getKey() {
            return null;
        }

    }

    public class ElementList {
        private String name;
        private LinkedHashMap<Serializable,Element> elements;
        private Set<Serializable> required;

        protected ElementList(String name,int initialSize) {
            this.name = name;
            elements = new LinkedHashMap<Serializable,Element>(initialSize);
            required = new HashSet<Serializable>(initialSize);
        }

        public Serializable getKey() {
            return getName();
        }

        void addField(String name, boolean required) {
            FieldDefinition definition = findField(name);
            if (definition == null)
                throw new ConfigError("No field '" + name + "'");
            addElement(new Field(definition,required),required);
        }

        void addComponent(String name, boolean required) {
            ComponentDefinition cd = findComponentDefinition(name);
            if (cd == null)
                throw new ConfigError("No component '" + name + "'");
            addElement(new Component(cd,required),required);
        }

        void addElement(Element e, boolean required) {
            Serializable key = e.getKey();
            if (elements.containsKey(key))
                throw new ConfigError("Already contains tag " + key);
            elements.put(key,e);
            if (required) {
                this.required.add(key);
            }
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

        public void append(int level,StringBuilder sb) {
            sb.append(this.getClass().getSimpleName()).append(" ").append(name).append(":");
            for (Map.Entry<Serializable, Element> entry : elements.entrySet()) {
                sb.append('\n');
                org.yajul.fix.util.Formatter.indent(level,sb);
                sb.append(entry.getKey()).append("->");
                entry.getValue().append(level+1,sb);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            append(0,sb);
            return sb.toString();
        }
    }

    ComponentDefinition findComponentDefinition(String name) {
        return components.get(name);
    }

    public class Group extends ElementList implements Element {
        private Field countField;
        protected Group(String name, int initialSize) {
            super(name, initialSize);
        }

        public boolean isRequired() {
            return false;
        }
    }

    public class ComponentDefinition extends ElementList {
        protected ComponentDefinition(String name, int initialSize) {
            super(name, initialSize);
        }
    }

    public class MessageType extends ElementList {
        public MessageType(String name, int initialSize) {
            super(name, initialSize);
        }
    }
    
}
