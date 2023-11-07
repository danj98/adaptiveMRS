//
// Created by djh on 11/2/23.
//

#include "Map.h"
#include "point.h"

void Map::addObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        grid[y][x] = OBSTACLE;
    }
}

void Map::removeObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        grid[y][x] = false;
    }
}

bool Map::hasObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        return grid[y][x] == OBSTACLE;
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

void Map::setObstacles(const std::vector<Point>& obstacleNodes) {
    for (const auto& node : obstacleNodes) {
        int x = node.x;
        int y = node.y;
        addObstacle(x, y);
    }
}

void Map::setRobotPosition(int x, int y) {
    if(isValidCell(x, y)) {
        grid[y][x] = ROBOT;
    }
}

void Map::setTaskPosition(int x, int y) {
    if(isValidCell(x, y)) {
        grid[y][x] = TASK;
    }
}

void Map::displayMap() const {
    for (int i = 0; i < height; i++) {
        printf("|");
        for (int j = 0; j < width; j++) {
            if (grid[i][j] == EMPTY) {
                printf(" ");
            } else if (grid[i][j] == OBSTACLE) {
                printf("O");
            } else if (grid[i][j] == ROBOT) {
                printf("R");
            } else if (grid[i][j] == TASK) {
                printf("T");
            }
        }
        printf("|\n");
    }
}