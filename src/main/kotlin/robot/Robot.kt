package adaptiveMRS.robot

import adaptiveMRS.mission.CellType
import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Task
import adaptiveMRS.utility.Location
import adaptiveMRS.utility.aStar
import java.util.UUID
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Robot (
    val id: Int,
    val movementCapabilities: MovementCapability = MovementCapability(0.0, 0.0),
    val battery: Battery,
    val devices: List<Device>,
    var location: Location,
    val home: Location,
    var status: Status = Status.IDLE,
    var task: Task? = null,
    private var path: List<Location> = listOf(),
    var beingAssigned: Boolean = false,
) {
    fun execute(context: Context) {
        if (battery.level <= 0) {
            status = Status.RECHARGING
        }

        when (status) {
            Status.IDLE -> prepareForTask(context)
            Status.MOVING -> moveToNextLocation(context)
            Status.WORKING -> performTaskAction()
            Status.RECHARGING -> recharge(context)
        }
    }

    private fun isWithinOneCellOf(taskLocation: Location): Boolean {
        return abs(location.x - taskLocation.x) <= 1 && abs(location.y - taskLocation.y) <= 1
    }

    private fun moveTo(target: Location) {
        location = target
    }

    /*
     * Does nothing if the robot has no tasks assigned.
     * When the robot has an assigned task, its status changes to moving.
     */
    private fun prepareForTask(context: Context) {
        if (task == null) return
        path = aStar(location, task!!.location, context)
        status = Status.MOVING
    }

    private fun moveToNextLocation(context: Context) {
        if (path.isEmpty()) {
            path = aStar(location, task!!.location, context)
            if (path.isEmpty()) {
                status = Status.IDLE
                return
            }
        }
        val nextStep = path.first()
        location = nextStep
        path = path.drop(1)
        battery.level -= movementCapabilities.movementCost
        // If the robot is equipped with a lidar, it scans the environment
        if (devices.any { it is LIDAR }) {
            devices.filterIsInstance<LIDAR>().forEach { it.execute(location, context) }
        }
        if (task != null && isWithinOneCellOf(task!!.location)) {
            status = Status.WORKING
        }
    }

    private fun performTaskAction() {
        task?.workload = (task?.workload ?: 0.0) - devices.filterIsInstance<Arm>().first().workingSpeed
        battery.level -= devices.filterIsInstance<Arm>().first().workingCost
        if ((task?.workload ?: 0.0) <= 0) {
            task?.isComplete = true
            task = null
            status = Status.IDLE
        }
    }

    private fun recharge(context: Context) {
        if (path.isEmpty() || path.last() != home) path = aStar(location, home, context)
        if (location == home) {
            battery.level = battery.capacity
            task = null
            status = Status.IDLE
        } else {
            moveToNextLocation(context)
        }
    }
}

class MovementCapability (
    val payloadWeight: Double,
    val maxSpeed: Double,
    val movementCost: Double = 1.0,
)

class Battery (
    var level: Double = 100.0,
    val capacity: Double = 100.0,
)

open class Device (
    val id: UUID,
    val name: String,
    val supportedActions: List<Action>,
) {
    fun supports(action: Action): Boolean {
        return action in supportedActions
    }
}

class Arm (
    val workingSpeed: Double,
    val workingCost: Double = 5.0,
) : Device(
    id = UUID.randomUUID(),
    name = "Arm",
    supportedActions = listOf(Action.WORK),
)

class LIDAR (
    private val detectionRange: Double,
) : Device(
    id = UUID.randomUUID(),
    name = "LIDAR",
    supportedActions = listOf(Action.DETECTION),
) {
    fun execute(robotLocation: Location, context: Context) {
        val findings = mutableMapOf<Location, CellType>()

        // Check task locations against detection range
        context.taskLocations.forEach { taskLocation ->
            if (isWithinRange(robotLocation, taskLocation) && !context.isKnownTask(taskLocation)) {
                findings[taskLocation] = CellType.TASK
            }
        }
        // Check if each obstacle against detection range
        context.obstacles.forEach { obstacle ->
            obstacle.shell.forEach { obstacleLocation ->
                if (isWithinRange(robotLocation, obstacleLocation) && !context.isKnownObstacle(obstacleLocation)) {
                    findings[obstacleLocation] = CellType.OBSTACLE
                }
            }
        }

        findings.forEach() { (location, cellType) ->
            context.knownLocations[location] = cellType
        }
    }

    private fun isWithinRange(robotLocation: Location, targetLocation: Location): Boolean {
        val distance = sqrt(((robotLocation.x - targetLocation.x).toDouble().pow(2)) +
                    ((robotLocation.y - targetLocation.y).toDouble().pow(2)))
        return distance <= detectionRange
    }
}

enum class Action {
    MOVE_GROUND,
    WORK,
    DETECTION,
}

enum class Status {
    IDLE,
    MOVING,
    WORKING,
    RECHARGING,
}
