package adaptiveMRS.utility

import adaptiveMRS.mission.*
import java.util.UUID
import com.google.gson.Gson
import adaptiveMRS.robot.*
import adaptiveMRS.simulation.*
import adaptiveMRS.utility.Location
import kotlin.random.Random


data class JsonRobot(
    val name: String,
    val location: JsonLocation,
    val devices: List<JsonDevice>
)

data class JsonDevice(
    val deviceType: String,
    val workingSpeed: Double?,
    val detectionRange: Double?
)

data class JsonLocation(
    val lat: Double,
    val lng: Double
)

data class JsonMission(
    val robots: List<JsonRobot>,
    val tasks: List<JsonTask>,
    val obstacles: List<List<JsonLocation>>
)

data class JsonTask(
    val name: String,
    val location: JsonLocation,
    val workload: Double,
    val dependency: String,
    val actionType: Action?
)


fun createMissionFromJson(json: String): Environment {
    val gson = Gson()
    val jsonMission = gson.fromJson(json, JsonMission::class.java)

    val robots = jsonMission.robots.map { jsonRobot ->
        val lng = jsonRobot.location.lng.toInt()
        val lat = jsonRobot.location.lat.toInt()
        Robot(
            id = Random.nextInt(),
            location = Location(lat, lng),
            devices = jsonRobot.devices.map { device ->
                when (device.deviceType) {
                    "Arm" -> Arm(workingSpeed = device.workingSpeed!!)
                    "LIDAR" -> LIDAR(detectionRange = device.detectionRange!!)
                    else -> throw IllegalArgumentException("Unsupported device type")
                }
            },
            battery = Battery(100.0),
            beingAssigned = false,
            home = Location(lat, lng),
        )
    }

    val obstacles = jsonMission.obstacles.map { obstacle ->
        Obstacle(obstacle.map { Location(it.lat.toInt(), it.lng.toInt()) }.toMutableList())
    }.toMutableList()
    val context = Context.create(
        0,
        10,
        10,
        obstacles,
        mutableMapOf(),
        mutableListOf()
    )

    val tasks = jsonMission.tasks.map { JsonTask ->
        val lng = JsonTask.location.lng.toInt()
        val lat = JsonTask.location.lat.toInt()
        Task(
            location = Location(lat, lng),
            workload = JsonTask.workload,
            actionType = JsonTask.actionType ?: Action.WORK,
            dependencies = mutableListOf(),
            assignedRobots = mutableListOf(),
            isComplete = false,
            timesAssigned = 0,
            id = Random.nextInt()
        )
    }

    val mission = Mission(
        id = Random.nextInt(),
        tasks = tasks
    )

    return Environment(mission = mission, robots = robots, initialContext = context, "qLearning")
}
