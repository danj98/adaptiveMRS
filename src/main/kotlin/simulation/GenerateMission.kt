package adaptiveMRS.simulation

import adaptiveMRS.mission.*
import adaptiveMRS.robot.*
import adaptiveMRS.robot.Action
import adaptiveMRS.utility.Location
import java.util.*

fun generateMission(mapSize: Pair<Int, Int>, numTasks: Int, numRobots: Int, seed: Long? = null): Triple<Context, Mission, List<Robot>> {
    val rng = Random(seed ?: System.currentTimeMillis())

    val obstacles = generateObstacles(mapSize, rng)

    val context = Context(
        UUID.randomUUID(),
        mapSize.first,
        mapSize.second,
        mutableListOf(),
        mutableMapOf(),
        mutableListOf()
    )

    fillKnownLocationFromObstacles(obstacles, context)

    val tasks = generateTasksEnsuringReachability(mapSize, numTasks, rng, context)

    generateDependecies(tasks, rng)

    val robots = generateRobotsEnsuringSafety(numRobots, context, rng)

    val mission = Mission(
        UUID.randomUUID(),
        tasks
    )


    return Triple(context, mission, robots)
}

fun addKnownLocations(context: Context) {
    for (x in 0 until context.width) {
        for (y in 0 until context.height) {
            val location = Location(x, y)
            if (context.isObstacle(location)) {
                context.knownLocations[location] = CellType.OBSTACLE
            } else if (context.taskLocations.contains(location)) {
                context.knownLocations[location] = CellType.TASK
            } else {
                context.knownLocations[location] = CellType.EMPTY
            }
        }
    }
}


fun fillKnownLocationFromObstacles(obstacles: List<Location>, context: Context) {
    obstacles.forEach { obstacleLocation ->
        context.knownLocations[obstacleLocation] = CellType.OBSTACLE
    }
}

fun getInteriorCells(obstacle: Obstacle): List<Location> {
    val minX = obstacle.shell.minByOrNull { it.x }!!.x
    val maxX = obstacle.shell.maxByOrNull { it.x }!!.x
    val minY = obstacle.shell.minByOrNull { it.y }!!.y
    val maxY = obstacle.shell.maxByOrNull { it.y }!!.y

    val interiorCells = mutableListOf<Location>()
    for (x in minX until maxX) {
        for (y in minY until maxY) {
            val location = Location(x, y)
            if (isPointInsidePolygon(location, obstacle.shell)) {
                interiorCells.add(location)
            }
        }
    }
    return interiorCells
}

fun isPointInsidePolygon(point: Location, vertices: List<Location>): Boolean {
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

// Generates a random amount of obstacles around the map
fun generateObstacles(mapSize: Pair<Int, Int>, rng: Random): List<Location> {
    val numObstacles = rng.nextInt(20)
    val obstacles = mutableListOf<Location>()
    for (i in 0 until numObstacles) {
        val x = rng.nextInt(mapSize.first)
        val y = rng.nextInt(mapSize.second)
        val location = Location(x, y)
        obstacles.add(location)
    }
    return obstacles
}

fun generateTasks(mapSize: Pair<Int, Int>, numTasks: Int, rng: Random, context: Context): List<Task> {
    val tasks = mutableListOf<Task>()
    for (i in 0 until numTasks) {
        var x: Int
        var y: Int
        var location: Location
        do {
            x = rng.nextInt(mapSize.first)
            y = rng.nextInt(mapSize.second)
            location = Location(x, y)
        } while (context.isObstacle(location))

        tasks.add(Task(
            id = i,
            workload = rng.nextDouble() * 4 + 1.0,
            location = location,
            actionType = Action.WORK,
            assignedRobots = mutableListOf(),
            dependencies = mutableListOf()
        ))
    }
    return tasks
}

fun generateTasksEnsuringReachability(mapSize: Pair<Int, Int>, numTasks: Int, rng: Random, context: Context): List<Task> {
    val tasks = mutableListOf<Task>()
    repeat(numTasks) { i ->
        var location: Location
        do {
            val x = rng.nextInt(mapSize.first)
            val y = rng.nextInt(mapSize.second)
            location = Location(x, y)
        } while (location in context.knownLocations)
        val task = Task(
            id = i,
            workload = rng.nextDouble() * 4 + 1.0,
            location = location,
            actionType = Action.WORK,
            assignedRobots = mutableListOf(),
            dependencies = mutableListOf()
        )
        tasks.add(task)
        context.knownLocations[location] = CellType.TASK
    }
    return tasks
}


fun generateDependecies(tasks: List<Task>, rng: Random) {
    tasks.forEach { task ->
        if (rng.nextDouble() < 0.3) {
            val otherTaskIndex = rng.nextInt(tasks.size)
            val otherTask = tasks[otherTaskIndex]

            if (task.id != otherTask.id) {
                task.dependencies.add(TaskDependency(otherTask, null))
                otherTask.dependencies.add(TaskDependency(null, task))
            }
        }
    }
}

fun generateRobots(numRobots: Int, context: Context, rng: Random): List<Robot> {
    val robots = mutableListOf<Robot>()
    for (i in 0 until numRobots) {
        var x: Int
        var y: Int
        var location: Location
        do {
            x = rng.nextInt(context.width)
            y = rng.nextInt(context.height)
            location = Location(x, y)
        } while (context.knownLocations[location] == CellType.OBSTACLE)

        val robot = Robot(
            id = i,
            movementCapabilities = MovementCapability(0.0, 0.0),
            battery = Battery(100.0),
            devices = listOf(
                LIDAR(
                    detectionRange = 5.0
                ),
                Arm(
                    workingSpeed = 1.0
                )
            ),
            location = location,
            home = location,
            status = Status.IDLE,
            task = null
        )

        robots.add(robot)
    }
    return robots
}

fun generateRobotsEnsuringSafety(numRobots: Int, context: Context, rng: Random): List<Robot> {
    val robots = mutableListOf<Robot>()
    repeat(numRobots) { i ->
        var location: Location
        do {
            val x = rng.nextInt(context.width)
            val y = rng.nextInt(context.height)
            location = Location(x, y)
        } while (location in context.knownLocations)

        val robot = Robot(
            id = i,
            movementCapabilities = MovementCapability(0.0, 0.0),
            battery = Battery(100.0),
            devices = listOf(
                LIDAR(
                    detectionRange = 5.0
                ),
                Arm(
                    workingSpeed = 1.0
                )
            ),
            location = location,
            home = location,
            status = Status.IDLE,
            task = null
        )

        robots.add(robot)
    }
    return robots
}