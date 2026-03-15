package co.edu.eci.blueprints.persistence;

/**
 * Exception thrown to indicate an error occurred during blueprint persistence operations.
 * This exception is used to signal issues when saving, retrieving, or managing blueprints in the persistence layer.
 */
public class BlueprintPersistenceException extends Exception {

    /**
     * Constructs a new BlueprintPersistenceException with the specified detail message.
     * @param msg the detail message describing the cause of the exception
     */
    public BlueprintPersistenceException(String msg) { super(msg); }
}
