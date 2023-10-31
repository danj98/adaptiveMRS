#include <iostream>
#include "yaml-cpp/yaml.h"
#include <stdio.h>
#include <fstream>

// prints a grid based on width and height
void printGrid(int width, int height) {
    for (int i = 0; i < height; i++) {
        std::cout << "|";
        for (int j = 0; j < width; j++) {
            std::cout << " |";
        }
        std::cout << std::endl;
    }
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cout << "Usage: ./path_planning <input.yaml>" << std::endl;
        return 1;
    }

    std::string filename = argv[1];

    std::ifstream fin(filename);
    if (!fin) {
        std::cout << "File not found!" << std::endl;
        return 1;
    }
    YAML::Node mission = YAML::Load(fin);

    int width = mission["world_information"]["grid_size"]["width"].as<int>();
    int height = mission["world_information"]["grid_size"]["height"].as<int>();

    printGrid(width, height);

    return 0;
}


