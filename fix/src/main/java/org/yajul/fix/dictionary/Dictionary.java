package org.yajul.fix.dictionary;

import org.yajul.fix.message.ValueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
    private Map<Integer,Field> fields = new HashMap<Integer,Field>();
    private Map<String,Field> fieldsByName = new HashMap<String,Field>();
    private FieldList header;
    private FieldList trailer;

    public Dictionary(String version) {
        this.version = version;
    }

    void addField(int num, String name, boolean required, ValueType valueType, Map<String, String> values) {
        Field field = new Field(num, name, required, valueType, values);
        if (log.isDebugEnabled())
            log.debug("addField() : " + field);
        if (fields.containsKey(num))
            throw new ConfigError("Duplicate tag number: " + num);
        if (fieldsByName.containsKey(name))
            throw new ConfigError("Duplicate field name: " + name);
        fields.put(num, field);
        fieldsByName.put(name,field);
    }

    public Field findField(String name) {
        return fieldsByName.get(name);
    }

    void setHeader(FieldList header) {
        if (log.isDebugEnabled())
            log.debug("setHeader() : " + header);
        this.header = header;
    }

    void setTrailer(FieldList trailer) {
        if (log.isDebugEnabled())
            log.debug("setTrailer() : " + trailer);
        this.trailer = trailer;
    }

    public FieldList getHeader() {
        return header;
    }

    public class Field {
        private int num;
        private ValueType valueType;
        private Map<String, String> values;
        private String name;
        private boolean required;

        public Field(int num, String name, boolean required, ValueType valueType, Map<String, String> values) {
            this.num = num;
            this.valueType = valueType;
            this.name = name;
            this.required = required;
            this.values = values;
        }

        public Field(Field f, boolean required) {
            this(f.num,f.name,required,f.valueType,f.values);
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
            return "Field{" + name + "<" + num + ">" +
                    ", required=" + required +
                    ", valueType=" + valueType +
                    ", values=" + values +
                    '}';
        }
    }

    public class FieldList {
        private String name;
        private LinkedHashMap<Integer,Field> fields;
        private Set<Integer> requiredTags;

        public FieldList(String name,int initialSize) {
            this.name = name;
            this.requiredTags = new HashSet<Integer>(initialSize);
            fields = new LinkedHashMap<Integer,Field>(initialSize);
        }

        void addField(String name, boolean required) {
            Field f = findField(name);
            if (f == null)
                throw new ConfigError("No field '" + name + "'");
            if (required != f.required) {
                // Clone the field so we can override it's attributes.
                f = new Field(f,required);
            }
            Integer key = f.getNum();
            if (fields.containsKey(key))
                throw new ConfigError("Already contains tag " + key);
            fields.put(f.getNum(),f);
            if (f.isRequired()) {
                requiredTags.add(f.getNum());
            }
        }

        @Override
        public String toString() {
            return "FieldList{" +
                    "name=" + name +
                    ", fields=" + fields +
                    '}';
        }

        public Field find(int tag) {
            return fields.get(tag);
        }

        public Set<Integer> getRequiredTags() {
            return requiredTags;
        }

        public String getName() {
            return name;
        }
    }
    
}
