//
// Created by djh on 11/7/23.
//

#include "GlobalMissionManager.h"
#include "Map.h"
#include "LocalMissionManager.h"

void GlobalMissionManager::setMission(const YAML::Node &missionNode) {
    //Mission mission;
    /*
    for (const auto& taskNode : missionNode["tasks"]) {
        Task task;
        task.setX(taskNode["x"].as<int>());
        task.setY(taskNode["y"].as<int>());
        task.setCost(taskNode["cost"].as<int>());
        task.setName(taskNode["name"].as<std::string>());
        mission.addTask(task);
    }
    Task goal;
*/
}

/*
 * Runs the currently set mission
 */
void GlobalMissionManager::run() {

}

/*
 * Finds the highest bid from a local mission manager and returns the robot.
 */
BidResult GlobalMissionManager::getHighestBid(std::vector<Robot> robots, Mission mission, Map map) {
    BidResult bidResult = {robots[0], 0};
    for (auto& robot : robots) {
        LocalMissionManager localMissionManager(mission, robot, map);
        int bid = localMissionManager.getBid();
        if (bid > bidResult.highestBid) {
            bidResult.highestBid = bid;
            bidResult.selectedRobot = robot;
        }
    }
    return bidResult;
}