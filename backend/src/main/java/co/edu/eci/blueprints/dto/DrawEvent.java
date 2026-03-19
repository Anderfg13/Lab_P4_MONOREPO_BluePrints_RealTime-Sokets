package co.edu.eci.blueprints.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DrawEvent(
	@NotBlank(message = "author is required") String author,
	@NotBlank(message = "name is required") String name,
	@NotNull(message = "point is required") @Valid PointDTO point
) {}
