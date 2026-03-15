package co.edu.eci.blueprints.persistence;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
/**
 * In-memory implementation of the BlueprintPersistence interface.
 * Stores blueprints in a thread-safe map and provides methods to manage them.
 * This implementation is mainly for testing or demonstration purposes and does not persist data across application restarts.
 */
public class InMemoryBlueprintPersistence implements BlueprintPersistence {

    /**
     * Thread-safe map storing blueprints using a composite key of author and name.
     */
    private final Map<String, Blueprint> blueprints = new ConcurrentHashMap<>();

    /**
     * Initializes the in-memory persistence with some sample blueprints for demonstration.
     */
    public InMemoryBlueprintPersistence() {
        // Sample data 1:1 style (author/name key)
        Blueprint bp1 = new Blueprint("john", "house",
                List.of(new Point(0,0), new Point(10,0), new Point(10,10), new Point(0,10)));
        Blueprint bp2 = new Blueprint("john", "garage",
                List.of(new Point(5,5), new Point(15,5), new Point(15,15)));
        Blueprint bp3 = new Blueprint("jane", "garden",
                List.of(new Point(2,2), new Point(3,4), new Point(6,7)));
        blueprints.put(keyOf(bp1), bp1);
        blueprints.put(keyOf(bp2), bp2);
        blueprints.put(keyOf(bp3), bp3);
    }

    /**
     * Generates a unique key for a blueprint using its author and name.
     * @param bp The blueprint
     * @return The composite key in the format "author:name"
     */
    private String keyOf(Blueprint bp) { return bp.getAuthor() + ":" + bp.getName(); }

    /**
     * Generates a unique key from author and name.
     * @param author The author's name
     * @param name The blueprint's name
     * @return The composite key in the format "author:name"
     */
    private String keyOf(String author, String name) { return author + ":" + name; }

    /**
     * Saves a new blueprint to the in-memory store.
     * @param bp The blueprint to save
     * @throws BlueprintPersistenceException if a blueprint with the same author and name already exists
     */
    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        String k = keyOf(bp);
        if (blueprints.containsKey(k)) throw new BlueprintPersistenceException("Blueprint already exists: " + k);
        blueprints.put(k, bp);
    }

    /**
     * Retrieves a blueprint by author and name.
     * @param author The author of the blueprint
     * @param name The name of the blueprint
     * @return The requested blueprint
     * @throws BlueprintNotFoundException if the blueprint is not found
     */
    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        Blueprint bp = blueprints.get(keyOf(author, name));
        if (bp == null) throw new BlueprintNotFoundException("Blueprint not found: %s/%s".formatted(author, name));
        return bp;
    }

    /**
     * Retrieves all blueprints created by a specific author.
     * @param author The author's name
     * @return A set of blueprints by the author
     * @throws BlueprintNotFoundException if no blueprints are found for the author
     */
    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        Set<Blueprint> set = blueprints.values().stream()
                .filter(bp -> bp.getAuthor().equals(author))
                .collect(Collectors.toSet());
        if (set.isEmpty()) throw new BlueprintNotFoundException("No blueprints for author: " + author);
        return set;
    }

    /**
     * Retrieves all blueprints stored in memory.
     * @return A set of all blueprints
     */
    @Override
    public Set<Blueprint> getAllBlueprints() {
        return new HashSet<>(blueprints.values());
    }

    /**
     * Adds a new point to the specified blueprint.
     * @param author The author of the blueprint
     * @param name The name of the blueprint
     * @param x The x-coordinate of the new point
     * @param y The y-coordinate of the new point
     * @throws BlueprintNotFoundException if the blueprint is not found
     */
    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        Blueprint bp = getBlueprint(author, name);
        bp.addPoint(new Point(x, y));
    }

    @Override
    public Blueprint updateBlueprint(String author, String name, Blueprint blueprint)
            throws BlueprintNotFoundException, BlueprintPersistenceException {
        Blueprint existing = getBlueprint(author, name);
        String currentKey = keyOf(author, name);
        String targetKey = keyOf(blueprint);

        if (!currentKey.equals(targetKey) && blueprints.containsKey(targetKey)) {
            throw new BlueprintPersistenceException("Blueprint already exists: " + targetKey);
        }

        blueprints.remove(currentKey);
        existing.setAuthor(blueprint.getAuthor());
        existing.setName(blueprint.getName());
        existing.replacePoints(blueprint.getPoints());
        blueprints.put(keyOf(existing), existing);
        return existing;
    }

    @Override
    public void deleteBlueprint(String author, String name) throws BlueprintNotFoundException {
        String key = keyOf(author, name);
        Blueprint removed = blueprints.remove(key);
        if (removed == null) {
            throw new BlueprintNotFoundException("Blueprint not found: %s/%s".formatted(author, name));
        }
    }
}
