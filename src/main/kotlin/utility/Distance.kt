package adaptiveMRS.utility

import adaptiveMRS.mission.CellType
import adaptiveMRS.mission.Context
import kotlin.math.abs
import kotlin.math.pow
import java.util.PriorityQueue

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

fun aStar(from: Location, to: Location, context: Context): List<Location> {
    val openList = mutableListOf<Node>()
    val closedList = mutableListOf<Node>()
    val startNode = Node(from, g = 0, h = heuristic(from, to))
    openList.add(startNode)

    while (openList.isNotEmpty()) {
        val currentNode = openList.minByOrNull { it.f } ?: break
        println(currentNode.location)
        if (currentNode.location == to) {
            return generatePath(currentNode)
        }

        openList.remove(currentNode)
        closedList.add(currentNode)

        getNeighbors(currentNode, context, to).forEach { neighbor ->
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

fun getNeighbors(node: Node, context: Context, to: Location): List<Node> {
    val directions = listOf(
        Location(1, 0), Location(0, 1), Location(-1, 0), Location(0, -1),
        Location(1, 1), Location(-1, 1), Location(1, -1), Location(-1, -1)
    )
    return directions.mapNotNull { dir ->
        val newLoc = Location(node.location.x + dir.x, node.location.y + dir.y)
        if (newLoc.x in 0 until context.width && newLoc.y in 0 until context.height && !context.isObstacle(newLoc)) {
            Node(newLoc, h = heuristic(newLoc, to)).takeUnless { it.location == node.location }
        } else null
    }
}
*/

fun euclideanDistance(from: Location, to: Location): Double {
    return kotlin.math.sqrt((from.x - to.x).toDouble().pow(2) + (from.y - to.y).toDouble().pow(2))
}

/*
 * A* algorithm for path planning based on the knownLocations map
 */

fun aStar(from: Location, to: Location, context: Context): List<Location> {
    if (context.knownLocations[from] == CellType.OBSTACLE || context.knownLocations[to] == CellType.OBSTACLE) {
        println("Start or end location is blocked.")
        return emptyList()
    }

    val openList = PriorityQueue(compareBy<Node> { it.f })
    val closedSet = mutableSetOf<Location>()
    val startNode = Node(from, g = 0, h = heuristic(from, to))
    openList.add(startNode)

    while (openList.isNotEmpty()) {
        val currentNode = openList.poll()
        if (currentNode.location == to) {
            return generatePath(currentNode)
        }

        closedSet.add(currentNode.location)

        getNeighbors(currentNode, context).forEach { neighbor ->
            if (neighbor.location in closedSet) return@forEach
            val tentativeG = currentNode.g + distance(currentNode.location, neighbor.location)

            if (openList.none { it.location == neighbor.location && tentativeG >= it.g }) {
                neighbor.parent = currentNode
                neighbor.g = tentativeG
                neighbor.h = heuristic(neighbor.location, to)
                openList.add(neighbor)
            }
        }
    }
    return emptyList()
}

fun distance(from: Location, to: Location): Int {
    return if (from.x != to.x && from.y != to.y) 14 else 10
}

fun getNeighbors(node: Node, context: Context): List<Node> {
    val directions = listOf(
        Location(1, 0), Location(0, 1), Location(-1, 0), Location(0, -1)
        //Location(1, 1), Location(-1, 1), Location(1, -1), Location(-1, -1)
    )
    return directions.mapNotNull { dir ->
        val newLoc = Location(node.location.x + dir.x, node.location.y + dir.y)
        if (newLoc.x in 0 until context.width && newLoc.y in 0 until context.height && !context.isKnownObstacle(newLoc)) {
            Node(newLoc, h = heuristic(newLoc, node.location))
        } else null
    }
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
