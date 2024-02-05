package adaptiveMRS

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.mission.Task
import adaptiveMRS.mission.TaskDependency
import adaptiveMRS.robot.*
import adaptiveMRS.simulation.Environment
import adaptiveMRS.utility.Location
import java.util.*

fun main() {
    // Set up mission
    val mission = Mission(
        id = UUID.randomUUID(),
        tasks = listOf(
            Task(
                id = 1,
                workload = 1.0,
                referencePosition = Location(9, 0),
                actionType = Action.WORK,
                assignedRobots = mutableListOf(),
                dependencies = mutableListOf(
                    TaskDependency(
                        from = null,
                        to = null
                    )
                )
            ),
            Task(
                id = 2,
                workload = 1.0,
                referencePosition = Location(7, 5),
                actionType = Action.WORK,
                assignedRobots = mutableListOf(),
                dependencies = mutableListOf()
            ),
            Task(
                id = 3,
                workload = 1.0,
                referencePosition = Location(5, 3),
                actionType = Action.WORK,
                assignedRobots = mutableListOf(),
                dependencies = mutableListOf()
            )
        ),
        completedTasks = emptyList()
    )

    // Set up robots
    val robots = listOf(
        Robot(
            id = 1,
            movementCapabilities = MovementCapability(
            payloadWeight = 1.0,
            maxSpeed = 1.0
        ),
        battery = Battery(
            capacity = 1.0,
            rechargeTime = 1.0
        ),
        devices = listOf(
            Device(
                id = UUID.randomUUID(),
                name = "Arm",
                supportedActions = listOf(Action.WORK)
            )
        ),
        home = Location(0, 0),
        currentLocation = Location(0, 0)
        ),
        Robot(
            id = 2,
            movementCapabilities = MovementCapability(
                payloadWeight = 1.0,
                maxSpeed = 1.0
            ),
            battery = Battery(
                capacity = 1.0,
                rechargeTime = 1.0
            ),
            devices = listOf(
                Device(
                    id = UUID.randomUUID(),
                    name = "Arm",
                    supportedActions = listOf(Action.WORK)
                )
            ),
            home = Location(0, 0),
            currentLocation = Location(0, 0)
        ),
        Robot(
            id = 3,
            movementCapabilities = MovementCapability(
                payloadWeight = 1.0,
                maxSpeed = 1.0
            ),
            battery = Battery(
                capacity = 1.0,
                rechargeTime = 1.0
            ),
            devices = listOf(
                Device(
                    id = UUID.randomUUID(),
                    name = "Arm",
                    supportedActions = listOf(Action.WORK)
                )
            ),
            home = Location(9, 9),
            currentLocation = Location(9, 9)
        )
    )

    val context = Context(
        id = UUID.randomUUID(),
        width = 10,
        height = 10,
        obstacles = emptyList(),
    )

    val env = Environment(
        robots = robots,
        mission = mission,
        initialContext = context
    )

    env.run()
}