package org.yajul.fix.dictionary;

import org.yajul.fix.ValueType;

import java.util.Map;
import java.util.HashMap;

/**
 * A FIX dictionary.  Field, message, and component definitions.
 * <br>
 * User: josh
 * Date: Jul 29, 2009
 * Time: 9:29:47 AM
 */
public class Dictionary {
    public static final String ANY_VALUE = "__ANY__";

    private String version;
    private Map<Integer,Field> fields = new HashMap<Integer,Field>();

    public Dictionary(String version) {
        this.version = version;
    }

    void addField(int num, ValueType valueType, Map<String, String> values) {
        fields.put(num,new Field(num,valueType,values));
    }

    public class Field {
        private int num;
        private ValueType valueType;
        private Map<String, String> values;

        private Field(int num, ValueType valueType, Map<String, String> values) {
            this.num = num;
            this.valueType = valueType;
            this.values = values;
        }

        public int getNum() {
            return num;
        }

        public ValueType getValueType() {
            return valueType;
        }
        
    }
    
}
