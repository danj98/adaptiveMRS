package adaptiveMRS

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.mission.Task
import adaptiveMRS.mission.TaskDependency
import adaptiveMRS.robot.*
import adaptiveMRS.simulation.Environment
import adaptiveMRS.simulation.generateMission
import adaptiveMRS.utility.Location
import java.util.*

fun main() {

    var context: Context
    var robots: List<Robot>
    var mission: Mission

    val seed = 123L

    generateMission(100 to 100, 50, 10, seed).let {
        context = it.first
        mission = it.second
        robots = it.third
    }

    val env = Environment(
        robots = robots,
        mission = mission,
        initialContext = context
    )

    env.run()
}