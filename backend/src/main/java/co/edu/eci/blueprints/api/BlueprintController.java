package co.edu.eci.blueprints.api;
import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.dto.BlueprintDTO;
import co.edu.eci.blueprints.dto.BlueprintMapper;
import co.edu.eci.blueprints.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.persistence.BlueprintPersistenceException;
import co.edu.eci.blueprints.services.BlueprintsServices;
import java.util.Set;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import co.edu.eci.blueprints.dto.PointDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/blueprints")
@CrossOrigin(origins = "http://localhost:5173")
public class BlueprintController {

    private final BlueprintsServices services;

    public BlueprintController(BlueprintsServices services) {
        this.services = services;
    }

    /**
     * GET /api/blueprints?author=:author
     * Obtiene todos los blueprints de un autor.
     * @param author Nombre del autor
     * @return ApiResp con lista de blueprints
     */
    @Operation(summary = "Obtener blueprints por autor", description = "Devuelve todos los blueprints de un autor especificado. Códigos de respuesta: 200 (éxito), 404 (no encontrado)")
    @GetMapping(params = "author")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de blueprints obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron blueprints para el autor")
        })
    public ResponseEntity<ApiResp<Set<BlueprintDTO>>> getByAuthor(@RequestParam String author) {
        try {
            Set<Blueprint> data = services.getBlueprintsByAuthor(author);
            Set<BlueprintDTO> dtoSet = data.stream().map(BlueprintMapper::toDTO).collect(java.util.stream.Collectors.toSet());
            return buildResponse(200, "Success", dtoSet);
        } catch (BlueprintNotFoundException e) {
            return buildResponse(404, e.getMessage(), null);
        }
    }

    /**
     * GET /api/blueprints/{author}/{name}
     * Obtiene un blueprint específico por autor y nombre.
     * @param author Nombre del autor
     * @param name Nombre del blueprint
     * @return ApiResp con el blueprint
     */
    @Operation(summary = "Obtener blueprint por autor y nombre", description = "Devuelve un blueprint específico por autor y nombre. Códigos de respuesta: 200 (éxito), 404 (no encontrado)")
    @GetMapping("/{author}/{name}")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Blueprint obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el blueprint solicitado")
        })
    public ResponseEntity<ApiResp<BlueprintDTO>> getByAuthorAndName(@PathVariable String author, @PathVariable String name) {
        try {
            Blueprint data = services.getBlueprint(author, name);
            BlueprintDTO dto = BlueprintMapper.toDTO(data);
            return buildResponse(200, "Success", dto);
        } catch (BlueprintNotFoundException e) {
            return buildResponse(404, e.getMessage(), null);
        }
    }

    /**
     * POST /api/blueprints
     * Crea un nuevo blueprint.
     * @param req Datos del blueprint
     * @return ApiResp con estado de creación
     */
    @Operation(summary = "Crear blueprint", description = "Crea un nuevo blueprint con los datos proporcionados. Códigos de respuesta: 201 (creado), 400 (solicitud inválida)")
    @PostMapping
        @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Blueprint creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
        })
    public ResponseEntity<ApiResp<Void>> add(@Valid @RequestBody NewBlueprintRequest req) {
        try {
            List<PointDTO> points = req.points();
            Blueprint bp = new Blueprint(req.author(), req.name(), points.stream().map(BlueprintMapper::toEntity).toList());
            services.addNewBlueprint(bp);
            return buildResponse(201, "Created", null);
        } catch (BlueprintPersistenceException e) {
            return buildResponse(400, e.getMessage(), null);
        }
    }

    /**
     * PUT /api/blueprints/{author}/{name}
     * Actualiza un blueprint existente.
     * @param author Nombre del autor
     * @param name Nombre del blueprint
     * @param req Datos actualizados
     * @return ApiResp con el blueprint actualizado
     */
    @Operation(summary = "Actualizar blueprint", description = "Actualiza un blueprint existente con los datos proporcionados. Códigos de respuesta: 200 (éxito), 400 (solicitud inválida), 404 (no encontrado), 409 (conflicto)")
    @PutMapping("/{author}/{name}")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Blueprint actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida: el campo 'points' no puede ser nulo"),
            @ApiResponse(responseCode = "404", description = "No se encontró el blueprint solicitado"),
            @ApiResponse(responseCode = "409", description = "Error de persistencia"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    public ResponseEntity<ApiResp<BlueprintDTO>> update(@PathVariable String author, @PathVariable String name,@Valid @RequestBody UpdateBlueprintRequest req) {
        if (req.points() == null) {
            return buildResponse(400, "Solicitud inválida: el campo 'points' no puede ser nulo", null);
        }
        try {
            List<PointDTO> points = req.points();
            Blueprint updatedBlueprint = new Blueprint(req.author(), req.name(), points.stream().map(BlueprintMapper::toEntity).toList());
            Blueprint updated = services.updateBlueprint(author, name, updatedBlueprint);
            return buildResponse(200, "Blueprint actualizado correctamente", BlueprintMapper.toDTO(updated));
        } catch (BlueprintNotFoundException e) {
            return buildResponse(404, "No se encontró el blueprint solicitado: " + e.getMessage(), null);
        } catch (BlueprintPersistenceException e) {
            return buildResponse(409, "Error de persistencia: " + e.getMessage(), null);
        } catch (Exception e) {
            return buildResponse(500, "Error interno del servidor: " + e.getMessage(), null);
        }
    }

    /**
     * DELETE /api/blueprints/{author}/{name}
     * Elimina un blueprint existente.
     * @param author Nombre del autor
     * @param name Nombre del blueprint
     * @return ApiResp con estado de eliminación
     */
    @Operation(summary = "Eliminar blueprint", description = "Elimina un blueprint existente por autor y nombre. Códigos de respuesta: 200 (éxito), 404 (no encontrado)")
    @DeleteMapping("/{author}/{name}")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Blueprint eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el blueprint solicitado")
        })
    public ResponseEntity<ApiResp<Void>> delete(@PathVariable String author, @PathVariable String name) {
        try {
            services.deleteBlueprint(author, name);
            return buildResponse(200, "Deleted", null);
        } catch (BlueprintNotFoundException e) {
            return buildResponse(404, e.getMessage(), null);
        }
    }
    private <T> ResponseEntity<ApiResp<T>> buildResponse(int code, String message, T data) {
        return ResponseEntity.status(code == 200 ? HttpStatus.OK :
                                     code == 201 ? HttpStatus.CREATED :
                                     code == 400 ? HttpStatus.BAD_REQUEST :
                                     code == 404 ? HttpStatus.NOT_FOUND :
                                     code == 409 ? HttpStatus.CONFLICT : HttpStatus.OK)
                .body(new ApiResp<>(code, message, data));
    }

    public record NewBlueprintRequest(@NotBlank String author,@NotBlank String name,@Valid java.util.List<PointDTO> points
    ) { }

    public record UpdateBlueprintRequest(@NotBlank String author,@NotBlank String name,@Valid java.util.List<PointDTO> points
    ) { }
}

