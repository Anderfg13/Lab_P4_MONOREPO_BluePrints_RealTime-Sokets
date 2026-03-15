package co.edu.eci.blueprints.dto;

import java.util.List;

/**
 * Data Transfer Object for Blueprint entity.
 */
public class BlueprintDTO {
    private Long id;
    private String author;
    private String name;
    private List<PointDTO> points;

    /**
     * Constructor vacío requerido para serialización/deserialización.
     */
    public BlueprintDTO() {}

    /**
     * Constructor completo para inicializar todos los campos del DTO.
     * @param id identificador único del blueprint
     * @param author nombre del autor
     * @param name nombre del blueprint
     * @param points lista de puntos del blueprint
     */
    public BlueprintDTO(Long id, String author, String name, List<PointDTO> points) {
        this.id = id;
        this.author = author;
        this.name = name;
        this.points = points;
    }

    /**
     * Obtiene el identificador del blueprint.
     * @return id del blueprint
     */
    public Long getId() { return id; }

    /**
     * Establece el identificador del blueprint.
     * @param id identificador a asignar
     */
    public void setId(Long id) { this.id = id; }

    /**
     * Obtiene el nombre del autor.
     * @return nombre del autor
     */
    public String getAuthor() { return author; }

    /**
     * Establece el nombre del autor.
     * @param author nombre del autor
     */
    public void setAuthor(String author) { this.author = author; }

    /**
     * Obtiene el nombre del blueprint.
     * @return nombre del blueprint
     */
    public String getName() { return name; }

    /**
     * Establece el nombre del blueprint.
     * @param name nombre del blueprint
     */
    public void setName(String name) { this.name = name; }

    /**
     * Obtiene la lista de puntos del blueprint.
     * @return lista de PointDTO
     */
    public List<PointDTO> getPoints() { return points; }

    /**
     * Establece la lista de puntos del blueprint.
     * @param points lista de PointDTO
     */
    public void setPoints(List<PointDTO> points) { this.points = points; }
}
