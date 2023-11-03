//
// Created by djh on 11/2/23.
//

#include "Map.h"
#include "point.h"

void Map::addObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        grid[y][x] = true;
    }
}

void Map::removeObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        grid[y][x] = false;
    }
}

bool Map::hasObstacle(int x, int y) {
    if(isValidCell(x, y)) {
        return grid[y][x];
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