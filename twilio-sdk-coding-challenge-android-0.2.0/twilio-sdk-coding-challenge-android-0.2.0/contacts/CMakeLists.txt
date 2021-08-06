cmake_minimum_required(VERSION 3.4.1)

file(GLOB_RECURSE SOURCE_FILES
    "src/main/cpp/*.h"
    "src/main/cpp/*.cpp"
)

add_library(${CONTACTS_LIBRARY} SHARED ${SOURCE_FILES})