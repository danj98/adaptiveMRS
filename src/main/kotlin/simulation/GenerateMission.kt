package adaptiveMRS.simulation

import adaptiveMRS.mission.*
import adaptiveMRS.robot.*
import adaptiveMRS.robot.Action
import adaptiveMRS.utility.Location
import java.util.*

fun generateMission(mapSize: Pair<Int, Int>, numTasks: Int, numRobots: Int, seed: Long? = null): Triple<Context, Mission, List<Robot>> {
    val rng = Random(seed ?: System.currentTimeMillis())
    val context = Context(
        id = UUID.randomUUID(),
        mapSize.first,
        mapSize.second,
        generateObstacles(mapSize, rng)
    )
    val tasks = generateTasks(mapSize, numTasks, rng, context.obstacles)
    generateDependecies(tasks, rng)

    val robots = generateRobots(numRobots, context, rng)

    val mission = Mission(
        id = UUID.randomUUID(),
        tasks = tasks,
        completedTasks = mutableListOf()
    )

    return Triple(context, mission, robots)
}

fun generateObstacles(mapSize: Pair<Int, Int>, rng: Random): List<Obstacle> {
    val numObstacles = rng.nextInt(5)
    val obstacles = mutableListOf<Obstacle>()
    for (i in 0 until numObstacles) {
        val x = rng.nextInt(mapSize.first)
        val y = rng.nextInt(mapSize.second)
        val width = rng.nextInt(5)
        val height = rng.nextInt(5)
        val shell = listOf(
            Location(x, y),
            Location(x + width, y),
            Location(x + width, y + height),
            Location(x, y + height)
        )
        obstacles.add(Obstacle(shell))
    }
    return obstacles
}

fun generateTasks(mapSize: Pair<Int, Int>, numTasks: Int, rng: Random, obstacles: List<Obstacle>): List<Task> {
    val tasks = mutableListOf<Task>()
    for (i in 0 until numTasks) {
        var x: Int
        var y: Int
        var location: Location
        do {
            x = rng.nextInt(mapSize.first)
            y = rng.nextInt(mapSize.second)
            location = Location(x, y)
        } while (obstacles.any { it.isPointObstacle(location) })

        tasks.add(Task(
            id = i,
            workload = 1.0,
            referencePosition = location,
            actionType = Action.WORK,
            assignedRobots = mutableListOf(),
            dependencies = mutableListOf()
        ))
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
        } while (context.isObstacle(location))

        robots.add(Robot(
            id = i,
            movementCapabilities = MovementCapability(
                payloadWeight = 1.0,
                maxSpeed = rng.nextDouble()
            ),
            battery = Battery(
                capacity = 1.0,
                rechargeTime = 1.0
            ),
            devices = listOf(
                Device(
                    id = UUID.randomUUID(),
                    name = "Arm",
                    supportedActions = listOf(Action.WORK)
                )
            ),
            home = location,
            currentLocation = location
        ))
    }
    return robots
}