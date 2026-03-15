package co.edu.eci.blueprints.dto;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import java.util.List;
import java.util.stream.Collectors;

public class BlueprintMapper {
    /**
     * Convierte una entidad Blueprint a su DTO correspondiente.
     * @param blueprint entidad Blueprint
     * @return BlueprintDTO equivalente
     */
    public static BlueprintDTO toDTO(Blueprint blueprint) {
        if (blueprint == null) return null;
        List<PointDTO> points = blueprint.getPoints().stream()
                .map(BlueprintMapper::toDTO)
                .collect(Collectors.toList());
        return new BlueprintDTO(
                blueprint.getId(),
                blueprint.getAuthor(),
                blueprint.getName(),
                points
        );
    }

    /**
     * Convierte un DTO BlueprintDTO a su entidad correspondiente.
     * @param dto BlueprintDTO
     * @return entidad Blueprint equivalente
     */
    public static Blueprint toEntity(BlueprintDTO dto) {
        if (dto == null) return null;
        List<Point> points = dto.getPoints().stream()
                .map(BlueprintMapper::toEntity)
                .collect(Collectors.toList());
        Blueprint blueprint = new Blueprint(dto.getAuthor(), dto.getName(), points);
        // El id solo se setea si es necesario (por ejemplo, para updates)
        return blueprint;
    }

    /**
     * Convierte una entidad Point a su DTO correspondiente.
     * @param point entidad Point
     * @return PointDTO equivalente
     */
    public static PointDTO toDTO(Point point) {
        if (point == null) return null;
        return new PointDTO(point.x(), point.y());
    }

    /**
     * Convierte un DTO PointDTO a su entidad correspondiente.
     * @param dto PointDTO
     * @return entidad Point equivalente
     */
    public static Point toEntity(PointDTO dto) {
        if (dto == null) return null;
        return new Point(dto.getX(), dto.getY());
    }
}
