package adaptiveMRS

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.mission.Task
import adaptiveMRS.mission.TaskDependency
import adaptiveMRS.robot.*
import adaptiveMRS.simulation.Environment
import adaptiveMRS.simulation.StateActionFeature
import adaptiveMRS.simulation.generateMission
import adaptiveMRS.simulation.trainQTable
import adaptiveMRS.utility.Location
import java.util.*

fun main() {

    var context: Context
    var robots: List<Robot>
    var mission: Mission

    val seed = 123L

    generateMission(100 to 100, 50, 10).let {
        context = it.first
        mission = it.second
        robots = it.third
    }

    val env = Environment(
        robots = robots,
        mission = mission,
        initialContext = context
    )

    var qTable = mutableMapOf<StateActionFeature, Double>()

    //env.run()
    qTable = trainQTable(1000)
    val its = env.run(qTable)
    println("Iterations: $its")
}