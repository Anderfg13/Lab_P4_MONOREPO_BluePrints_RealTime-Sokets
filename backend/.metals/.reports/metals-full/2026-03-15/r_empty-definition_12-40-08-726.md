error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java:_empty_/Operation#description#
file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java
empty definition using pc, found symbol in pc: _empty_/Operation#description#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1452
uri: file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java
text:
```scala
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

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
     * @return ApiResponse con lista de blueprints
     */
    @Operation(
        summary = "Obtener blueprints por autor",
        @@description = "Devuelve todos los blueprints de un autor especificado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "No se encontraron blueprints para el autor")
    })
    @GetMapping(params = "author")
    public ResponseEntity<ApiResponse<Set<BlueprintDTO>>> getByAuthor(@RequestParam String author) {
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
     * @return ApiResponse con el blueprint
     */
    @Operation(
        summary = "Obtener blueprint por autor y nombre",
        description = "Devuelve un blueprint específico por autor y nombre.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blueprint obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    @GetMapping("/{author}/{name}")
    public ResponseEntity<ApiResponse<BlueprintDTO>> getByAuthorAndName(@PathVariable String author, @PathVariable String name) {
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
     * @return ApiResponse con estado de creación
     */
    @Operation(
        summary = "Crear blueprint",
        description = "Crea un nuevo blueprint con los datos proporcionados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Blueprint creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> add(@Valid @RequestBody NewBlueprintRequest req) {
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
     * @return ApiResponse con el blueprint actualizado
     */
    @Operation(
        summary = "Actualizar blueprint",
        description = "Actualiza un blueprint existente con los datos proporcionados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blueprint actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado"),
        @ApiResponse(responseCode = "409", description = "Conflicto al actualizar")
    })
    @PutMapping("/{author}/{name}")
    public ResponseEntity<ApiResponse<BlueprintDTO>> update(@PathVariable String author, @PathVariable String name,@Valid @RequestBody UpdateBlueprintRequest req) {
        if (req.points() == null) {
            return buildResponse(400, "Solicitud inválida o datos incorrectos", null);
        }
        try {
            List<PointDTO> points = req.points();
            Blueprint updatedBlueprint = new Blueprint(req.author(), req.name(), points.stream().map(BlueprintMapper::toEntity).toList());
            Blueprint updated = services.updateBlueprint(author, name, updatedBlueprint);
            return buildResponse(200, "Success", BlueprintMapper.toDTO(updated));
        } catch (BlueprintNotFoundException e) {
            return buildResponse(404, e.getMessage(), null);
        } catch (BlueprintPersistenceException e) {
            return buildResponse(409, e.getMessage(), null);
        }
    }

    /**
     * DELETE /api/blueprints/{author}/{name}
     * Elimina un blueprint existente.
     * @param author Nombre del autor
     * @param name Nombre del blueprint
     * @return ApiResponse con estado de eliminación
     */
    @Operation(
        summary = "Eliminar blueprint",
        description = "Elimina un blueprint existente por autor y nombre.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Blueprint eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    @DeleteMapping("/{author}/{name}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String author, @PathVariable String name) {
        try {
            services.deleteBlueprint(author, name);
            return buildResponse(200, "Deleted", null);
        } catch (BlueprintNotFoundException e) {
            return buildResponse(404, e.getMessage(), null);
        }
    }
    private <T> ResponseEntity<ApiResponse<T>> buildResponse(int code, String message, T data) {
        return ResponseEntity.status(code == 200 ? HttpStatus.OK :
                                     code == 201 ? HttpStatus.CREATED :
                                     code == 400 ? HttpStatus.BAD_REQUEST :
                                     code == 404 ? HttpStatus.NOT_FOUND :
                                     code == 409 ? HttpStatus.CONFLICT : HttpStatus.OK)
                .body(new ApiResponse<>(code, message, data));
    }

    public record NewBlueprintRequest(@NotBlank String author,@NotBlank String name,@Valid java.util.List<PointDTO> points
    ) { }

    public record UpdateBlueprintRequest(@NotBlank String author,@NotBlank String name,@Valid java.util.List<PointDTO> points
    ) { }
}


```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Operation#description#