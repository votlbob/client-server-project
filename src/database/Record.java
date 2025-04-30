package database;

import java.util.ArrayList;

/**
 * Table record
 */
public class Record {

    /**
     * List of key/value pairs (fields) that comprise the record
     */
    private ArrayList<Field> fields;

    /**
     * Constructor
     */
    public Record() {
        fields = new ArrayList<Field>();
    }

    /**
     * Get the list of fields
     * @return the list of fields
     */
    public ArrayList<Field> getFields() {
        return fields;
    }

    /**
     * Get the number of fields in the record
     * @return the number of fields
     */
    public int getNFields() {
        return fields.size();
    }

    /**
     * Get the value for a specified key
     * @param key the key whose value is to be gotten
     * @return the value
     * @throws IllegalArgumentException Thrown if the key does not exist
     */
    public String getValue(String key) throws IllegalArgumentException  {
        for (Field f : fields) {
            if (f.getKey().trim().equals(key)) {
                return f.getValue();
            }
        }
        throw new IllegalArgumentException("key not found");
    }

    /**
     * Set the value for a specified key
     * @param key the key whose value is to be set
     * @param value the new value
     * @throws IllegalArgumentException Thrown if the key does not exist
     */
    public void setValue(String key, String value) throws IllegalArgumentException {
        for (Field f : fields) {
            if (f.getKey().trim().equals(key)) {
                f.setValue(value);
                return;
            }
        }
        throw new IllegalArgumentException("key not found");
    }
    /**
     * Add a field to the record
     * @param addfield the key/value pair to be added
     * @throws IllegalArgumentException Thrown if the key already exists in the record
     */
    public void addField(Field addfield) throws IllegalArgumentException {
        for (Field f : fields) {
            if (f.getKey().equals(addfield.getKey())) {
                throw new IllegalArgumentException("duplicate key");
            }
        }
        fields.add(addfield);

    }

    /**
     * Reorder the fields
     * @param fieldnames The new order of the fields
     */
    public void reorder(ArrayList<String> fieldnames) {
        ArrayList<Field> newfields = new ArrayList<Field>();
        for (String fs : fieldnames) {
            for (Field f : this.fields) {
                if (f.getKey().trim().equals(fs.trim())) {
                    newfields.add(newfields.size(), new Field(f.getKey(), f.getValue()));
                }
            }
        }
        this.fields = newfields;
    }

    @Override
    public String toString() {
        String s = "[";

        for (Field f : fields) {
            s += f + ", ";
        }
        s += "]";
        return s;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Record)) {
            return false;
        }
        Record r = (Record)obj;
        if (this.fields.size() != r.fields.size()) {
            return false;
        }
        for (int i = 0; i < this.fields.size(); ++i) {
            if (!(this.fields.get(i).equals(r.fields.get(i)))) {
                return false;
            }
        }
        return true;
    }


}
