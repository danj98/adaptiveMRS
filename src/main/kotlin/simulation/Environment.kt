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

    fun run() {
        println("Starting...")
        while (state.mission.tasks.any { !it.isComplete }) {
            step()
            //Thread.sleep(500)
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
        robot.task = task
        task.assignedRobots.add(robot)
        return state to 0.0
    }
}
