cmake_minimum_required(VERSION 3.26)
project(adaptiveMRS)

set(CMAKE_CXX_STANDARD 17)

add_executable(adaptiveMRS src/main.cpp
        src/Robot.cpp
        src/Robot.h
        src/point.h
        src/Map.cpp
        src/Map.h
        src/Mission.cpp
        src/Mission.h
        src/Task.cpp
        src/Task.h
        src/GlobalMissionManager.cpp
        src/GlobalMissionManager.h)

find_package(yaml-cpp REQUIRED)

target_link_libraries(adaptiveMRS yaml-cpp)
