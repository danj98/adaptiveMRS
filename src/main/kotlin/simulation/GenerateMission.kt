package adaptiveMRS.simulation

import adaptiveMRS.mission.*
import adaptiveMRS.robot.*
import adaptiveMRS.robot.Action
import adaptiveMRS.utility.Location
import java.util.*
import kotlin.math.abs

class MissionGenerator(
    private val mapSize: Pair<Int, Int>,
    private val numTasks: Int,
    private val numRobots: Int
) {
    private val rng = Random(System.currentTimeMillis())

    fun generate(): Triple<Context, Mission, List<Robot>> {
        // Create a new context and mission setup for each call
        val context = createContext()
        val robots = generateRobots(context)
        val tasks = generateTasks(context)
        val mission = Mission(rng.nextInt(), tasks)

        // Populate valid obstacle locations considering new tasks and robots
        addObstacles(context, robots, tasks)

        return Triple(context, mission, robots)
    }

    private fun createContext(): Context {
        val obstacles = mutableListOf<Obstacle>()
        val knownLocations = mutableMapOf<Location, CellType>()
        for (x in 0 until mapSize.first) {
            for (y in 0 until mapSize.second) {
                knownLocations[Location(x, y)] = CellType.EMPTY
            }
        }
        return Context.create(rng.nextInt(), mapSize.first, mapSize.second, obstacles, knownLocations, mutableListOf())
    }


    private fun generateRobots(context: Context): List<Robot> {
        val robots = mutableListOf<Robot>()
        val availableLocations = context.knownLocations.filter { it.value == CellType.EMPTY }.keys.toMutableList()
        repeat(numRobots) {
            val index = rng.nextInt(availableLocations.size)
            val location = availableLocations.removeAt(index)
            val battery = Battery(100.0)
            val devices = listOf(LIDAR(detectionRange = 5.0), Arm(workingSpeed = 1.0))
            robots.add(Robot(it, MovementCapability(1.0, 1.0), battery, devices, location, location))
        }
        return robots
    }

    private fun generateTasks(context: Context): List<Task> {
        val tasks = mutableListOf<Task>()
        val availableLocations = context.knownLocations.filter { it.value == CellType.EMPTY }.keys.toMutableList()
        repeat(numTasks) {
            val index = rng.nextInt(availableLocations.size)
            val location = availableLocations.removeAt(index)
            val dependencies = mutableListOf<TaskDependency>()
            tasks.add(Task(it, location, 5.0, mutableListOf(), dependencies, false, Action.WORK))
            context.knownLocations[location] = CellType.TASK
        }
        return tasks
    }

    private fun addObstacles(context: Context, robots: List<Robot>, tasks: List<Task>) {
        val unavailableLocations = robots.map { it.location } + tasks.map { it.location }
        val validLocations = context.knownLocations.filterKeys {
            it !in unavailableLocations && !isNearby(unavailableLocations, it)
        }.keys.toMutableList()

        val numObstacles = (validLocations.size * 0.1).toInt()
        repeat(numObstacles) {
            if (validLocations.isNotEmpty()) {
                val index = rng.nextInt(validLocations.size)
                val location = validLocations.removeAt(index)
                context.obstacles.add(Obstacle(mutableListOf(location)))
                context.knownLocations[location] = CellType.OBSTACLE
            }
        }
    }

    private fun isNearby(unavailableLocations: List<Location>, location: Location): Boolean {
        return unavailableLocations.any {
            abs(it.x - location.x) <= 1 && abs(it.y - location.y) <= 1
        }
    }

    // Print map with tasks and obstacles
    fun printMap(context: Context) {
        for (y in 0 until context.height) {
            for (x in 0 until context.width) {
                val location = Location(x, y)
                val cellType = context.knownLocations[location]
                print(when (cellType) {
                    CellType.EMPTY -> "."
                    CellType.OBSTACLE -> "#"
                    CellType.TASK -> "T"
                    null -> "?"
                })
            }
            println()
        }
    }
}
