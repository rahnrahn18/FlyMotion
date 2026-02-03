cmake_minimum_required(VERSION 3.22.1)

project("flymotion")

# Configure OpenCV
set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/opencv/libs/${ANDROID_ABI}")
find_package(OpenCV REQUIRED)
message(STATUS "OpenCV library status: ${OpenCV_LIBS}")

# Define source files
# Core
file(GLOB CORE_SOURCES "core/*.cpp")
# Motion
file(GLOB MOTION_SOURCES "motion/*.cpp")
# Export
file(GLOB EXPORT_SOURCES "export/*.cpp")
# JNI
file(GLOB JNI_SOURCES "jni/*.cpp")
# Render
file(GLOB RENDER_SOURCES "render/*.cpp")
# Transform
file(GLOB TRANSFORM_SOURCES "transform/*.cpp")

# Main library
add_library(flymotion SHARED
        native-lib.cpp
        ${CORE_SOURCES}
        ${MOTION_SOURCES}
        ${EXPORT_SOURCES}
        ${JNI_SOURCES}
        ${RENDER_SOURCES}
        ${TRANSFORM_SOURCES}
)

# Include directories
target_include_directories(flymotion PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/opencv/include
)

# Link libraries
target_link_libraries(flymotion
        ${OpenCV_LIBS}
        android
        log
        GLESv2
        EGL
        jnigraphics
)
