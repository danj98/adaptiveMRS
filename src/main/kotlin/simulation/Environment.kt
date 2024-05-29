package adaptiveMRS.simulation

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.robot.Robot
import adaptiveMRS.robot.Status

data class State(
    val robots: List<Robot>,
    val context: Context,
    val mission: Mission
)

class Environment(
    private val mission: Mission,
    private val robots: List<Robot>,
    private val initialContext: Context,
    private val taskAssigner: String,
    private val qTable: MutableMap<StateAction, Double> = mutableMapOf()
) {

    private var iterations = 0
    private var state = State(robots, initialContext, mission)
    private val stateActionsDuringMission = mutableListOf<StateAction>()

    fun run(): Pair<Int, MutableList<StateAction>> {
        //println("The method is $taskAssigner")
        stateActionsDuringMission.clear()

        while (mission.tasks.any { !it.isComplete }) {
            assignTasksToIdleRobots()
            for (task in state.mission.tasks) {
                if (task.timesAssigned > 10) {
                    task.isComplete = true
                }
            }
            //println("Tasks assigned, starting execution loop")

            while (robots.all { it.status != Status.IDLE }) {
                robots.forEach { robot ->
                    robot.execute(state.context)
                    //println("Robot ${robot.id} status: ${robot.status}")
                }
                state.mission.tasks.forEach { task ->
                    if (task.workload <= 0) {
                        task.isComplete = true
                    }
                }
                //println("Iteration $iterations: Tasks ${state.mission.tasks.map { it.isComplete }}")
                iterations++
            }
           //println("All robots are IDLE")
        }
        //println("Mission completed in $iterations iterations using method $taskAssigner")
        return Pair(iterations, stateActionsDuringMission)
    }

    private fun assignTasksToIdleRobots() {
        val idleRobots = robots.filter { it.status == Status.IDLE }
        idleRobots.forEach { robot ->
            robot.beingAssigned = true

            val task = when (taskAssigner) {
                "random" -> {
                    randomTaskAssignment(state)
                }
                "market" -> {
                    marketBasedAssignment(state)
                }
                "qLearning" -> {
                    val (stateAction, task) = QLearningTaskAssigner(state, qTable)
                    stateActionsDuringMission.add(stateAction)
                    task
                }
                else -> {
                    throw IllegalArgumentException("Invalid task assigner")
                }
            }
            robot.task = task
            task.timesAssigned++
            //println("Robot ${robot.id} assigned task ${task.id} at location ${task.location}")
            robot.status = Status.MOVING
            robot.beingAssigned = false
        }

    }
}

