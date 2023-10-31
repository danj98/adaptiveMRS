cmake_minimum_required(VERSION 3.26)
project(adaptiveMRS)

set(CMAKE_CXX_STANDARD 17)

add_executable(adaptiveMRS main.cpp)

find_package(yaml-cpp REQUIRED)

target_link_libraries(adaptiveMRS yaml-cpp)
