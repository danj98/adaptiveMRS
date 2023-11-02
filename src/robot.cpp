//
// Created by djh on 11/1/23.
//

#include <iostream>
#include <utility>
#include "robot.h"
#include "point.h"
#include <cmath>
#include <yaml-cpp/node/node.h>

void Robot::move(Point newPosition) {
    int distance = sqrt(pow(newPosition.x - currentPosition.x, 2) + pow(newPosition.y - currentPosition.y, 2));
    battery -= distance;
    if (battery < 0) {
        std::cout << name << " is out of battery!" << std::endl;
        active = false;
        return;
    }
    currentPosition = newPosition;
    std::cout << name << " new position [" << newPosition.x << ", " << newPosition.y << "]";
}

Point Robot::getPosition() const {
    return currentPosition;
}

int Robot::getBattery() const {
    return battery;
}

void Robot::setName(std::string n) {
    name = n;
}