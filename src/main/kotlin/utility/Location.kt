package adaptiveMRS.utility

import adaptiveMRS.mission.Context

data class Location(
    val x: Int = 0,
    val y: Int = 0
) {
    fun distanceTo(other: Location, context: Context): Int {
        val distance = aStar(this, other, context).size
        return distance
    }
}