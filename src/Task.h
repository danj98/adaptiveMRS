//
// Created by djh on 11/7/23.
//

#ifndef ADAPTIVEMRS_TASK_H
#define ADAPTIVEMRS_TASK_H


#include <string>
#include <utility>

class Task {
public:
    Task() = default;
    Task(int x, int y, int cost, std::string name, int id) : x(x), y(y), cost(cost), name(std::move(name)) {}

    int getX() const;
    int getY() const;
    int getCost() const;
    const std::string &getName() const;

    void setX(int x);
    void setY(int y);
    void setCost(int cost);
    void setName(const std::string &name);

private:
    int id;
    int x;
    int y;
    int cost;
    std::string name;
};


#endif //ADAPTIVEMRS_TASK_H
