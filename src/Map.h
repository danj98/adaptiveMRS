//
// Created by djh on 11/2/23.
//

#ifndef ADAPTIVEMRS_MAP_H
#define ADAPTIVEMRS_MAP_H
#include <iostream>
#include "yaml-cpp/yaml.h"
#include "point.h"
#include <stdio.h>
#include <fstream>
#include "Cell.h"


class Map {
public:

    Map() = default;
    Map(int width, int height) : width(width), height(height) {
        grid.resize(height);
        for (int i = 0; i < height; i++) {
            grid[i].resize(width);
            for (int j = 0; j < width; j++) {
                grid[i][j]  = CellType::EMPTY;
            }
        }
    }

    void addObstacle(int x, int y);
    void removeObstacle(int x, int y);
    bool isEmpty(int x, int y);
    bool hasObstacle(int x, int y);
    void setObstacles(const std::vector<Cell>& obstacleNodes);
    void setRobotPosition(int x, int y);
    void setTaskPosition(int x, int y);

    int getWidth() const;
    int getHeight() const;
    std::vector<std::vector<CellType>> getGrid() const;

    void displayMap() const;
    void displayMapWithRoute(const std::vector<Cell>& route) const;

private:
    std::vector<std::vector<CellType>> grid;
    int width;
    int height;

    bool isValidCell(int x, int y) const;
};


#endif //ADAPTIVEMRS_MAP_H
