package adaptiveMRS.simulation

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Task
import adaptiveMRS.robot.Arm
import adaptiveMRS.robot.Robot
import adaptiveMRS.robot.Status
import kotlin.math.pow
import kotlin.math.sqrt

fun randomTaskAssignment(state: State): Task {
    val availableTasks = state.mission.tasks.filter { !it.isComplete }
    if (availableTasks.isEmpty()) throw IllegalStateException("No available tasks")
    return availableTasks.random()
}

fun marketBasedAssignment(state: State): Task {
    val robot = state.robots.find { it.beingAssigned }

    val suitableTasks = state.mission.tasks.filter { task ->
        !task.isComplete && robot?.devices?.any { device ->
            device is Arm && device.supportedActions.contains(task.actionType)
        } ?: false
    }

    if (suitableTasks.isEmpty()) throw IllegalStateException("No suitable tasks for robot $robot")

    val taskBids = suitableTasks.map { task ->
        val distanceScore = 1.0 - (task.location.euclideanDistanceTo(robot!!.location) / (sqrt(state.context.width.toDouble().pow(2) + state.context.height.toDouble().pow(2))))
        val workloadScore = 1 / task.workload
        val urgencyScore = task.dependencies.size.toDouble()
        val allocationScore = task.assignedRobots.size.toDouble() / state.robots.size

        val aggregatedScore = distanceScore + workloadScore + urgencyScore - allocationScore

        Pair(task, aggregatedScore)
    }
    return weightedRandomSelection(taskBids)
}

fun weightedRandomSelection(taskBids: List<Pair<Task, Double>>): Task {
    val totalScore = taskBids.sumByDouble { it.second }
    val randomValue = Math.random() * totalScore
    var currentScore = 0.0
    taskBids.forEach { (task, score) ->
        currentScore += score
        if (currentScore >= randomValue) return task
    }
    throw IllegalStateException("No task selected")
}

