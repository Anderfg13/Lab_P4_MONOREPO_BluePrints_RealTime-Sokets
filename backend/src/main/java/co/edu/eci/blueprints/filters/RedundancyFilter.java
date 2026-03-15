package co.edu.eci.blueprints.filters;

import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * RedundancyFilter: removes consecutive duplicate points.
 * If there are two or more equal consecutive points, only keeps the first one.
 * Profile: "redundancy"
 */
@Component
@Profile("redundancy")
public class RedundancyFilter implements BlueprintsFilter {
    
    @Override
    public Blueprint apply(Blueprint bp) {
        List<Point> originalPoints = bp.getPoints();
        
        // If there are 0 or 1 points, there's nothing to filter
        if (originalPoints == null || originalPoints.size() <= 1) {
            return bp;
        }
        
        List<Point> filteredPoints = new ArrayList<>();
        
        // Always add the first point
        Point previousPoint = originalPoints.get(0);
        filteredPoints.add(previousPoint);
        
        // Iterate from the second point onwards
        for (int i = 1; i < originalPoints.size(); i++) {
            Point currentPoint = originalPoints.get(i);
            
            // Only add the point if it's different from the previous one
            if (!currentPoint.equals(previousPoint)) {
                filteredPoints.add(currentPoint);
                previousPoint = currentPoint;
            }
            // If it's the same, we skip it (don't add it)
        }
        
        return new Blueprint(bp.getAuthor(), bp.getName(), filteredPoints);
    }
}