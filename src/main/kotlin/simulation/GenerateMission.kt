package adaptiveMRS.simulation

import adaptiveMRS.mission.*
import adaptiveMRS.robot.*
import adaptiveMRS.robot.Action
import adaptiveMRS.utility.Location
import java.util.*

class MissionGenerator(
    private val mapSize: Pair<Int, Int>,
    private val numTasks: Int,
    private val numRobots: Int
) {
    private val rng = Random(System.currentTimeMillis())

    fun generate(): Triple<Context, Mission, List<Robot>> {
        val context = createContext()
        addObstacles(context)
        val reachableLocations = findReachableLocations(context)

        val robots = generateRobots(context, reachableLocations)
        val tasks = generateTasks(context, reachableLocations)
        val mission = Mission(rng.nextInt(), tasks)

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

    private fun addObstacles(context: Context) {
        val totalObstacles = (mapSize.first * mapSize.second * 0.1).toInt()
        val knownObstacles = totalObstacles / 2

        repeat(knownObstacles) {
            val x = rng.nextInt(1, mapSize.first - 1)
            val y = rng.nextInt(1, mapSize.second - 1)
            val location = Location(x, y)
            if (context.knownLocations[location] == CellType.EMPTY) {
                context.obstacles.add(Obstacle(mutableListOf(location)))
                context.knownLocations[location] = CellType.OBSTACLE
            }
        }

        repeat(totalObstacles - knownObstacles) {
            val x = rng.nextInt(1, mapSize.first - 1)
            val y = rng.nextInt(1, mapSize.second - 1)
            val location = Location(x, y)
            if (!context.knownLocations.containsKey(location)) {
                context.obstacles.add(Obstacle(mutableListOf(location)))
            }
        }
    }

    private fun findReachableLocations(context: Context): MutableList<Location> {
        val start = Location(0, 0)
        val reachableLocations = mutableListOf<Location>()
        val visited = mutableSetOf<Location>()
        val queue = ArrayDeque<Location>()

        if (!context.isObstacle(start)) {
            queue.add(start)
            visited.add(start)
        }

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (!context.isObstacle(current)) {
                reachableLocations.add(current)
                getNeighbors(current).forEach { neighbor ->
                    if (neighbor !in visited && !context.isObstacle(neighbor)) {
                        queue.add(neighbor)
                        visited.add(neighbor)
                    }
                }
            }
        }
        return reachableLocations
    }

    private fun generateRobots(context: Context, availableLocations: MutableList<Location>): List<Robot> {
        val robots = mutableListOf<Robot>()
        repeat(numRobots) {
            val index = rng.nextInt(availableLocations.size)
            val location = availableLocations.removeAt(index)
            val battery = Battery(100.0, 100.0)
            val devices = listOf(LIDAR(detectionRange = 5.0), Arm(workingSpeed = 1.0))
            robots.add(Robot(it, MovementCapability(1.0, 1.0), battery, devices, location, location))
        }
        return robots
    }

    private fun generateTasks(context: Context, availableLocations: MutableList<Location>): List<Task> {
        val tasks = mutableListOf<Task>()
        repeat(numTasks) {
            val index = rng.nextInt(availableLocations.size)
            val location = availableLocations.removeAt(index)
            val dependencies = mutableListOf<TaskDependency>()
            tasks.add(Task(it, location, 5.0, mutableListOf(), dependencies, false, Action.WORK))
            context.knownLocations[location] = CellType.TASK
        }
        return tasks
    }

    private fun getNeighbors(location: Location): List<Location> {
        return listOf(
            Location(location.x + 1, location.y),
            Location(location.x - 1, location.y),
            Location(location.x, location.y + 1),
            Location(location.x, location.y - 1)
        ).filter {
            it.x in 0 until mapSize.first && it.y in 0 until mapSize.second
        }
    }
}
