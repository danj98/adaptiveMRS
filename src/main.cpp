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
    std::vector<Point> obstacles;

    std::ifstream fin(filename);
    if (!fin) {
        std::cout << "File not found!" << std::endl;
        return 1;
    }
    YAML::Node config = YAML::Load(fin);

    YAML::Node worldInfo = config["world_information"];
    YAML::Node robotInfo = config["robots"];
    YAML::Node mission = config["mission"];

    int width = worldInfo["grid_size"]["width"].as<int>();
    int height = worldInfo["grid_size"]["height"].as<int>();
    YAML::Node obstacleNodes = worldInfo["obstacles"];

    // The map is a 2D array of size width x height
    int map[width][height];

    for (const auto& node : obstacleNodes) {
        int x = node[0].as<int>();
        int y = node[1].as<int>();
        obstacles.emplace_back(x, y);
    }

    // Fills map with 0s except for obstacles
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++){
            if (std::find(obstacles.begin(), obstacles.end(), Point(j, i)) != obstacles.end()) {
                map[j][i] = 1;
            } else {
                map[j][i] = 0;
            }
        }
    }

    Map map1 = Map(3, 3);
    map1.addObstacle(2, 2);

    Robot robot1 = Robot("robot1", 1, 100, Point(0, 0), true);

    printGrid(width, height, obstacles);


    assignTasks(mission, robotInfo);

    return 0;
}


