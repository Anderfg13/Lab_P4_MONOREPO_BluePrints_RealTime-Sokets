package co.edu.eci.blueprints.persistence;

/**
 * Exception thrown to indicate that a requested blueprint was not found in the persistence layer.
 * This exception is used when an operation attempts to retrieve a blueprint that does not exist.
 */
public class BlueprintNotFoundException extends Exception {

    /**
     * Constructs a new BlueprintNotFoundException with the specified detail message.
     * @param msg the detail message describing the cause of the exception
     */
    public BlueprintNotFoundException(String msg) { super(msg); }
}
