package adaptiveMRS.utility

import adaptiveMRS.mission.Context
import kotlin.math.abs

fun distance(from: Location, to: Location, context: Context): Int {
    return aStar(from, to, context).size
}

data class Node(
    val location: Location,
    var g: Int = Int.MAX_VALUE,
    var h: Int = 0,
    var parent: Node? = null
) {
    val f: Int
        get() = g + h
}

/*
 * A* algorithm for path planning
 */
fun aStar(from: Location, to: Location, context: Context): List<Location> {
    val openList = mutableListOf<Node>()
    val closedList = mutableListOf<Node>()
    val startNode = Node(from, g = 0, h = heuristic(from, to))
    openList.add(startNode)

    while (openList.isNotEmpty()) {
        val currentNode = openList.minByOrNull { it.f } ?: break
        if (currentNode.location == to) {
            return generatePath(currentNode)
        }

        openList.remove(currentNode)
        closedList.add(currentNode)

        getNeighbors(currentNode, context).forEach { neighbor ->
            if (neighbor in closedList || context.isObstacle(neighbor.location)) return@forEach
            val tentativeG = currentNode.g + 1

            if (tentativeG < neighbor.g) {
                neighbor.parent = currentNode
                neighbor.g = tentativeG
                neighbor.h = heuristic(neighbor.location, to)
                if (neighbor !in openList) {
                    openList.add(neighbor)
                }
            }
        }
    }
    return emptyList()
}

fun heuristic(from: Location, to: Location): Int {
    return abs(from.x - to.x) + abs(from.y - to.y)
}

fun generatePath(node: Node): List<Location> {
    val path = mutableListOf<Location>()
    var current: Node? = node
    while (current != null) {
        path.add(0, current.location)
        current = current.parent
    }
    return path
}

fun getNeighbors(node: Node, context: Context): List<Node> {
    val directions = listOf(
        Location(1, 0), Location(0, 1), Location(-1, 0), Location(0, -1),
        Location(1, 1), Location(-1, 1), Location(1, -1), Location(-1, -1)
    )
    return directions.mapNotNull { dir ->
        val newLoc = Location(node.location.x + dir.x, node.location.y + dir.y)
        if (newLoc.x in 0 until context.width && newLoc.y in 0 until context.height) Node(newLoc) else null
    }
}