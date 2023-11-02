//
// Created by djh on 11/1/23.
//

#ifndef ADAPTIVEMRS_ROBOT_H
#define ADAPTIVEMRS_ROBOT_H

#include <yaml-cpp/node/node.h>
#include "point.h"

class Robot {
public:
    Robot(std::string robotName, int robotId, int initialBattery, Point initialPosition)
        : name(std::move(robotName)), id(robotId), battery(initialBattery), currentPosition(initialPosition) {}

    void setName(std::string n);
    void move(Point d);
    Point getPosition() const;
    int getBattery() const;

private:
    std::string name;
    int id;
    int battery;
    Point currentPosition;
    bool active;
};


#endif //ADAPTIVEMRS_ROBOT_H
