package co.edu.eci.blueprints.persistence;

import co.edu.eci.blueprints.model.Blueprint;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * PostgreSQL implementation of BlueprintPersistence using Spring Data JPA.
 * Replaces the in-memory version and persists blueprints in the database.
 */
@Primary
@Repository
public class PostgresBlueprintPersistence implements BlueprintPersistence {

    @Autowired
    private EntityManager entityManager;

    private boolean existsBlueprint(String author, String name) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM Blueprint b WHERE b.author = :author AND b.name = :name", Long.class);
        query.setParameter("author", author);
        query.setParameter("name", name);
        return query.getSingleResult() > 0;
    }

    @Override
    public void saveBlueprint(Blueprint bp) throws BlueprintPersistenceException {
        try {
            if (existsBlueprint(bp.getAuthor(), bp.getName())) {
                throw new BlueprintPersistenceException("Blueprint already exists: " + bp.getAuthor() + "/" + bp.getName());
            }
            entityManager.persist(bp);
        } catch (BlueprintPersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new BlueprintPersistenceException("Error saving blueprint: " + e.getMessage());
        }
    }

    @Override
    public Blueprint getBlueprint(String author, String name) throws BlueprintNotFoundException {
        TypedQuery<Blueprint> query = entityManager.createQuery(
            "SELECT b FROM Blueprint b WHERE b.author = :author AND b.name = :name", Blueprint.class);
        query.setParameter("author", author);
        query.setParameter("name", name);
        List<Blueprint> result = query.getResultList();
        if (result.isEmpty()) {
            throw new BlueprintNotFoundException("Blueprint not found: " + author + "/" + name);
        }
        return result.get(0);
    }

    @Override
    public Set<Blueprint> getBlueprintsByAuthor(String author) throws BlueprintNotFoundException {
        TypedQuery<Blueprint> query = entityManager.createQuery(
            "SELECT b FROM Blueprint b WHERE b.author = :author", Blueprint.class);
        query.setParameter("author", author);
        List<Blueprint> result = query.getResultList();
        if (result.isEmpty()) {
            throw new BlueprintNotFoundException("No blueprints for author: " + author);
        }
        return new HashSet<>(result);
    }

    @Override
    public Set<Blueprint> getAllBlueprints() {
        TypedQuery<Blueprint> query = entityManager.createQuery(
            "SELECT b FROM Blueprint b", Blueprint.class);
        return new HashSet<>(query.getResultList());
    }

    @Override
    public void addPoint(String author, String name, int x, int y) throws BlueprintNotFoundException {
        Blueprint bp = getBlueprint(author, name);
        bp.addPoint(new co.edu.eci.blueprints.model.Point(x, y));
        entityManager.merge(bp);
    }

    @Override
    public Blueprint updateBlueprint(String author, String name, Blueprint blueprint)
            throws BlueprintNotFoundException, BlueprintPersistenceException {
        Blueprint existing = getBlueprint(author, name);
        boolean changingKey = !existing.getAuthor().equals(blueprint.getAuthor())
                || !existing.getName().equals(blueprint.getName());

        if (changingKey && existsBlueprint(blueprint.getAuthor(), blueprint.getName())) {
            throw new BlueprintPersistenceException(
                "Blueprint already exists: " + blueprint.getAuthor() + ":" + blueprint.getName());
        }

        existing.setAuthor(blueprint.getAuthor());
        existing.setName(blueprint.getName());
        existing.replacePoints(blueprint.getPoints());
        return entityManager.merge(existing);
    }

    @Override
    public void deleteBlueprint(String author, String name) throws BlueprintNotFoundException {
        Blueprint existing = getBlueprint(author, name);
        entityManager.remove(entityManager.contains(existing) ? existing : entityManager.merge(existing));
    }
}
