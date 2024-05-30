package adaptiveMRS.mission

import adaptiveMRS.utility.Location
import java.util.UUID
import kotlin.math.pow

class Context private constructor(
    val id: Int,
    val width: Int,
    val height: Int,
    val obstacles: MutableList<Obstacle>,
    val knownLocations: MutableMap<Location, CellType>,
    val taskLocations: List<Location>
) {

    companion object {
        @Volatile private var INSTANCE: Context? = null

        fun getInstance(
            id: Int,
            width: Int,
            height: Int,
            obstacles: MutableList<Obstacle>,
            knownLocations: MutableMap<Location, CellType>,
            taskLocations: List<Location>
        ) : Context {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Context(id, width, height, obstacles, knownLocations, taskLocations).also { INSTANCE = it }
            }
        }

        fun create(
            id: Int,
            width: Int,
            height: Int,
            obstacles: MutableList<Obstacle>,
            knownLocations: MutableMap<Location, CellType>,
            taskLocations: List<Location>
        ): Context {
            return Context(id, width, height, obstacles, knownLocations, taskLocations)
        }

        fun deepCopy(context: Context): Context {
            val obstacles = context.obstacles.map { Obstacle(it.shell.toMutableList()) }.toMutableList()
            val knownLocations = mutableMapOf<Location, CellType>()
            context.knownLocations.forEach { (location, cellType) ->
                knownLocations[Location(location.x, location.y)] = cellType
            }
            val taskLocations = context.taskLocations.map { Location(it.x, it.y) }
            return Context(context.id, context.width, context.height, obstacles, knownLocations, taskLocations)
        }
    }

    fun isObstacle(location: Location): Boolean {
        return knownLocations[location] == CellType.OBSTACLE || obstacles.any { obstacle -> obstacle.shell.contains(location) }
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

    fun deepCopy(): Context {
        return deepCopy(this)
    }
}

open class Area (
    val shell: MutableList<Location>
)

class Obstacle (
    shell: MutableList<Location>
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
