package adaptiveMRS.mission

import adaptiveMRS.utility.Location
import java.util.UUID

data class Context (
    val id: UUID,
    val width: Int,
    val height: Int,
    val obstacles: List<Obstacle>,
    val knownLocations: Map<Location, CellType>,
    val taskLocations: List<Location>
) {
    fun isObstacle(location: Location): Boolean {
        for (obstacle in obstacles) {
            if (location.x >= obstacle.shell[0].x && location.x <= obstacle.shell[2].x) {
                if (location.y >= obstacle.shell[0].y && location.y <= obstacle.shell[2].y) {
                    return true
                }
            }
        }
        return false
    }

    fun isKnownTask(location: Location): Boolean {
        return knownLocations[location] == CellType.TASK
    }

    fun isKnownObstacle(location: Location): Boolean {
        return knownLocations[location] == CellType.OBSTACLE
    }
}

open class Area (
    val shell: List<Location>
)

class Obstacle (
    shell: List<Location>
) : Area(shell) {

    fun isPointObstacle(location: Location): Boolean {
        return location.x >= shell[0].x && location.x <= shell[2].x && location.y >= shell[0].y && location.y <= shell[2].y
    }
}

enum class CellType {
    EMPTY,
    OBSTACLE,
    TASK
}
