package co.edu.eci.blueprints.persistence;

import co.edu.eci.blueprints.model.Blueprint;
import java.util.Set;

/**
 * Interface for blueprint persistence operations.
 * Defines methods for saving, retrieving, and managing blueprints in the persistence layer.
 */
public interface BlueprintPersistence {

    /**
     * Saves a new blueprint to the persistence layer.
     * @param bp The blueprint to save
     * @throws BlueprintPersistenceException if a persistence error occurs
     */
    void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException;

    /**
     * Retrieves a blueprint by author and name.
     * @param author The author of the blueprint
     * @param name The name of the blueprint
     * @return The requested blueprint
     * @throws BlueprintNotFoundException if the blueprint is not found
     */
    Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException;

    /**
     * Retrieves all blueprints created by a specific author.
     * @param author The author's name
     * @return A set of blueprints by the author
     * @throws BlueprintNotFoundException if no blueprints are found for the author
     */
    Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException;

    /**
     * Retrieves all blueprints stored in the persistence layer.
     * @return A set of all blueprints
     */
    Set<Blueprint> getAllBlueprints();

    /**
     * Adds a new point to the specified blueprint.
     * @param author The author of the blueprint
     * @param name The name of the blueprint
     * @param x The x-coordinate of the new point
     * @param y The y-coordinate of the new point
     * @throws BlueprintNotFoundException if the blueprint is not found
     */
    void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException;

    /**
     * Updates an existing blueprint identified by author and name.
     * @param author Current blueprint author
     * @param name Current blueprint name
     * @param blueprint Updated blueprint data
     * @return The updated blueprint
     * @throws BlueprintNotFoundException if the blueprint is not found
     * @throws BlueprintPersistenceException if the target blueprint key already exists
     */
    Blueprint updateBlueprint(String author, String name, Blueprint blueprint)
            throws BlueprintNotFoundException, BlueprintPersistenceException;

    /**
     * Deletes a blueprint identified by author and name.
     * @param author The blueprint author
     * @param name The blueprint name
     * @throws BlueprintNotFoundException if the blueprint is not found
     */
    void deleteBlueprint(String author, String name) throws BlueprintNotFoundException;
}
