//
// Created by djh on 11/7/23.
//

#ifndef ADAPTIVEMRS_GLOBALMISSIONMANAGER_H
#define ADAPTIVEMRS_GLOBALMISSIONMANAGER_H


#include <vector>
#include <yaml-cpp/node/node.h>
#include "Mission.h"
#include "Robot.h"
#include "Map.h"

struct BidResult {
    Robot selectedRobot;
    double highestBid;
};

class GlobalMissionManager {
public:
    GlobalMissionManager(
            std::vector<Robot> robots,
            Mission mission,
            Map map) :
            robots(robots),
            currentMission(mission),
            map(map){}

    void setMission(const YAML::Node& missionNode);
    void run();
    bool isValidMission(Mission mission);
    BidResult getHighestBid(std::vector<Robot> robots, Mission mission, Map map);
    void assignMission(Robot robot, Mission mission);



private:
    Mission currentMission;
    std::vector<Robot> robots;
    Map map;
};


#endif //ADAPTIVEMRS_GLOBALMISSIONMANAGER_H
