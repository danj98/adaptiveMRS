package adaptiveMRS.simulation

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Task
import adaptiveMRS.robot.Arm
import adaptiveMRS.robot.Robot

/* Actions that the Q-learning task assigner can use */

fun templateAction(robot: Robot, availableTasks: List<Task>, context: Context): Task {
    return availableTasks.random()
}

fun proximityAction(robot: Robot, availableTasks: List<Task>, context: Context): Task {
    //println("Proximity action called")
    val robotLocation = robot.location
    val closestTask = availableTasks.minByOrNull { it.location.distanceTo(robotLocation, context) }
    return closestTask ?: availableTasks.random()
}

fun workloadAction(robot: Robot, availableTasks: List<Task>, context: Context): Task {
    //println("Workload action called")
    val availableTasksWithWorkload = availableTasks.filter { task ->
        val distance = robot.location.distanceTo(task.location, context)
        val batteryLevel = robot.battery.level
        val movementCost = distance * robot.movementCapabilities.movementCost
        val workSpeed = robot.devices.filter { it.supportedActions.contains(task.actionType) }
            .maxOfOrNull { device ->
                when (device) {
                    is Arm -> device.workingSpeed
                    else -> 0.0
                }
            } ?: 0.0
        val taskCost = robot.devices.filter { it.supportedActions.contains(task.actionType) }
            .maxOfOrNull { device ->
                when (device) {
                    is Arm -> device.workingCost
                    else -> 0.0
                }
            } ?: 0.0

        batteryLevel - movementCost - (task.workload / workSpeed) * taskCost >= 0
    }
    if (availableTasksWithWorkload.isEmpty()) {
        return availableTasks.random()
    }
    return availableTasksWithWorkload.random()
}

// Choose the task that can be completed the fastest based on working speed
fun workingSpeedAction(robot: Robot, availableTasks: List<Task>, context: Context): Task {
    //println("Working speed action called")
    val availableTasksWithWorkingSpeed = availableTasks.filter { task ->
        val workingSpeed = robot.devices.filter { it.supportedActions.contains(task.actionType) }
            .maxOfOrNull { device ->
                when (device) {
                    is Arm -> device.workingSpeed
                    else -> 0.0
                }
            } ?: 0.0
        task.workload / workingSpeed <= 0
    }
    if (availableTasksWithWorkingSpeed.isEmpty()) {
        return availableTasks.random()
    }
    return availableTasksWithWorkingSpeed.random()
}

// Choose a task which has the most dependencies
fun dependenciesAction(robot: Robot, availableTasks: List<Task>, context: Context): Task {
    //println("Dependencies action called")
    val availableTasksWithDependencies = availableTasks.filter { task ->
        task.dependencies.isNotEmpty()
    }
    return availableTasksWithDependencies.maxByOrNull { it.dependencies.size } ?: availableTasks.random()
}

// Choose a task with no robots working on it
fun noRobotsAction(robot: Robot, availableTasks: List<Task>, context: Context): Task {
    //println("No robots action called")
    val availableTasksWithNoRobots = availableTasks.filter { task ->
        task.assignedRobots.isEmpty()
    }
    return availableTasksWithNoRobots.random()
}

// Choose a task with the fewest robots working on it
fun fewestRobotsAction(robot: Robot, availableTasks: List<Task>, context: Context): Task {
    val availableTasksWithFewestRobots = availableTasks.filter { task ->
        task.assignedRobots.size == (availableTasks.minOfOrNull { it.assignedRobots.size } ?: 0)
    }
    return availableTasksWithFewestRobots.random()
}

// Choose a task closest to the robot's home location
fun homeLocationAction(robot: Robot, availableTasks: List<Task>, context: Context): Task {
    //println("Home location action called")
    val homeLocation = robot.home
    val closestTask = availableTasks.minByOrNull { it.location.distanceTo(homeLocation, context) }
    return closestTask ?: availableTasks.random()
}