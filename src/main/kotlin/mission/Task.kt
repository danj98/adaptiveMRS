package adaptiveMRS.mission

import adaptiveMRS.robot.Action
import adaptiveMRS.robot.Robot
import adaptiveMRS.utility.Location

data class Task (
    val id: Int,
    val location: Location,
    var workload: Double,
    val assignedRobots: MutableList<Robot>,
    val dependencies: MutableList<TaskDependency>,
    var isComplete: Boolean = false,
    val actionType: Action,
    var timesAssigned: Int = 0
) {
    companion object {
        fun deepCopy(task: Task): Task {
            val assignedRobots = task.assignedRobots.map { it }
            val dependencies = task.dependencies.map { TaskDependency(it.from, it.to) }
            return Task(
                task.id,
                task.location,
                task.workload,
                assignedRobots.toMutableList(),
                dependencies.toMutableList(),
                task.isComplete,
                task.actionType
            )
        }
    }
}

data class TaskDependency(
    val from: Task?,
    val to: Task?
)

class TaskAssignment(private val task: Task, private val assignedRobots: MutableList<Robot> = mutableListOf()) {
    fun getTaskLocationForRobot(robot: Robot): Location {
        return if (assignedRobots.contains(robot)) {
            task.location
        } else {
            Location()
        }
    }
}