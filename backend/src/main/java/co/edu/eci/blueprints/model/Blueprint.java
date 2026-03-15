package co.edu.eci.blueprints.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a blueprint composed of an author, a name, and a list of points.
 * Allows access and modification of blueprint data, as well as comparison by author and name.
 * This class is mapped as a JPA entity for database persistence.
 */
@Entity
@Table(name = "blueprints")
public class Blueprint {

    /**
     * Primary key for the blueprint entity.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the blueprint's author.
     */
    @Column(nullable = false)
    private String author;

    /**
     * Name of the blueprint.
     */
    @Column(nullable = false)
    private String name;

    /**
     * List of points that make up the blueprint.
     * Stored as an element collection in the database.
     */
    @ElementCollection
    private List<Point> points = new ArrayList<>();

    /**
     * Default constructor required by JPA.
     */
    public Blueprint() {}

    /**
     * Creates a new blueprint with the specified author, name, and list of points.
     * @param author Name of the author
     * @param name Name of the blueprint
     * @param pts List of points that make up the blueprint
     */
    public Blueprint(String author, String name, List<Point> pts) {
        this.author = author;
        this.name = name;
        if (pts != null) points.addAll(pts);
    }

    /**
     * Gets the primary key of the blueprint.
     * @return Blueprint id
     */
    public Long getId() { return id; }

    /**
     * Gets the name of the blueprint's author.
     * @return Author's name
     */
    public String getAuthor() { return author; }

    /**
     * Updates the author of the blueprint.
     * @param author New author name
     */
    public void setAuthor(String author) { this.author = author; }

    /**
     * Gets the name of the blueprint.
     * @return Blueprint name
     */
    public String getName() { return name; }

    /**
     * Updates the blueprint's name.
     * @param name New blueprint name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Gets the list of points that make up the blueprint.
     * @return List of points
     */
    public List<Point> getPoints() { return Collections.unmodifiableList(points); }

    /**
     * Adds a point to the blueprint's list of points.
     * @param p Point to add
     */
    public void addPoint(Point p) { points.add(p); }

    /**
     * Replaces the current points with a new list.
     * @param updatedPoints New points to assign
     */
    public void replacePoints(List<Point> updatedPoints) {
        points.clear();
        if (updatedPoints != null) {
            points.addAll(updatedPoints);
        }
    }

    /**
     * Compares this blueprint with another object to determine if they are equal.
     * Two blueprints are equal if they have the same author and name.
     * @param o Object to compare
     * @return true if both blueprints have the same author and name, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Blueprint bp)) return false;
        return Objects.equals(author, bp.author) && Objects.equals(name, bp.name);
    }

    /**
     * Generates the hash code for the blueprint based on the author and name.
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(author, name);
    }
}
