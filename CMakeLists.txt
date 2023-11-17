cmake_minimum_required(VERSION 3.26)
project(adaptiveMRS)

set(CMAKE_CXX_STANDARD 17)
set(YAML_CPP_INSTALL_DIR ${CMAKE_BINARY_DIR}/yaml-cpp)

include(ExternalProject)


ExternalProject_Add(yaml-cpp
        GIT_REPOSITORY https://github.com/jbeder/yaml-cpp.git
        GIT_TAG 0.8.0
        CMAKE_ARGS -DCMAKE_INSTALL_PREFIX=${YAML_CPP_INSTALL_DIR}
)

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
        src/GlobalMissionManager.h
        src/LocalMissionManager.cpp
        src/LocalMissionManager.h
        src/Cell.h)

#find_package(yaml-cpp REQUIRED)

target_include_directories(adaptiveMRS PRIVATE ${YAML_CPP_INSTALL_DIR}/include)
target_link_libraries(adaptiveMRS PRIVATE ${YAML_CPP_INSTALL_DIR}/lib/libyaml-cpp.a)
