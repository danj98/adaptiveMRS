package adaptiveMRS.simulation

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.mission.Task
import adaptiveMRS.robot.Robot
import adaptiveMRS.robot.Status

data class State(
    val robots: List<Robot>,
    val context: Context,
    val mission: Mission
)



class Environment(val mission: Mission, val robots: List<Robot>, initialContext: Context) {
    private var iterations = 0
    private var state = State(
        robots = robots,
        context = initialContext,
        mission = mission
    )

    /*
    private fun step() {
        if (state.mission.tasks.all { it.isComplete }) {
            return
        }
        // If any robots are idle, assign them a task
        if (state.robots.any { it.status == Status.IDLE }) {
            state = marketBasedAssignment(this)
            //state = randomTaskAssignment(state)
        }

        for (robots in state.robots) {
            robots.execute(context = state.context)
        }
        // Check if any tasks are complete
        state.mission.tasks.forEach { task ->
            if (task.workload <= 0) {
                task.isComplete = true
            }
        }
        iterations++
    }
    */

    fun run(qTable: MutableMap<StateActionFeature, Double> = mutableMapOf()): Int {
        println("Starting...")
        while (mission.tasks.any { !it.isComplete }) {
            // For each idle robot
            val idleRobots = robots.filter { it.status == Status.IDLE }
            idleRobots.forEach { robot ->
                robot.beingAssigned = true
                // Q-learning based task assignment
                val (stateAction, task) = QLearningTaskAssigner(state, qTable)
                // Random task assignment
                // val task = randomTaskAssignment(availableTasks = state.mission.tasks.filter { !it.isComplete })
                // Market-based task assignment
                // state = marketBasedAssignment(this)

                robot.task = task
                robot.status = Status.MOVING
                robot.beingAssigned = false
                // If the stateAction does not exist in the qTable, add it with default value 0.0
                if (!qTable.containsKey(stateAction)) {
                    qTable[stateAction] = 0.0
                }
            }
            while (robots.any { it.status != Status.IDLE }) {
                robots.forEach { robot ->
                    robot.execute(state.context)
                }
                state.mission.tasks.forEach { task ->
                    if (task.workload <= 0) {
                        task.isComplete = true
                    }
                }
                iterations++
            }
        }
        return iterations
    }

    fun getCurrentState(): State {
        return state
    }
}

fun updateQTable(qTable: MutableMap<StateActionFeature, Double>, iterations: Int) {
    // Update q-values with the reward of -iterations if it is higher than the current value
    qTable.forEach { (stateAction, value) ->
        qTable[stateAction] = maxOf(value, -iterations.toDouble())
    }
}

/*
 * Each run is an episode. When an episode is done (all tasks are complete), the q-table is updated (if necessary).
 * The same q-table is then used for the next episode.
 */
fun trainQTable(episodes: Int): MutableMap<StateActionFeature, Double> {
    val qTable = mutableMapOf<StateActionFeature, Double>()
    for (i in 0 until episodes) {
        val (context, mission, robots) = generateMission(100 to 100, 50, 10)
        val env = Environment(mission, robots, context)
        val iterations = env.run(qTable)
        updateQTable(qTable, iterations)
        println("Episode $i: $iterations iterations")
    }
    return qTable
}