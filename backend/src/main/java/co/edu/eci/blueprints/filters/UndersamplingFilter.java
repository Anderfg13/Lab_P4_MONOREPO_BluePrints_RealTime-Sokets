package co.edu.eci.blueprints.filters;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * UndersamplingFilter: keeps 1 out of every 2 points (even indices).
 * Profile: "undersampling"
 */
@Component
@Profile("undersampling")
public class UndersamplingFilter implements BlueprintsFilter {
    
    @Override
    public Blueprint apply(Blueprint bp) {
        List<Point> originalPoints = bp.getPoints();
        
        // If there are 0 or 1 points, there's no point in applying the filter
        if (originalPoints == null || originalPoints.size() <= 1) {
            return bp;
        }
        
        List<Point> filteredPoints = new ArrayList<>();
        
        // Keep points at even indices (0, 2, 4, ...)
        for (int i = 0; i < originalPoints.size(); i++) {
            if (i % 2 == 0) {
                filteredPoints.add(originalPoints.get(i));
            }
        }
        
        return new Blueprint(bp.getAuthor(), bp.getName(), filteredPoints);
    }
}