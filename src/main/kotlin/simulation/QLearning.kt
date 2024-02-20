package adaptiveMRS.simulation

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.mission.Task
import adaptiveMRS.robot.Robot
import adaptiveMRS.robot.Status
import adaptiveMRS.utility.Location
import adaptiveMRS.utility.aStar
import adaptiveMRS.utility.euclideanDistance
import kotlin.math.pow
import kotlin.math.sqrt

fun QLearningTaskAssigner(state: State, qTable: Map<StateActionFeature, Double>): Pair<StateActionFeature, Task> {
    // encode current state
    val encodedState = encodeState(state)
    // Select action to use
    val action = selectAction(qTable, encodedState)
    // Use action to select task
    return Pair(StateActionFeature(encodedState, action) , selectTask(state, action))
}

fun encodeState(state: State): List<Double> {
    val encodedState = mutableListOf<Double>()
    // Add normalized task positions
    for (task in state.mission.tasks) {
        encodedState.add(task.location.x.toDouble() / state.context.width)
        encodedState.add(task.location.y.toDouble() / state.context.height)
    }
    // Add normalized task workload
    val maxWorkload = state.mission.tasks.maxOf { it.workload }
    for (task in state.mission.tasks) {
        encodedState.add(task.workload / maxWorkload)
    }
    // Add normalized number of task dependencies
   val maxDependencies = state.mission.tasks.maxOf { it.dependencies.size }
    for (task in state.mission.tasks) {
        encodedState.add(task.dependencies.size.toDouble() / maxDependencies)
    }
    // Add normalized number of assigned robots
    val maxAssignedRobots = state.mission.tasks.maxOf { it.assignedRobots.size }
    for (task in state.mission.tasks) {
        encodedState.add(task.assignedRobots.size.toDouble() / maxAssignedRobots)
    }

    // Add normalized robot positions
    for (robot in state.robots) {
        encodedState.add(robot.location.x.toDouble() / state.context.width)
        encodedState.add(robot.location.y.toDouble() / state.context.height)
    }
    // Add normalized robot status
    for (robot in state.robots) {
        encodedState.add(when (robot.status) {
            Status.IDLE -> 0.0
            Status.MOVING -> 0.5
            Status.WORKING -> 1.0
            Status.RECHARGING -> 0.0
        })
    }
    // Add normalized robot battery level
    for (robot in state.robots) {
        encodedState.add(robot.battery.level / robot.battery.capacity)
    }
    // Add normalized robot distance to tasks
    val diagonal = sqrt(state.context.width.toDouble().pow(2) + state.context.height.toDouble().pow(2))
    for (robot in state.robots) {
        for (task in state.mission.tasks) {
            val distance = euclideanDistance(robot.location, task.location)
            encodedState.add(distance / diagonal)
        }
    }
    // Add if robot is being assigned
    for (robot in state.robots) {
        encodedState.add(if (robot.beingAssigned) 1.0 else 0.0)
    }
    return encodedState
}

data class StateActionFeature(val features: List<Double>, val action: Int)

fun selectAction(qTable: Map<StateActionFeature, Double>, stateVector: List<Double>): Int {
    // Epsilon greedy strategy
    val epsilon = 0.1
    val randomValue = Math.random()
    val actionRange = 0..5
    return if (randomValue < epsilon) {
        actionRange.random()
    } else {
        actionRange.maxByOrNull { action ->
            qTable.getOrDefault(StateActionFeature(stateVector, action), 0.0)
        } ?: actionRange.random()
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