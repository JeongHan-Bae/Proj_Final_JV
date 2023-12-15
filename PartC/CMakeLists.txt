cmake_minimum_required(VERSION 3.24)
project(PartC)

set(CMAKE_CXX_STANDARD 17)

include_directories("../Sources/rapidjson-1.1.0/include")

add_executable(PartC server.cpp)

target_link_libraries(PartC PRIVATE ws2_32)