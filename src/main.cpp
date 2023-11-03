#include <iostream>
#include "yaml-cpp/yaml.h"
#include <stdio.h>
#include <fstream>
#include "point.h"
#include "robot.h"
#include "Map.h"


// prints the map to console
void printMap(Map map, std::vector<Robot> robots) {}


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

    std::vector<Point> obstacles;
    for (const auto& node : obstacleNodes) {
        int x = node[0].as<int>();
        int y = node[1].as<int>();
        obstacles.emplace_back(x, y);
    }

    map.setObstacles(obstacles);

    // Robot creation
    Robot robot1 = Robot(robotInfo[0]["name"].as<std::string>(),
                         robotInfo[0]["id"].as<int>(),
                         robotInfo[0]["battery"].as<int>(),
                        Point(robotInfo[0]["start_position"][0].as<int>(), robotInfo[0]["start_position"][1].as<int>()),
                         robotInfo[0]["active"].as<bool>(),
                         robotInfo[0]["capabilities"].as<int>());

    Robot robot2 = Robot(robotInfo[1]["name"].as<std::string>(),
                         robotInfo[1]["id"].as<int>(),
                         robotInfo[1]["battery"].as<int>(),
                         Point(robotInfo[1]["start_position"][0].as<int>(), robotInfo[1]["start_position"][1].as<int>()),
                         robotInfo[1]["active"].as<bool>(),
                         robotInfo[1]["capabilities"].as<int>());

    std::cout << "Robot 1: " << robot1.getPosition().x << ", " << robot1.getPosition().y << std::endl;
    std::cout << "Robot 2: " << robot2.getPosition().x << ", " << robot2.getPosition().y << std::endl;

    // Draw map
    printGrid()

    //printGrid(width, height, obstacles);


    assignTasks(mission, robotInfo);

    return 0;
}


