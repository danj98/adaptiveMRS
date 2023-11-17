#include <iostream>
#include "yaml-cpp/yaml.h"
#include <stdio.h>
#include <fstream>
#include "point.h"
#include "Robot.h"
#include "Map.h"
#include "GlobalMissionManager.h"


void assignTasks(YAML::Node mission, YAML::Node robotInfo) {
   std::cout << "Assigning tasks..." << std::endl;
}


int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cout << "Usage: ./path_planning <input.yaml>" << std::endl;
        return 1;
    }

    std::string filename = argv[1];

    // Loads the YAML file
    YAML::Node config = YAML::LoadFile(filename);

    YAML::Node worldInfo = config["world_information"];
    YAML::Node robotInfo = config["robots"];
    YAML::Node mission = config["mission"];

    // Map creation
    int width = worldInfo["grid_size"]["width"].as<int>();
    int height = worldInfo["grid_size"]["height"].as<int>();
    YAML::Node obstacleNodes = worldInfo["obstacles"];

    Map map = Map(width, height);

    std::vector<Cell> obstacles;
    for (const auto& node : obstacleNodes) {
        int x = node[0].as<int>();
        int y = node[1].as<int>();
        obstacles.emplace_back(x, y, CellType::OBSTACLE);
    }

    map.setObstacles(obstacles);

    // Robot creation
    Robot robot1 = Robot(robotInfo[0]["name"].as<std::string>(),
                         robotInfo[0]["id"].as<int>(),
                         robotInfo[0]["battery"].as<int>(),
                        Cell(robotInfo[0]["start_position"][0].as<int>(), robotInfo[0]["start_position"][1].as<int>(), CellType::ROBOT),
                         robotInfo[0]["active"].as<bool>(),
                         robotInfo[0]["capabilities"].as<int>());

    Robot robot2 = Robot(robotInfo[1]["name"].as<std::string>(),
                         robotInfo[1]["id"].as<int>(),
                         robotInfo[1]["battery"].as<int>(),
                         Cell(robotInfo[1]["start_position"][0].as<int>(), robotInfo[1]["start_position"][1].as<int>(), CellType::ROBOT),
                         robotInfo[1]["active"].as<bool>(),
                         robotInfo[1]["capabilities"].as<int>());

    std::cout << "Robot 1: " << robot1.getPosition().x << ", " << robot1.getPosition().y << std::endl;
    std::cout << "Robot 2: " << robot2.getPosition().x << ", " << robot2.getPosition().y << std::endl;

    map.setRobotPosition(robot1.getPosition().x, robot1.getPosition().y);
    map.setRobotPosition(robot2.getPosition().x, robot2.getPosition().y);

    // Task creation
    Cell goal = Cell(mission["goal_position"][0].as<int>(), mission["goal_position"][1].as<int>(), CellType::TASK);
    map.setTaskPosition(goal.x, goal.y);

    Mission m = Mission(std::vector<Task>{}, Task(goal.x, goal.y, 0, "goal", 1));

    // Draw map
    map.displayMap();

    std::vector<Robot> robots;
    robots.push_back(robot1);
    robots.push_back(robot2);
    GlobalMissionManager globalMissionManager = GlobalMissionManager(robots, m, map);

    BidResult bid = globalMissionManager.getHighestBid(robots, m, map);
    std::cout << "\n";
    map.displayMapWithRoute(bid.selectedRobot.getRoute());

    // Print selected route
    std::cout << "Route: ";
    for (const auto& cell : bid.selectedRobot.getRoute()) {
        std::cout << "[" << cell.x << ", " << cell.y << "] ";
    }


    return 0;
}


