package adaptiveMRS.utility

import adaptiveMRS.simulation.StateAction
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

fun serializeQTable(qTable: Map<StateAction, Double>, filename: String) {
    val json = Json { allowStructuredMapKeys = true }
    val jsonString = json.encodeToString(qTable)
    File(filename).writeText(jsonString)
}

fun deserializeQTable(filename: String): MutableMap<StateAction, Double> {
    val jsonString = File(filename).readText()
    return Json.decodeFromString(jsonString)
}