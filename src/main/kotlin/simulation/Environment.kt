package adaptiveMRS.simulation

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.utility.Location
import adaptiveMRS.robot.Robot
import adaptiveMRS.robot.Status

data class State(
    val robots: List<Robot>,
    val context: Context,
    val mission: Mission
)

data class Action (
    val robotIndex: Int,
    val taskIndex: Int
)

class Environment(val mission: Mission, val robots: List<Robot>, initialContext: Context) {
    private var iterations = 0
    private var state = State(
        robots = robots,
        context = initialContext,
        mission = mission
    )

    private fun step() {
        if (state.mission.tasks.all { it.isComplete }) {
            return
        }
        // If any robots are idle, assign them a task
        if (state.robots.any { it.status == Status.IDLE }) {
            state = qLearningTaskAssignment(this)
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
        // Print any new assignments
        state.mission.tasks.forEachIndexed { _, task ->
            if (task.assignedRobots.isNotEmpty()) {
                println("Task ${task.id} is assigned to ${task.assignedRobots.map { it.id }}")
            }
        }

        // Print the status of each robot
        state.robots.forEach { robot ->
            println("Robot ${robot.id} is ${robot.status}")
        }

        // Print if any new task is complete
        state.mission.tasks.forEach { task ->
            if (task.isComplete) {
                println("Task ${task.id} is complete")
            }
        }
        iterations++
    }

    fun run() {
        println("Starting...")
        while (state.mission.tasks.any { !it.isComplete }) {
            step()
            println("new step...")
            Thread.sleep(500)
        }
        println("Mission complete")
        println("Iterations used: $iterations")
    }

    fun getCurrentState(): State {
        return state
    }

    fun getAvailableActions(state: State): List<Action> {
        val availableRobots = state.robots.filter { it.status == Status.IDLE }
        val availableTasks = state.mission.tasks.filter { task ->
            !task.isComplete &&
                    task.dependencies.all { dependency ->
                        dependency.from?.isComplete ?: true
                    } &&
                    availableRobots.any { robot ->
                        robot.devices.all { device ->
                            device.supportedActions.any { action ->
                                action == task.actionType
                            }
                        }
                    }
        }
        return availableRobots.flatMap { robot ->
            availableTasks.map { task ->
                Action(robotIndex = state.robots.indexOf(robot), taskIndex = state.mission.tasks.indexOf(task))
            }
        }
    }

    fun performAction(state: State, action: Action): Pair<State, Double> {
        val robot = state.robots[action.robotIndex]
        val task = state.mission.tasks[action.taskIndex]
        robot.currentTask = task
        task.assignedRobots.add(robot)
        return state to 0.0
    }
}
