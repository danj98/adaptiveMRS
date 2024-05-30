package adaptiveMRS.simulation

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.mission.Task
import adaptiveMRS.robot.Robot
import adaptiveMRS.robot.Status
import adaptiveMRS.utility.Location
import adaptiveMRS.utility.euclideanDistance
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

fun QLearningTaskAssigner(state: State, qTable: Map<StateAction, Double>): Pair<StateAction, Task> {
    val encodedState = encodeState(state)
    val action = selectAction(qTable, encodedState)
    val task = selectTask(state, action)
    return Pair(StateAction(encodedState, action), task)
}

fun encodeState(state: State): List<Double> {
    val encodedState = mutableListOf<Double>()

    val robot = state.robots.filter { it.beingAssigned }[0]
    // Robot normalized x and y
    val normalizedX = (robot.location.x.toDouble() / state.context.width).round(1)
    val normalizedY = (robot.location.y.toDouble() / state.context.height).round(1)
    encodedState.add(normalizedX)
    encodedState.add(normalizedY)


    // Robot normalized battery level
    encodedState.add((robot.battery.level / robot.battery.capacity).round(1))

    // Average distance of three closest tasks, bucketed into intervals of 0.1
    val availableTasks = state.mission.tasks.filter { !it.isComplete }
    val closestTasks = availableTasks.sortedBy { euclideanDistance(robot.location, it.location) }.take(3)
    val averageDistance = closestTasks.map { euclideanDistance(robot.location, it.location) }.average()
    val maxDistance = sqrt(state.context.width.toDouble().pow(2) + state.context.height.toDouble().pow(2))
    val bucketedDistance = ((averageDistance / maxDistance) * 10).toInt().toDouble() / 10
    encodedState.add(bucketedDistance)

    // Average workload of three closest tasks
    val averageWorkload = closestTasks.map { it.workload }.average()
    encodedState.add((averageWorkload / 5.0).round(1))

    // Total number of dependencies of three closest tasks
    val totalDependencies = closestTasks.sumBy { it.dependencies.size }
    encodedState.add(totalDependencies.toDouble())

    // Number of robots within 1/3 of the map size
    val robotsWithinRange = state.robots.filter { robot ->
        euclideanDistance(robot.location, Location(state.context.width / 2, state.context.height / 2)) <=
                sqrt(state.context.width.toDouble().pow(2) + state.context.height.toDouble().pow(2)) / 3
    }
    encodedState.add(robotsWithinRange.size.toDouble())

    // Normalized number of tasks currently being executed
    val tasksBeingExecuted = state.robots.filter { it.status == Status.WORKING }.size
    encodedState.add(tasksBeingExecuted.toDouble() / state.mission.tasks.size)

    // Normalized number of tasks pending assignment
    val tasksPendingAssignment = state.mission.tasks.filter { !it.isComplete && it.assignedRobots.isEmpty() }.size
    encodedState.add((tasksPendingAssignment.toDouble() / state.mission.tasks.size).round(1))

    return encodedState
}

@Serializable
data class StateAction(val state: List<Double>, val action: Int)

// If random is less than epsilon, return a random action, otherwise return the action with the highest Q-value in the current state
fun selectAction(qTable: Map<StateAction, Double>, stateVector: List<Double>, epsilon: Double = 0.1): Int {
    return if (Random.nextDouble() < epsilon) {
        Random.nextInt(0, 6)
    } else {
        val qValues = (0..5).map { action ->
            qTable.getOrDefault(StateAction(stateVector, action), 0.0)
        }
        val maxQValue = qValues.maxOrNull() ?: 0.0
        val maxQValueActions = (0..5).filter { action ->
            qTable.getOrDefault(StateAction(stateVector, action), 0.0) == maxQValue
        }
        if (maxQValue == 0.0 && maxQValueActions.size == 6) {
            Random.nextInt(0, 6)
        } else {
            maxQValueActions.random()
        }
    }
}

fun selectTask(state: State, action: Int): Task {
    val currentRobot = state.robots.filter { it.beingAssigned }[0]
    val availableTasks = state.mission.tasks.filter { !it.isComplete }
    val context = state.context

    return when (action) {
        0 -> proximityAction(currentRobot, availableTasks, context)
        1 -> workloadAction(currentRobot, availableTasks, context)
        2 -> workingSpeedAction(currentRobot, availableTasks, context)
        3 -> dependenciesAction(currentRobot, availableTasks, context)
        4 -> noRobotsAction(currentRobot, availableTasks, context)
        5 -> homeLocationAction(currentRobot, availableTasks, context)
        else -> {
            val tasks = state.mission.tasks.filter { !it.isComplete }
            tasks.random()
        }
    }
}

fun updateQtable(qTable: MutableMap<StateAction, Double>, currentStateAction: StateAction, reward: Double) {
    val learningRate = 0.1
    val discountFactor = 0.9
    val maxFutureQ = qTable.values.maxOrNull() ?: 0.0
    val oldQValue = qTable.getOrDefault(currentStateAction, 0.0)
    val newQValue = oldQValue + learningRate * (reward + discountFactor * maxFutureQ - oldQValue)
    qTable[currentStateAction] = newQValue
}



fun trainQTable(episodes: Int, qTable: MutableMap<StateAction, Double>, batch_size: Int = 10) {
    var batch = mutableListOf<Pair<StateAction, Double>>()

    for (episode in 1..episodes) {
        val generator = MissionGenerator(Pair(100, 100), 50, 10)
        val (context, mission, robots) = generator.generate()
        val env = Environment(mission, robots, context, "qLearning", qTable)
        val (iterations, stateActionsDuringMission) = env.run()
        val reward = -iterations.toDouble()

        stateActionsDuringMission.forEach { stateAction ->
            batch.add(Pair(stateAction, reward))
            if (batch.size >= batch_size) {
                batch.forEach { (sa, r) ->
                    updateQtable(qTable, sa, r)
                }
                batch.clear()
            }
        }

        if (episode % 100 == 0) {
            println("Episode $episode completed")
        }
        println("Finished episode $episode")
    }

    // Process any remaining batch items
    batch.forEach { (sa, r) ->
        updateQtable(qTable, sa, r)
    }
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}