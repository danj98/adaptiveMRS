package adaptiveMRS

import adaptiveMRS.mission.Context
import adaptiveMRS.mission.Mission
import adaptiveMRS.mission.Obstacle
import adaptiveMRS.robot.*
import adaptiveMRS.simulation.*
import adaptiveMRS.utility.Location
import adaptiveMRS.utility.createMissionFromJson
import com.google.gson.Gson

fun main(args: Array<String>) {

    //testScalability()

    /*
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
     */
    // Get mission from JSON in argument
    //val (mission, robots, context) = parseMission(args[0])

    val env = createMissionFromJson(json)
    val (iterations, _) = env.run()
    println("Mission completed in $iterations iterations")
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

/*
Example JSON to turn into mission:
*/
val json = """{
  "robots": [
    {
      "name": "hans",
      "location": {
        "lat": 51.504646690284794,
        "lng": -0.092010498046875
      },
      "devices": [
        {
          "deviceType": "Arm",
          "workingSpeed": 1
        },
        {
          "deviceType": "LIDAR",
          "detectionRange": 5
        }
      ]
    },
    {
      "name": "nils",
      "location": {
        "lat": 51.50551929916671,
        "lng": -0.10797500610351564
      },
      "devices": [
        {
          "deviceType": "Arm",
          "workingSpeed": 1
        },
        {
          "deviceType": "LIDAR",
          "detectionRange": 5
        }
      ]
    }
  ],
  "tasks": [
    {
      "name": "name",
      "location": {
        "lat": 51.51395957079169,
        "lng": -0.09939193725585939
      },
      "workload": 5,
      "dependency": "",
      "actionType": "Work"
    },
    {
      "name": "task2",
      "location": {
        "lat": 51.5090451724131,
        "lng": -0.07450103759765626
      },
      "workload": 10,
      "dependency": "name",
      "actionType": "Work"
    }
  ],
  "obstacles": [
    [
      {
        "lat": 51.501779440106596,
        "lng": -0.08771896362304689
      },
      {
        "lat": 51.49739807006878,
        "lng": -0.09046554565429689
      },
      {
        "lat": 51.50071085210534,
        "lng": -0.10162353515625
      },
      {
        "lat": 51.50530560274019,
        "lng": -0.09115219116210939
      }
    ]
  ]
}"""


