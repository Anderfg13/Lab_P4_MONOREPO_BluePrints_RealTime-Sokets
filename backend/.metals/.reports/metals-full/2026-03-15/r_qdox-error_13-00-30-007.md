error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java
file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[139,28]

error in qdox parser
file content:
```java
offset: 7044
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
     * @return ApiResponse con lista de blueprints
     */
    @Operation(summary = "Obtener blueprints por autor", description = "Devuelve todos los blueprints de un autor especificado. Códigos de respuesta: 200 (éxito), 404 (no encontrado)")
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
    @Operation(summary = "Obtener blueprint por autor y nombre", description = "Devuelve un blueprint específico por autor y nombre. Códigos de respuesta: 200 (éxito), 404 (no encontrado)")
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
    @Operation(summary = "Crear blueprint", description = "Crea un nuevo blueprint con los datos proporcionados. Códigos de respuesta: 201 (creado), 400 (solicitud inválida)")
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
    @Operation(summary = "Actualizar blueprint", description = "Actualiza un blueprint existente con los datos proporcionados. Códigos de respuesta: 200 (éxito), 400 (solicitud inválida), 404 (no encontrado), 409 (conflicto)")
        @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Blueprint actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida: el campo 'points' no puede ser nulo"),
            @ApiResponse(responseCode = "404", description = "No se encontró el blueprint solicitado"),
            @ApiResponse(responseCode = "409", description = "Error de persistencia"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    @PutMapping("/{author}/{name}")
    public ResponseEntity<ApiResponse<BlueprintDTO>> update(@PathVariable String author, @PathVariable String name,@Valid @RequestBody UpdateBlueprintRequest req) {
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
     * @return ApiResponse con estado de eliminación
     */
    @Operation(summary = "Eliminar blueprint", description = "Elimina un blueprint existente por autor y nombre. Códigos de respuesta: 200 (éxito), 404 (no encontrado)")
    @DeleteMapping("/{author}/{name}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String author, @PathVariable String name) {
        try {
            services.deleteBlueprint(author, name);
            return buildResponse(200, "Deleted", null);
        } catch (BlueprintNotFoundException e) {
            return buildResponse(404, e.getMessage(), null);
        }
    }
    publ <T> ResponseEntity<@@ApiResponse<T>> buildResponse(int code, String message, T data) {
        HttpStatus status;
        switch (code) {
            case 200: status = HttpStatus.OK; break;
            case 201: status = HttpStatus.CREATED; break;
            case 400: status = HttpStatus.BAD_REQUEST; break;
            case 404: status = HttpStatus.NOT_FOUND; break;
            case 409: status = HttpStatus.CONFLICT; break;
            case 500: status = HttpStatus.INTERNAL_SERVER_ERROR; break;
            default: status = HttpStatus.OK;
        }
        return ResponseEntity.status(status).body(new ApiResponse<>(code, message, data));
    }

    public record NewBlueprintRequest(@NotBlank String author,@NotBlank String name,@Valid java.util.List<PointDTO> points
    ) { }

    public record UpdateBlueprintRequest(@NotBlank String author,@NotBlank String name,@Valid java.util.List<PointDTO> points
    ) { }
}


```

```



#### Error stacktrace:

```
com.thoughtworks.qdox.parser.impl.Parser.yyerror(Parser.java:2025)
	com.thoughtworks.qdox.parser.impl.Parser.yyparse(Parser.java:2147)
	com.thoughtworks.qdox.parser.impl.Parser.parse(Parser.java:2006)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:232)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:190)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:94)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:89)
	com.thoughtworks.qdox.library.SortedClassLibraryBuilder.addSource(SortedClassLibraryBuilder.java:162)
	com.thoughtworks.qdox.JavaProjectBuilder.addSource(JavaProjectBuilder.java:174)
	scala.meta.internal.mtags.JavaMtags.indexRoot(JavaMtags.scala:49)
	scala.meta.internal.metals.SemanticdbDefinition$.foreachWithReturnMtags(SemanticdbDefinition.scala:99)
	scala.meta.internal.metals.Indexer.indexSourceFile(Indexer.scala:560)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3(Indexer.scala:691)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3$adapted(Indexer.scala:688)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:630)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:628)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1313)
	scala.meta.internal.metals.Indexer.reindexWorkspaceSources(Indexer.scala:688)
	scala.meta.internal.metals.MetalsLspService.$anonfun$onChange$2(MetalsLspService.scala:940)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:691)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:500)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1583)
```
#### Short summary: 

QDox parse error in file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java