package adaptiveMRS.robot

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
            Status.IDLE -> {
                if (task != null) {
                    path = aStar(currentLocation, task!!.referencePosition, context)
                    status = if (isWithinOneCellOf(task!!.referencePosition)) Status.WORKING else Status.MOVING
                }
            }
            Status.MOVING -> {
                if (path.isNotEmpty()) {
                    val nextStep = path.first()
                    moveTo(nextStep)
                    path = path.drop(1)
                    if (isWithinOneCellOf(task!!.referencePosition)) {
                        status = Status.WORKING
                    }
                }
            }
            Status.WORKING -> {
                if (task!!.workload <= 0) {
                    task!!.isComplete = true
                    status = Status.IDLE
                    task = null
                } else {
                    task!!.workload -= 0.5
                }
            }
        }
    }

    private fun isWithinOneCellOf(taskLocation: Location): Boolean {
        return abs(currentLocation.x - taskLocation.x) <= 1 && abs(currentLocation.y - taskLocation.y) <= 1
    }

    private fun moveTo(target: Location) {
        currentLocation = target
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

class Device (
    val id: UUID,
    val name: String,
    val supportedActions: List<Action>,
    val workingSpeed: Double = 1.0
) {
    fun supports(action: Action): Boolean {
        return action in supportedActions
    }
}

enum class Action {
    MOVE,
    WORK,
    DETECT_OBSTACLE,
}

enum class Status {
    IDLE,
    MOVING,
    WORKING
}
