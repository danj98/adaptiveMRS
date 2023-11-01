//
// Created by djh on 11/1/23.
//

#ifndef ADAPTIVEMRS_ROBOT_H
#define ADAPTIVEMRS_ROBOT_H
#include "point.h"

class robot {
public:
    void setName(std::string n);
    void move(Point d);
    Point getPosition() const;
    int getBattery() const;

private:
    std::string name;
    int id;
    int battery;
    Point currentPosition;
};


#endif //ADAPTIVEMRS_ROBOT_H
