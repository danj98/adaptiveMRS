package adaptiveMRS

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.mission.Task
import adaptiveMRS.robot.*
import adaptiveMRS.simulation.*
import java.util.*
import kotlinx.serialization.*

fun main() {
    val numMissions = 10
    val trainingCycles = 10
    val missionsPerTraining = 1000
    val qTable = mutableMapOf<StateAction, Double>()

    val methods = listOf("random", "market", "qLearning")

    val averageScores = mutableMapOf<String, MutableList<Int>>()

    methods.forEach { method ->
        averageScores[method] = mutableListOf()
    }

    // Initial results
    runMissions(methods, numMissions, qTable, averageScores)

    methods.forEach { method ->
        val avgScore = averageScores[method]?.average() ?: 0.0
        println("Method: ${method}, Average Score: $avgScore")
    }

    for (cycle in 1..trainingCycles) {
        trainQTable(missionsPerTraining, qTable)

        averageScores.forEach { (_, scores) -> scores.clear() }

        runMissions(methods, numMissions, qTable, averageScores)

        println("After cycle $cycle")
        methods.forEach { method ->
            val avgScore = averageScores[method]?.average() ?: 0.0
            println("Method: ${method}, Average Score: $avgScore")
        }
    }
}

fun runMissions(
    methods: List<String>,
    numMissions: Int,
    qTable: MutableMap<StateAction, Double>,
    averageScores: MutableMap<String, MutableList<Int>>
) {

    for (i in 1..numMissions) {
        val generator = MissionGenerator(Pair(100, 100), 50, 10)
        val (c, mis, robs) = generator.generate()

        methods.forEach { method ->
            val missionCopy = mis.deepCopy()
            val robotsCopy = robs.map { it.copy() }
            val contextCopy = c.deepCopy()

            val env = Environment(missionCopy, robotsCopy, contextCopy, method, qTable)
            val (iterations, _) = env.run()
            averageScores[method]?.add(iterations)
            //generator.printMap(contextCopy)
        }
    }
}


