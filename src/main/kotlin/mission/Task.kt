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
    val actionType: Action
)

data class TaskDependency(
    val from: Task?,
    val to: Task?
)