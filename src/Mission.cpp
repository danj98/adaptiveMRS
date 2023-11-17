//
// Created by djh on 11/7/23.
//

#include <algorithm>
#include "Mission.h"
#include "Cell.h"

void Mission::addTask(Task task) {
    if (isValidTask(task)) {
        tasks.push_back(task);
    }
}

void Mission::removeTask(Task task) {
    //TODO: implement removeTask
}

void Mission::setGoal(Cell goal) {
    Mission::goal = goal;
    goal.type = CellType::TASK;
}

void Mission::setTasks(std::vector<Task> tasks) {
    Mission::tasks = tasks;
}

bool Mission::isValidTask(Task task) {
    return true; // TODO: create valid task criteria
}

bool Mission::isValidGoal(Task goal) {
    return true; // TODO: create valid goal criteria
}
