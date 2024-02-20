package adaptiveMRS.simulation

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Task
import adaptiveMRS.robot.Arm
import adaptiveMRS.robot.Robot
import adaptiveMRS.robot.Status

/**
 * Random task assignment used for testing purposes
 */
fun randomTaskAssignment(availableTasks: List<Task>): Task {
    return availableTasks.random()
}



/*
 * Market-based task allocation
 */

fun marketBasedAssignment(env: Environment): State {
    val state = env.getCurrentState()
    val availableTasks = state.mission.tasks.filter { !it.isComplete }
    val availableRobots = state.robots.filter { it.status == Status.IDLE }
    val task = availableTasks.random()

    // Filter to only include robots that can perform the task
    val capableRobots = availableRobots.filter { robot ->
        robot.devices.any { device ->
            device.supportedActions.any { action ->
                action == task.actionType
            }
        }
    }
    // Bid based on distance to task, battery level, movementspeed, and device workingSpeed
    val bids = capableRobots.map { robot ->
        val distance = robot.location.distanceTo(task.location, state.context)
        val movementSpeed = robot.movementCapabilities.maxSpeed
        val deviceWorkingSpeed = robot.devices.filter { it.supportedActions.contains(task.actionType) }
            .maxOfOrNull { device ->
                when (device) {
                    is Arm -> device.workingSpeed
                    else -> 0.0
                }
            } ?: 0.0
        val bid = movementSpeed + deviceWorkingSpeed - distance
        robot to bid
    }

    // Select a robot based on the highest bid
    val selectedRobot = bids.maxByOrNull { it.second }!!.first
    selectedRobot.task = task
    task.assignedRobots.add(selectedRobot)
    return state
}

