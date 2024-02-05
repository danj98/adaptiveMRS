package adaptiveMRS.simulation

import adaptiveMRS.mission.Task
import adaptiveMRS.robot.Status
import kotlin.time.times

/**
 * Random task assignment used for testing purposes
 */
fun randomTaskAssignment(state: State): State {
    val availableRobots = state.robots.filter { it.status == Status.IDLE }
    val robot = availableRobots.random()
    val availableTasks = state.mission.tasks.filter { task ->
        !task.isComplete &&
                task.dependencies.all { dependency ->
                    dependency.from?.isComplete ?: true
                } &&
                robot.devices.all { device ->
                    device.supportedActions.any { action ->
                        action == task.actionType
                    }
                }
    }
    val task = availableTasks.random()
    robot.currentTask = task
    task.assignedRobots.add(robot)
    return state
}

val qTable = mutableMapOf<Pair<State, Action>, Double>()

fun qLearningTaskAssignment(env: Environment): State{
    val learningRate = 0.1
    val discountFactor = 0.9
    val epsilon = 0.1 // Exploration rate
    val iterations = 1000
    val random = java.util.Random()

    for (i in 0 until iterations) {
        // Simulate or fetch current state from the environment
        val state = env.getCurrentState()
        val availableActions = env.getAvailableActions(state)

        // Epsilon-greedy action selection
        val action = if (random.nextDouble() < epsilon) {
            availableActions.random()
        } else {
            // Select the action with the highest Q-value for the current state
            availableActions.maxByOrNull { qTable.getOrDefault(state to it, 0.0) }!!
        }

        // Perform the action and observe the new state and reward
        val (newState, reward) = env.performAction(state, action)

        // Update Q-table
        val oldQValue = qTable.getOrDefault(state to action, 0.0)
        val maxFutureQ = availableActions.maxOfOrNull { qTable.getOrDefault(newState to it, 0.0) } ?: 0.0
        val newQValue = oldQValue + learningRate * (reward + discountFactor * maxFutureQ - oldQValue)
        qTable[state to action] = newQValue
    }

    // Select the action with the highest Q-value for the current state
    val state = env.getCurrentState()
    val availableActions = env.getAvailableActions(state)
    val action = availableActions.maxByOrNull { qTable.getOrDefault(state to it, 0.0) }!!
    val (newState, _) = env.performAction(state, action)
    return newState
}

