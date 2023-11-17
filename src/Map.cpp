//
// Created by djh on 11/2/23.
//

#include "Map.h"
#include "point.h"

void Map::addObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        grid[y][x] = CellType::OBSTACLE;
    }
}

void Map::removeObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        grid[y][x] = CellType::EMPTY;
    }
}

bool Map::isEmpty(int x, int y) {
    if(isValidCell(x, y)) {
        return grid[y][x] == CellType::EMPTY;
    }
    return false;
}

bool Map::hasObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        return grid[y][x] == CellType::OBSTACLE;
    }
    return false;
}

int Map::getWidth() const {
    return width;
}

int Map::getHeight() const {
    return height;
}

bool Map::isValidCell(int x, int y) const {
    return x >= 0 && x < width && y >= 0 && y < height;
}

void Map::setObstacles(const std::vector<Cell>& obstacleNodes) {
    for (const auto& node : obstacleNodes) {
        int x = node.x;
        int y = node.y;
        addObstacle(x, y);
    }
}

void Map::setRobotPosition(int x, int y) {
    if(isValidCell(x, y)) {
        if (grid[y][x] == CellType::OBSTACLE) {
            std::cerr << "Cannot place robot on obstacle!" << std::endl;
            return;
        }
        grid[y][x] = CellType::ROBOT;
    }
}

void Map::setTaskPosition(int x, int y) {
    if(isValidCell(x, y)) {
        if (grid[y][x] == CellType::OBSTACLE) {
            std::cerr << "Cannot place task on obstacle!" << std::endl;
            return;
        }
        grid[y][x] = CellType::TASK;
    }
}

void Map::displayMap() const {
    for (int i = 0; i < height; i++) {
        std::cout << "| ";
        for (int j = 0; j < width; j++) {
            if (grid[i][j] == CellType::EMPTY) {
                std::cout << " ";
            } else if (grid[i][j] == CellType::ROBOT) {
                std::cout << "R";
            } else if (grid[i][j] == CellType::TASK) {
                std::cout << "T";
            } else if (grid[i][j] == CellType::OBSTACLE) {
                std::cout << "O";
            }
            std::cout << " ";
        }
        std::cout << "|";
        std::cout << std::endl;
    }
}

std::vector<std::vector<CellType>> Map::getGrid() const {
    return grid;
}

/*
 * Displays the map with the route taken by the robot.
 * The route is displayed as an 'x' on the map (without overwriting the robot's or task position).
 */
void Map::displayMapWithRoute(const std::vector<Cell>& route) const {
    std::vector<std::vector<CellType>> gridCopy = grid;

    for (const auto& cell : route) {
        if (gridCopy[cell.y][cell.x] == CellType::EMPTY) {
            gridCopy[cell.y][cell.x] = CellType::ROUTE;
        }
    }

    for (int i = 0; i < height; i++) {
        std::cout << "| ";
        for (int j = 0; j < width; j++) {
            if (gridCopy[i][j] == CellType::EMPTY) {
                std::cout << " ";
            } else if (gridCopy[i][j] == CellType::ROBOT) {
                std::cout << "R";
            } else if (gridCopy[i][j] == CellType::TASK) {
                std::cout << "T";
            } else if (gridCopy[i][j] == CellType::OBSTACLE) {
                std::cout << "O";
            } else if (gridCopy[i][j] == CellType::ROUTE) {
                std::cout << "x";
            }
            std::cout << " ";
        }
        std::cout << "|";
        std::cout << std::endl;
    }
}