//
// Created by djh on 11/7/23.
//

#ifndef ADAPTIVEMRS_MISSION_H
#define ADAPTIVEMRS_MISSION_H

#include <utility>
#include <vector>
#include "Task.h"
#include "point.h"
#include "Cell.h"

class Mission {
public:
    std::vector<Task> tasks;
    Cell goal;

    Mission() = default;
    Mission(std::vector<Task> tasks, Task goal) : tasks(tasks), goal(goal.getX(), goal.getY(), CellType::TASK) {}

    void addTask(Task task);
    void removeTask(Task task);
    void setGoal(Cell goal);
    void setTasks(std::vector<Task> tasks);

private:
    bool isValidTask(Task task);
    bool isValidGoal(Task goal);
};


#endif //ADAPTIVEMRS_MISSION_H
