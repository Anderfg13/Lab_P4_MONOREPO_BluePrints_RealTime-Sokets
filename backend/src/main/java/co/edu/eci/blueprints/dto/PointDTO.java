package co.edu.eci.blueprints.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Data Transfer Object for Point entity.
 */
public class PointDTO {
    @Min(value = 0, message = "x must be >= 0")
    @Max(value = 600, message = "x must be <= 600")
    private int x;

    @Min(value = 0, message = "y must be >= 0")
    @Max(value = 400, message = "y must be <= 400")
    private int y;

    /**
     * Constructor vacío requerido para serialización/deserialización.
     */
    public PointDTO() {}

    /**
     * Constructor para inicializar las coordenadas del punto.
     * @param x coordenada X
     * @param y coordenada Y
     */
    public PointDTO(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
