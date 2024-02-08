package adaptiveMRS.utility

import adaptiveMRS.mission.Context

data class Location(
    val x: Int,
    val y: Int
) {
    fun distanceTo(other: Location, context: Context): Int {
        return aStar(this, other, context).size
    }

    fun isObstacle(context: Context): Boolean {
        for (obstacle in context.obstacles) {
            if (x >= obstacle.shell[0].x && x <= obstacle.shell[2].x) {
                if (y >= obstacle.shell[0].y && y <= obstacle.shell[2].y) {
                    return true
                }
            }
        }
        return false
    }
}