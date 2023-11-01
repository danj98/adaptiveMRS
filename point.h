//
// Created by djh on 11/1/23.
//

#ifndef ADAPTIVEMRS_POINT_H
#define ADAPTIVEMRS_POINT_H

#include <utility>

struct Point {
    Point(int x, int y) {
        this->x = x;
        this->y = y;
    }
    Point() = default;

    int x;
    int y;

    bool operator==(const Point& other) const {
        return x == other.x && y == other.y;
    }
};

#endif //ADAPTIVEMRS_POINT_H
