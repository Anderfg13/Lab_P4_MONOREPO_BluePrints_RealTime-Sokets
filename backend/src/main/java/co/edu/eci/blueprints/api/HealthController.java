package co.edu.eci.blueprints.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<ApiResp<Map<String, Object>>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("timestamp", Instant.now().toString());
        data.put("service", "blueprints-api");

        boolean dbUp;
        try (Connection ignored = dataSource.getConnection()) {
            dbUp = true;
        } catch (Exception ex) {
            dbUp = false;
        }

        data.put("database", dbUp ? "UP" : "DOWN");
        data.put("realtime", "UP");

        int statusCode = dbUp ? 200 : 503;
        String message = dbUp ? "UP" : "DEGRADED";
        return ResponseEntity.status(statusCode).body(new ApiResp<>(statusCode, message, data));
    }
}
