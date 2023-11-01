//
// Created by djh on 11/1/23.
//

#include <iostream>
#include <utility>
#include "robot.h"
#include "point.h"
#include <cmath>

class Robot {
private:
    std::string name;
    int id;
    int battery;
    Point currentPosition;
public:
    Robot(std::string robotName, int robotId, int initialBattery, Point initialPosition)
        : name(robotName), id(robotId), battery(initialBattery), currentPosition(initialPosition) {}


    void move(Point newPosition) {
        int distance = sqrt(pow(newPosition.x - currentPosition.x, 2) + pow(newPosition.y - currentPosition.y, 2));
        battery -= distance;
        currentPosition = newPosition;
        std::cout << name << " new position [" << newPosition.x << ", " << newPosition.y << "]";

    }
};