package adaptiveMRS.utility

import adaptiveMRS.simulation.StateActionFeature
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.io.File

fun serializeQTable(qTable: Map<StateActionFeature, Double>, filename: String) {
    val json = Json { allowStructuredMapKeys = true }
    val jsonString = json.encodeToString(qTable)
    File(filename).writeText(jsonString)
}

fun deserializeQTable(filename: String): MutableMap<StateActionFeature, Double> {
    val jsonString = File(filename).readText()
    return Json.decodeFromString(jsonString)
}