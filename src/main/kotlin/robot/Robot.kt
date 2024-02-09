package adaptiveMRS.robot

import adaptiveMRS.mission.CellType
import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Task
import adaptiveMRS.utility.Location
import adaptiveMRS.utility.aStar
import java.util.UUID
import kotlin.math.abs

class Robot (
    val id: Int,
    val movementCapabilities: MovementCapability,
    val battery: Battery,
    val devices: List<Device>,
    var currentLocation: Location,
    val home: Location,
    var status: Status = Status.IDLE,
    var task: Task? = null,
    private var path: List<Location> = listOf(),
) {
    fun execute(context: Context) {
        when (status) {
            Status.IDLE -> prepareForTask(context)
            Status.MOVING -> moveToNextLocation()
            Status.WORKING -> performTaskAction()
        }
    }

    private fun isWithinOneCellOf(taskLocation: Location): Boolean {
        return abs(currentLocation.x - taskLocation.x) <= 1 && abs(currentLocation.y - taskLocation.y) <= 1
    }

    private fun moveTo(target: Location) {
        currentLocation = target
    }

    /*
     * Does nothing if the robot has no tasks assigned.
     * When the robot has an assigned task, its status changes to moving.
     */
    private fun prepareForTask(context: Context) {
        if (task == null) return
        path = aStar(currentLocation, task!!.referencePosition, context)
        status = Status.MOVING
    }

    private fun moveToNextLocation() {
        if (path.isNotEmpty()) {
            val nextStep = path.first()
            moveTo(nextStep)
            path = path.drop(1)
            // If the robot is equipped with a lidar, it scans the environment
            if (devices.any { it is LIDAR }) {
                devices.filterIsInstance<LIDAR>().forEach { it. }
            }
        }
        if (task != null && isWithinOneCellOf(task!!.referencePosition)) {
            status = Status.WORKING
        }
    }

    private fun performTaskAction() {
        if ((task?.workload ?: 0.0) <= 0) {
            task?.isComplete = true
            task = null
            status = Status.IDLE
        }
    }
}

class MovementCapability (
    val payloadWeight: Double,
    val maxSpeed: Double,
)

class Battery (
    val level: Double = 1.0,
    val capacity: Double,
    val rechargeTime: Double,
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
) : Device(
    id = UUID.randomUUID(),
    name = "Arm",
    supportedActions = listOf(Action.WORK),
)

class LIDAR (
    private val detectionRange: Double,
    private val discoveries: Map<Location, CellType> = mapOf(),
) : Device(
    id = UUID.randomUUID(),
    name = "LIDAR",
    supportedActions = listOf(Action.DETECTION),
) {
    fun execute(robotLocation: Location, context: Context) {
        val x = robotLocation.x
        val y = robotLocation.y
        val discoveries = mutableMapOf<Location, CellType>()
        for (i in -detectionRange.toInt()..detectionRange.toInt()) {
            for (j in -detectionRange.toInt()..detectionRange.toInt()) {
                val location = Location(x + i, y + j)
                if (context.knownLocations.containsKey(location)) {
                    discoveries[location] = context.knownLocations[location]!!
                }
            }
        }

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
    WORKING
}

interface DeviceAction {
    fun execute(robot: Robot, context: Context)
}

class LIDARAction(
    private val detectionRange: Int
) : DeviceAction {
    override fun execute(robot: Robot, context: Context) {

    }
}

class ArmAction(
    private val workingSpeed: Int
) : DeviceAction {
    override fun execute(robot: Robot, context: Context) {
    }
}
