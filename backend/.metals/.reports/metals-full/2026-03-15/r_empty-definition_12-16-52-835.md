error id: file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java:io/swagger/v3/oas/annotations/responses/ApiResponse#
file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java
empty definition using pc, found symbol in pc: io/swagger/v3/oas/annotations/responses/ApiResponse#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 1338
uri: file:///D:/ander/Documents/SEMESTRE%207/ARSW/LAB07%20-%20P4%20BLUEPRINTS/backend/src/main/java/co/edu/eci/blueprints/api/BlueprintController.java
text:
```scala
package co.edu.eci.blueprints.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/blueprints")
@CrossOrigin(origins = "http://localhost:5173")
public class BlueprintController {

    /**
     * Obtiene la lista de blueprints disponibles.
     *
     * @return ApiResponse con lista de blueprints (id y nombre)
     */
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Obtener lista de blueprints",
        description = "Devuelve una lista de blueprints con id y nombre.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"code\":200,\"message\":\"Success\",\"data\":[{\"id\":\"b1\",\"name\":\"Casa de campo\"},{\"id\":\"b2\",\"name\":\"Edificio urbano\"}]}"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiR@@esponse(responseCode = "401", description = "No autorizado", content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"code\":401,\"message\":\"Unauthorized\",\"data\":null}"))
        )
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> list() {
        List<Map<String, String>> data = List.of(
            Map.of("id", "b1", "name", "Casa de campo"),
            Map.of("id", "b2", "name", "Edificio urbano")
        );
        return ResponseEntity.ok(new ApiResponse<>(200, "Success", data));
    }

    /**
     * Crea un nuevo blueprint.
     *
     * @param in Datos del blueprint (nombre)
     * @return ApiResponse con blueprint creado (id y nombre)
     */
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Crear blueprint",
        description = "Crea un nuevo blueprint a partir del nombre proporcionado.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Blueprint creado exitosamente", content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"code\":201,\"message\":\"Created\",\"data\":{\"id\":\"new\",\"name\":\"nuevo\"}}"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"code\":400,\"message\":\"Solicitud inválida\",\"data\":null}"))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "No autorizado", content = @io.swagger.v3.oas.annotations.media.Content(
            mediaType = "application/json",
            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "{\"code\":401,\"message\":\"Unauthorized\",\"data\":null}"))
        )
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public ResponseEntity<ApiResponse<Map<String, String>>> create(@RequestBody Map<String, String> in) {
        if (in == null || !in.containsKey("name")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(400, "Solicitud inválida", null));
        }
        Map<String, String> created = Map.of("id", "new", "name", in.getOrDefault("name", "nuevo"));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>(201, "Created", created));
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: io/swagger/v3/oas/annotations/responses/ApiResponse#