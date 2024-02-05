package adaptiveMRS.mission

import adaptiveMRS.utility.Location
import java.util.UUID

data class Context (
    val id: UUID,
    val width: Int,
    val height: Int,
    val obstacles: List<Obstacle>
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
}

open class Area (
    val shell: List<Location>
)

class Obstacle (
    shell: List<Location>
) : Area(shell)
