//
// Created by djh on 11/1/23.
//

#ifndef ADAPTIVEMRS_ROBOT_H
#define ADAPTIVEMRS_ROBOT_H

#include <yaml-cpp/node/node.h>
#include "point.h"

enum RobotCapabilities {
    GROUND_BASED = 1 << 0,
    FLYING = 1 << 1,
    PICK_UP = 1 << 2,
    TRANSPORT = 1 << 3,
};

class Robot {
public:
    Robot(std::string robotName,
          int robotId,
          int initialBattery,
          Point initialPosition,
          bool active,
          int capabilities) :
        name(std::move(robotName)),
        id(robotId),
        battery(initialBattery),
        currentPosition(initialPosition),
        active(active),
        capabilities(capabilities){}

    void setName(std::string n);
    void move(Point d);
    void recharge(int amount);
    Point getPosition() const;
    int getBattery() const;
    bool canPerformTask(RobotCapabilities task) const;
    bool isActive() const;

private:
    std::string name;
    int id;
    int battery;
    Point currentPosition;
    bool active;
    int capabilities;
};


#endif //ADAPTIVEMRS_ROBOT_H
