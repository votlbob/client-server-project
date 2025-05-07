package server.database;

/**
 * Record field
 */
public class Field {

    /**
     * field name (key)
     */
    private volatile String key;

    /**
     * field value
     */
    private volatile String value;

    /**
     * Constructor
     * @param key the field name (key)
     * @param value the field value
     */
    public Field(String key, String value) {
        super();
        this.key = key;
        this.value = value;
    }

    /**
     * Get the field name (key)
     * @return the field name
     */
    public String getKey() {
        return key;
    }

    /**
     * Set the field name (key)
     * @param key the new field name (key)
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Get the value
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * St the value
     * @param value the value to be set
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Field) {
            Field fobj = (Field)obj;
            return this.key.equals(fobj.key) &&
                    this.value.equals(fobj.value);
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "[" + this.key + " : " + this.value + "]";
    }


}