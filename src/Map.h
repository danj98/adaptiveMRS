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


class Map {
public:
    Map() = default;
    Map(int width, int height) : width(width), height(height) {
        grid = std::vector<std::vector<bool>>(height, std::vector<bool>(width, false));

    }

    void addObstacle(int x, int y);
    void removeObstacle(int x, int y);
    bool hasObstacle(int x, int y);
    void setObstacles(const std::vector<Point>& obstacleNodes);

    int getWidth() const;
    int getHeight() const;

private:
    std::vector<std::vector<bool>> grid;
    int width;
    int height;

    bool isValidCell(int x, int y) const;
};


#endif //ADAPTIVEMRS_MAP_H
