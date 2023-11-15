//
// Created by djh on 11/15/23.
//

#ifndef ADAPTIVEMRS_CELL_H
#define ADAPTIVEMRS_CELL_H

enum class CellType {
    EMPTY,
    OBSTACLE,
    ROBOT,
    TASK,
    ROUTE
};

struct Cell {
    int x;
    int y;
    CellType type;

    Cell(int x, int y, CellType type) : x(x), y(y), type(type) {}
};

#endif //ADAPTIVEMRS_CELL_H
