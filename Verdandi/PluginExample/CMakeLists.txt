cmake_minimum_required(VERSION 3.16)

project(PluginExample LANGUAGES CXX)

set(CMAKE_AUTOUIC ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(QT NAMES Qt6 Qt5 REQUIRED COMPONENTS Core Gui Widgets)
find_package(Qt${QT_VERSION_MAJOR} REQUIRED COMPONENTS Core Gui Widgets)

add_library(PluginExample MODULE
  PluginExample_global.h
  pluginexample.cpp
  pluginexample.h
)

target_link_libraries(PluginExample PRIVATE
  Qt${QT_VERSION_MAJOR}::Core
  Qt${QT_VERSION_MAJOR}::Gui
  Qt${QT_VERSION_MAJOR}::Widgets
)

target_compile_definitions(PluginExample PRIVATE PLUGINEXAMPLE_LIBRARY)

set_target_properties(PluginExample PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/..
    OUTPUT_NAME "pluginproject"
)

if(WIN32)
    set_target_properties(PluginExample PROPERTIES
        SUFFIX ".dll"
        PREFIX ""
    )
endif()

if(APPLE)
    set_target_properties(PluginExample PROPERTIES
        SUFFIX ".dylib"
        BUNDLE FALSE
    )
endif()
