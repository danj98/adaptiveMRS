package adaptiveMRS

import adaptiveMRS.simulation.*

fun main() {

    //testScalability()

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
        }
    }
}

/*
 * Test runtime of the Q-learning algorithm
 */
fun testScalability() {
    val numMissions = 10
    val qTable = mutableMapOf<StateAction, Double>()

    val runtimes = mutableMapOf<Int, MutableList<Int>>()

    var numRobots = 1
    while (numRobots <= 100) {
        for (i in 1..numMissions) {
            val generator = MissionGenerator(Pair(100, 100), numRobots*5, numRobots)
            val (context, mission, robots) = generator.generate()
            val contextCopy = context.deepCopy()
            val missionCopy = mission.deepCopy()
            val robotsCopy = robots.map { it.copy() }

            val env = Environment(missionCopy, robotsCopy, contextCopy, "qLearning", qTable)
            val startTime = System.currentTimeMillis()
            val (iterations, _) = env.run()
            val endTime = System.currentTimeMillis()
            val runtime = endTime - startTime

            runtimes.getOrPut(numRobots) { mutableListOf() }.add(runtime.toInt())
        }
        println("Average time for $numRobots robots: ${runtimes[numRobots]?.average()} ms")
        numRobots = if (numRobots == 1) numRobots + 4 else numRobots + 5
    }

    println("Scalability test results")
    runtimes.forEach { (numRobots, times) ->
        val avgTime = times.average()
        println("Number of robots: $numRobots, Average time: $avgTime ms")
    }
}



