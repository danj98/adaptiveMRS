package adaptiveMRS.mission

import adaptiveMRS.utility.Location
import java.util.UUID

data class Context (
    val id: UUID,
    val width: Int,
    val height: Int,
    val obstacles: List<Obstacle>,
    val knownLocations: MutableMap<Location, CellType>,
    val taskLocations: List<Location>
) {
    fun isObstacle(location: Location): Boolean {
        obstacles.forEach { obstacle ->
            if (isPointInsidePolygon(location, obstacle.shell)) {
                return true
            }
        }
        return false
    }

    private fun isPointInsidePolygon(point: Location, vertices: List<Location>): Boolean {
        var count = 0
        var i = 0
        var j = vertices.size - 1
        while (i < vertices.size) {
            if ((vertices[i].y > point.y) != (vertices[j].y > point.y) &&
                (point.x < (vertices[j].x - vertices[i].x) * (point.y - vertices[i].y) / (vertices[j].y - vertices[i].y) + vertices[i].x)) {
                count++
            }
            j = i++
        }
        return count % 2 != 0
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
