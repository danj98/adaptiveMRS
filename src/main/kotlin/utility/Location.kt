package adaptiveMRS.utility

import adaptiveMRS.mission.Context
import kotlin.math.pow
import kotlin.math.sqrt

data class Location(
    val x: Int = 0,
    val y: Int = 0
) {
    fun distanceTo(other: Location, context: Context): Int {
        val distance = aStar(this, other, context).size
        return distance
    }

    fun euclideanDistanceTo(other: Location): Double {
        return sqrt((x - other.x).toDouble().pow(2) + (y - other.y).toDouble().pow(2))
    }

    fun neighbors(context: Context): List<Location> {
        val neighbours = mutableListOf<Location>()
        if (x > 0) neighbours.add(Location(x - 1, y))
        if (x < context.width - 1) neighbours.add(Location(x + 1, y))
        if (y > 0) neighbours.add(Location(x, y - 1))
        if (y < context.height - 1) neighbours.add(Location(x, y + 1))
        return neighbours
    }
}