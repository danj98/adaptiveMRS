//
// Created by djh on 11/10/23.
//

#ifndef ADAPTIVEMRS_LOCALMISSIONMANAGER_H
#define ADAPTIVEMRS_LOCALMISSIONMANAGER_H


#include "point.h"
#include "Mission.h"
#include "Robot.h"
#include "Map.h"

struct Node {
    int x;
    int y;
    int g; // Cost from start to current node
    int h; // Estimated cost from current node to goal

    Node(int x, int y, int g, int h) : x(x), y(y), g(g), h(h) {}

    // Total cost
    int f() const {
        return g + h;
    }

    bool operator>(const Node& other) const {
        return f() > other.f();
    }
};

class LocalMissionManager {
public:
    // Constructors
    LocalMissionManager() = default;
    LocalMissionManager(Mission mission, Robot robot, Map map) : currentMission(mission), robot(robot), map(map), bid(0) {}

    void run();
    std::vector<Cell> findRoute(const std::vector<std::vector<CellType>>& grid, const Cell& start, const Cell& goal);
    int getBid();
    int calculateHeuristic(const Cell& a, const Cell& b);

private:
    Mission currentMission;
    Robot robot;
    Map map;
    int bid;
};


#endif //ADAPTIVEMRS_LOCALMISSIONMANAGER_H
