package co.edu.eci.blueprints.dto;

import java.util.List;

public record BlueprintUpdate(String author, String name, List<PointDTO> points) {}
