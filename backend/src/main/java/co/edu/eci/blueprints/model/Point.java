package co.edu.eci.blueprints.model;
import jakarta.persistence.Embeddable;

/**
 * Represents a point in a 2D coordinate system.
 * Each point has an x (horizontal) and y (vertical) value.
 * This class is immutable and uses Java's record feature for simplicity.
 *
 * @param x The x-coordinate of the point
 * @param y The y-coordinate of the point
 */
@Embeddable
public record Point(int x, int y) { }
