package server.database;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public abstract class DBMSAbstract {

    /**
     * The file name for storing the server.database
     */
    protected String filename;

    /**
     * The server.database table
     */
    protected Table table;

    /**
     * The primary key field within the table
     */
    protected String primarykey;

    /**
     * Connects to the server.database by reading the contents of the file into the table
     * @param filename Name of the server.database file
     * @throws FileNotFoundException Thrown if filename cannot be read
     */
    public abstract void connect(String filename) throws FileNotFoundException;

    /**
     * Writes the contents of the server.database to the file (stored in filename) and closes the server.database connection
     * @throws FileNotFoundException Thrown if filename cannot be written
     */
    public abstract void disconnect() throws FileNotFoundException;

    /**
     * Returns a reference to the server.database table
     * @return The table
     */
    public abstract Table getTable();

    /**
     * Selects records from the server.database
     * @param fieldspec "name = value" pairs for record to be selected
     * @return list of records whose fieldspec matches the input
     */
    public abstract ArrayList<Record> select(String fieldspec);

    /**
     * Insert a record into the server.database
     * @param args "name = value" pairs for record to be inserted
     * @throws IllegalArgumentException Thrown if primary key already exists in the server.database
     */
    public abstract void insert(String...args) throws IllegalArgumentException;

    /**
     * Delete records from the server.database
     * Returns a list of deleted records
     * @param fieldspec "name = value" pair for identifying the records to be deleted
     * @return list of deleted records that match the fieldspec
     */
    public abstract ArrayList<Record> delete(String fieldspec);

    /**
     * Checks to see if a record with the specified field spec is in the server.database
     * @param fieldspec "name = value" pair for identifying the records to be searched for
     * @return true if a record with the given fieldspec is found
     */
    public abstract boolean contains(String fieldspec);

    /**
     *
     * @param fieldspec "name = value" pair for identifying the records to be updated
     * @param newfieldspec "name = value" pair for the update
     * @return A list of the original (un-updated) records
     * @throws IllegalArgumentException Thrown if the primary key field is specified
     */
    public abstract ArrayList<Record> update(String fieldspec, String newfieldspec) throws IllegalArgumentException;
}
