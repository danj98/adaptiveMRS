//
// Created by djh on 11/1/23.
//

#include <iostream>
#include <utility>
#include "Robot.h"
#include "point.h"
#include "LocalMissionManager.h"
#include <cmath>
#include <yaml-cpp/node/node.h>

void Robot::move(Cell newPosition) {
    int distance = sqrt(pow(newPosition.x - currentPosition.x, 2) + pow(newPosition.y - currentPosition.y, 2));
    battery -= distance;
    if (battery < 0) {
        std::cout << name << " is out of battery!" << std::endl;
        active = false;
        return;
    }
    currentPosition = newPosition;
    //std::cout << name << " new position [" << newPosition.x << ", " << newPosition.y << "]";
    printf("%s new position [%d, %d]\n", name.c_str(), newPosition.x, newPosition.y);
}

Cell Robot::getPosition() const {
    return currentPosition;
}

int Robot::getBattery() const {
    return battery;
}

void Robot::setName(std::string n) {
    name = std::move(n);
}

bool Robot::canPerformTask(RobotCapabilities task) const {
    return (capabilities & task) == task;
}

void Robot::recharge(int amount) {
    battery += amount;
}

bool Robot::isActive() const {
    return active;
}

int Robot::getMovementCost() const {
    return movementCost;
}

std::vector<Cell> Robot::getRoute() const {
    return route;
}

void Robot::setRoute(std::vector<Cell> r) {
    route = std::move(r);
}

/*
std::vector<Cell> Robot::getNeighbors() const {
    std::vector<Cell> neighbors;
    neighbors.emplace_back(currentPosition.x - 1, currentPosition.y);
    neighbors.emplace_back(currentPosition.x + 1, currentPosition.y);
    neighbors.emplace_back(currentPosition.x, currentPosition.y - 1);
    neighbors.emplace_back(currentPosition.x, currentPosition.y + 1);
    return neighbors;
}
*/
