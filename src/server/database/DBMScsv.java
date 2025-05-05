package server.database;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class DBMScsv extends DBMSAbstract {


    String filename;


    public void refresh() {
        try {
            disconnect();
            connect(filename);
        } catch( Exception e ) { e.printStackTrace(); }
    }
    @Override
    public void connect(String filename) throws FileNotFoundException {

        this.filename = filename;

        try {
            // -- server.database table will be held entirely in RAM
            table = new Table();
            table.readFromFile(filename);
            this.filename = filename;
        }
        catch (FileNotFoundException e) {
            throw e;
        }
    }
    @Override
    public void disconnect() throws FileNotFoundException {
        if (table != null) {
            // -- write RAM based table to file
            try {
                table.writeToFile(filename);
            } catch (FileNotFoundException e) {
                throw e;
            }
        }
    }


    @Override
    public boolean contains(String fieldspec) {
        ArrayList<Record> contains = this.select(fieldspec);

        return contains.size() > 0;
    }

    @Override
    public ArrayList<Record> select(String fieldspec) {
        ArrayList<Record> selections = new ArrayList<Record>();
        // -- get the column label and new value
        String[] fields = fieldspec.split("=", -1 );
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = fields[i].trim();
        }

        // -- find all records whose value matches that of the key
        for (Record r : table.getTable()) {
            String value = r.getValue(fields[0]);
            if (value.equals(fields[1])) {
                selections.add(r);
            }
        }
        return selections;
    }
    public ArrayList<Record> selectInverse(String fieldspec) {
        ArrayList<Record> selections = new ArrayList<Record>();
        // -- get the column label and new value
        String[] fields = fieldspec.split("=", -1 );
        for (int i = 0; i < fields.length; ++i) {
            fields[i] = fields[i].trim();
        }

        // -- find all records whose value matches that of the key
        for (Record r : table.getTable()) {
            String value = r.getValue(fields[0]);
            if (!value.equals(fields[1])) {
                selections.add(r);
            }
        }
        return selections;
    }

    @Override
    public void insert(String... args) throws IllegalArgumentException {
        // -- varargs are used the same as arrays
        Record r = new Record();

        for (int i = 0; i < args.length; ++i) {
            String[] pieces = args[i].split("=", -1 );
            for (int j = 0; j < pieces.length; ++j) {
                pieces[j] = pieces[j].trim();
            }
            Field f = new Field(pieces[0], pieces[1]);
            r.addField(f);
        }
        Field emptyIP = new Field( "IP", "" );
        r.addField( emptyIP );

        try {
            table.addRecord(r);
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        refresh();
    }
    @Override
    public ArrayList<Record> delete(String fieldspec) {
        ArrayList<Record> deletions = this.select(fieldspec);

        for (Record r : deletions) {
            for (int i = 0; i < this.table.getTable().size(); ++i) {
                if (r.equals(this.table.getTable().get(i))) {
                    this.table.getTable().remove(i);
                    break;
                }
            }
        }

        refresh();
        return deletions;
    }
    @Override
    public ArrayList<Record> update(String fieldspec, String newfieldspec) throws IllegalArgumentException {
        ArrayList<Record> updates = this.select(fieldspec);
        String[] newfields = newfieldspec.split("=", -1 );
        String updatekey = newfields[0].trim();
        String updatevalue = newfields[1].trim();
        if (updatekey.equals(table.getPrimaryKey())) {
            throw new IllegalArgumentException("primary key value cannot be updated");
        }

        for (Record r : updates) {
            for (int i = 0; i < this.table.getTable().size(); ++i) {
                if (r.equals(this.table.getTable().get(i))) {
                    Record up = this.table.getTable().get(i);
                    up.setValue(updatekey, updatevalue);
                }
            }
        }

        refresh();
        return updates;

    }


    @Override
    public Table getTable() {
        return table;
    }
    @Override
    public String toString() {
        return table.toString();
    }




}