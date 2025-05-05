package server.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Database table
 */
public class Table {

    /**
     * The path to the server.database file
     */
    private String databasefile = "";

    /**
     * The list of records that comprise the server.database table
     */
    private ArrayList<Record> table;

    /**
     * The list of field names in a record
     */
    private ArrayList<String> fieldnames;

    /**
     * The list of data types in a record
     */
    private ArrayList<String> fieldtypes;

    /**
     * The name of the primary key
     */
    private String primarykey;

    /**
     * Constructor
     */
    public Table() {
        table = new ArrayList<Record>();
        fieldnames = new ArrayList<String>();
        fieldtypes = new ArrayList<String>();
    }

    /**
     * Gets the table
     * @return the table
     */
    public ArrayList<Record> getTable() {
        return table;
    }

    /**
     * Gets the field names
     * @return the field names
     */
    public ArrayList<String> getFieldNames() {
        return fieldnames;
    }

    /**
     * Gets the primary key field name
     * @return the primary key
     */
    public String getPrimaryKey() {
        return primarykey;
    }

    /**
     * Create a new table
     * @param fnames list of field names
     * @param ftypes list of field data types (all are String for now)
     * @param pkey the primary key field
     */
    public void setFieldNamesAndTypes (ArrayList<String> fnames, ArrayList<String> ftypes, String pkey) {

        // -- sort the field names (and their respective types) to place the primary key first
        ArrayList<String> sortedfnames = new ArrayList<String>();
        ArrayList<String> sortedtypes = new ArrayList<String>();
        // -- add the primary key first
        for (int i = 0; i < fnames.size(); ++i) {
            if (fnames.get(i).trim().equals(pkey.trim())) {
                sortedfnames.add(fnames.get(i).trim());
                sortedtypes.add(ftypes.get(i).trim());
                break;
            }
        }
        // -- add all the non-primary keys after
        for (int i = 0; i < fnames.size(); ++i) {
            if (!fnames.get(i).equals(pkey)) {
                sortedfnames.add(sortedfnames.size(), fnames.get(i).trim());
                sortedtypes.add(sortedtypes.size(), ftypes.get(i).trim());
            }
        }
        fieldnames = sortedfnames;
        fieldtypes = sortedtypes;
        this.primarykey = pkey;
    }


    /**
     * Get the primary key value from a record
     * @param r The record
     * @return The primary key value
     * @throws IllegalArgumentException Thrown if the primary key does not exist in the record
     */
    private String getPrimaryKey(Record r) throws IllegalArgumentException {
        // -- get primary key value from record being added
        for (Field f : r.getFields()) {
            if (f.getKey().equals(primarykey)) {
                return f.getValue();
            }
        }
        throw new IllegalArgumentException("primary key not found");
    }

    /**
     * Add a record to the table
     * @param r The record to be added
     * @throws IllegalArgumentException Thrown if the number of fields does not match the table or if the primary key is duplicated
     */
    public void addRecord(Record r) throws IllegalArgumentException {

        System.out.println( "REGISTER: "+r );

        if (r.getNFields() != fieldnames.size()) {
            throw new IllegalArgumentException("mismatched number of fields");
        }

        // -- get primary key value from record being added
        String pkeyval = r.getValue(primarykey);

        // -- make sure the primary key value is not duplicated in the table
        for (Record rec : table) {
            String reckey = getPrimaryKey(rec);
            String reckeyval = rec.getValue(primarykey);
            if (reckeyval.equals(pkeyval)) {
                throw new IllegalArgumentException("duplicate primary key value");
            }
        }
        // -- to make sure the record fields are in the same order as the field names and types
        r.reorder(fieldnames);
        this.table.add(r);

    }

    /**
     * Write the server.database table to a CSV file
     * @param filename The name of the file to be written
     * @throws FileNotFoundException Thrown if the file cannot be written
     */
    public void writeToFile(String filename) throws FileNotFoundException {
        try {
            PrintWriter file = new PrintWriter(new File(filename));
            for (String s : fieldnames) {
                file.print(s + ",");
            }
            file.println();
            for (String s : fieldtypes) {
                file.print(s + ",");
            }
            file.println();
            for (Record rec : table) {
                for (Field field : rec.getFields()) {
                    file.print(field.getValue() + ",");
                }
                file.println();
            }
            file.close();
        }
        catch (FileNotFoundException e) {
            throw e;
        }
    }

    /**
     * Read the server.database table from a CSV file
     * @param filename The name of the file
     * @throws FileNotFoundException Thrown if the file cannot be read
     */
    public void readFromFile(String filename) throws FileNotFoundException {
        try {
            Scanner file = new Scanner(new File(filename));

            // -- read the field names
            ArrayList<String> fnames = new ArrayList<String>();
            String s = file.nextLine();
            // -- strip trailing comma if it exists
            if (s.trim().charAt(s.length() - 1) == ',') {
                s = s.substring(0, s.lastIndexOf(','));
            }
            String[] ss = s.split(",");
            for (String fname : ss) {
                fnames.add(fnames.size(), fname.trim());
            }
            // -- primary key is the first field listed
            this.primarykey = ss[0].trim();
            this.fieldnames = fnames;


            // -- read the field data types
            ArrayList<String> ftypes = new ArrayList<String>();
            s = file.nextLine();
            // -- strip trailing comma if it exists
            if (s.trim().charAt(s.length() - 1) == ',') {
                s = s.substring(0, s.lastIndexOf(','));
            }

            ss = s.split(",");
            for (String ftype : ss) {
                ftypes.add(ftypes.size(), ftype.trim());
            }
            this.fieldtypes = ftypes;

            // -- read the records
            ArrayList<Record> tbl = new ArrayList<Record>();
            while (file.hasNext()) {
                s = file.nextLine();
                // -- strip trailing comma if it exists
                if (s.trim().charAt(s.length() - 1) == ',') {
                    s = s.substring(0, s.lastIndexOf(','));
                }
                ss = s.split(",",-1);
                Record rec = new Record();
                for (int i = 0; i < fnames.size(); ++i) {
                    rec.addField(new Field(fnames.get(i), ss[i].trim()));
                }
                tbl.add(rec);
            }
            this.table = tbl;

            file.close();
        }
        catch (FileNotFoundException e) {
            throw e;
        }
    }


    @Override
    public String toString() {
        String s = "[\n";
        String sf = "\t[FIELDS: ";
        for (String ss : this.fieldnames) {
            if (ss.equals(primarykey)) {
                sf += "*";
            }
            sf += ss + ",";
        }
        sf += "]";
        s += sf + "\n";

        String st = "\t[TYPES: ";
        for (String ss : this.fieldtypes) {
            st += ss + ",";
        }
        st += "]";
        s += st + "\n";

        s += "\tRECORDS: \n";
        for (Record rec : table) {
            s += "\t" + rec;
            s += "\n";
        }
        s += "\n";
        s += "]\n";
        return s;
    }

}