//
// Created by djh on 11/7/23.
//

#ifndef ADAPTIVEMRS_GLOBALMISSIONMANAGER_H
#define ADAPTIVEMRS_GLOBALMISSIONMANAGER_H


#include <vector>
#include "Mission.h"

class GlobalMissionManager {
public:
    GlobalMissionManager() = default;

    void setMission(Mission mission);

    // Checks validity of mission
    bool isValidMission(Mission mission);


    


private:
    Mission currentMission;

};


#endif //ADAPTIVEMRS_GLOBALMISSIONMANAGER_H
