//
// Created by djh on 11/10/23.
//

#include "LocalMissionManager.h"
#include <queue>

void LocalMissionManager::run() {

}

int LocalMissionManager::calculateHeuristic(const Cell &a, const Cell &b) {
    return static_cast<int>(std::hypot(a.x - b.x, a.y - b.y));
}

std::vector<Cell> LocalMissionManager::findRoute(const std::vector<std::vector<CellType>>& grid, const Cell& start, const Cell& goal) {
    const int rows = static_cast<int>(grid.size());
    const int cols = static_cast<int>(grid[0].size());

    // Priority queue to store nodes based on their total cost (f)
    std::priority_queue<Node, std::vector<Node>, std::greater<Node>> openSet;

    // Set to store visited nodes
    std::vector<std::vector<bool>> visited(rows, std::vector<bool>(cols, false));

    // Vector to store the parent of each cell in the final path
    std::vector<std::vector<Cell>> parent(rows, std::vector<Cell>(cols, Cell(-1, -1, CellType::EMPTY)));

    // Start node
    Node startNode(start.x, start.y, 0, calculateHeuristic(start, goal));
    openSet.push(startNode);

    while (!openSet.empty()) {
        // Get the node with the lowest total cost (f)
        Node current = openSet.top();
        openSet.pop();

        // Check if the goal is reached
        if (current.x == goal.x && current.y == goal.y) {
            // Reconstruct the path from the goal to the start
            std::vector<Cell> path;
            while (current.x != -1 && current.y != -1) {
                path.push_back(Cell(current.x, current.y, CellType::EMPTY));
                current = Node(parent[current.x][current.y].x, parent[current.x][current.y].y, current.g, current.h);
            }
            std::reverse(path.begin(), path.end());
            return path;
        }

        // Mark the current node as visited
        visited[current.x][current.y] = true;

        // Define possible movements (up, down, left, right)
        const int dx[] = {0, 0, -1, 1};
        const int dy[] = {-1, 1, 0, 0};

        // Before entering the main loop, create a vector to store elements temporarily
        std::vector<Node> openSetVector;

        // Copy the elements from the priority queue to the vector
        while (!openSet.empty()) {
            openSetVector.push_back(openSet.top());
            openSet.pop();
        }

        // Perform the search on the vector
        for (const auto& neighbor : openSetVector) {
            for (int i = 0; i < 4; ++i) {
                int newX = neighbor.x + dx[i];
                int newY = neighbor.y + dy[i];

                // Check if the new position is within bounds and not an obstacle
                if (newX >= 0 && newX < rows && newY >= 0 && newY < cols && grid[newX][newY] != CellType::OBSTACLE && !visited[newX][newY]) {
                    // Calculate the cost from start to the new node
                    int newG = neighbor.g + 1; // Assuming each move has a cost of 1

                    // Create a new node
                    Node newNeighbor(newX, newY, newG, calculateHeuristic(Cell(newX, newY, CellType::EMPTY), goal));

                    // If the new node is not in the open set or has a lower total cost, update it
                    if (std::find_if(openSetVector.cbegin(), openSetVector.cend(), [newNeighbor](const Node& n) {
                        return n.x == newNeighbor.x && n.y == newNeighbor.y && n.f() <= newNeighbor.f();
                    }) == openSetVector.cend()) {
                        openSet.push(newNeighbor);
                        parent[newX][newY] = Cell(neighbor.x, neighbor.y, CellType::EMPTY);
                    }
                }
            }
        }

        // Restore the elements back to the priority queue
        for (const auto& node : openSetVector) {
            openSet.push(node);
        }
    }

    // If no path is found, return an empty path
    return {};
}

/*
 * Get bid based on cost of path
 */
int LocalMissionManager::getBid() {
    // Get the current position of the robot
    Cell currentPosition = robot.getPosition();

    // Get the current mission

    // Get the current map
    std::vector<std::vector<CellType>> grid = map.getGrid();

    // Get the goal position
    Cell goalPosition = currentMission.goal;

    // Find the path from the current position to the goal position
    std::vector<Cell> path = findRoute(grid, Cell(currentPosition.x, currentPosition.y, CellType::EMPTY), Cell(goalPosition.x, goalPosition.y, CellType::EMPTY));

    // Calculate the cost of the path
    int cost = 0;
    for (const auto& cell : path) {
        if (cell.type == CellType::EMPTY) {
            cost++;
        }
    }

    // Calculate the bid
    bid = cost * robot.getMovementCost();
    robot.setRoute(path);

    return bid;
}