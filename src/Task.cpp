//
// Created by djh on 11/7/23.
//

#include "Task.h"

int Task::getX() const {
    return x;
}

int Task::getY() const {
    return y;
}

int Task::getCost() const {
    return cost;
}

void Task::setX(int x) {
    Task::x = x;
}

void Task::setY(int y) {
    Task::y = y;
}

void Task::setCost(int cost) {
    Task::cost = cost;
}

const std::string &Task::getName() const {
    return name;
}

void Task::setName(const std::string &name) {
    Task::name = name;
}

