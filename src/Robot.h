//
// Created by djh on 11/1/23.
//

#ifndef ADAPTIVEMRS_ROBOT_H
#define ADAPTIVEMRS_ROBOT_H

#include <yaml-cpp/node/node.h>
#include "point.h"
#include "Mission.h"
#include "Cell.h"

enum RobotCapabilities {
    GROUND_BASED = 1 << 0,  // 0001
    FLYING = 1 << 1,        // 0010
    PICK_UP = 1 << 2,       // 0100
    TRANSPORT = 1 << 3,     // 1000
};

class Robot {
public:
    Robot(std::string robotName,
          int robotId,
          int initialBattery,
          Cell initialPosition,
          bool active,
          int capabilities) :
        name(std::move(robotName)),
        id(robotId),
        battery(initialBattery),
        currentPosition(initialPosition),
        active(active),
        capabilities(capabilities){}

    void setName(std::string n);
    void move(Cell d);
    void recharge(int amount);
    Cell getPosition() const;
    int getBattery() const;
    bool canPerformTask(RobotCapabilities task) const;
    bool isActive() const;
    std::vector<Cell> getNeighbors() const;
    int getMovementCost() const;
    std::vector<Cell> getRoute() const;
    void setRoute(std::vector<Cell> r);


private:
    std::string name;
    int id;
    int battery;
    Cell currentPosition;
    bool active;
    int capabilities;
    int movementCost = 1;
    std::vector<Cell> route;
};


#endif //ADAPTIVEMRS_ROBOT_H
