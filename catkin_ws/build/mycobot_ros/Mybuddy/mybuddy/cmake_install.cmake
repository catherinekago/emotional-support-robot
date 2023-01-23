# Install script for directory: /home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/src/mycobot_ros/Mybuddy/mybuddy

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/install")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "1")
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "FALSE")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/pkgconfig" TYPE FILE FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/mybuddy.pc")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/share/mybuddy/cmake" TYPE FILE FILES
    "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/mybuddyConfig.cmake"
    "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/mybuddyConfig-version.cmake"
    )
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/share/mybuddy" TYPE FILE FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/src/mycobot_ros/Mybuddy/mybuddy/package.xml")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/mybuddy" TYPE PROGRAM FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/follow_display.py")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/mybuddy" TYPE PROGRAM FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/slider_control.py")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/mybuddy" TYPE PROGRAM FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/teleop_keyboard.py")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/mybuddy" TYPE PROGRAM FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/listen_real.py")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/mybuddy" TYPE PROGRAM FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/listen_real_of_topic.py")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/mybuddy" TYPE PROGRAM FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/build/mycobot_ros/Mybuddy/mybuddy/catkin_generated/installspace/simple_gui.py")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/share/mybuddy" TYPE DIRECTORY FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/src/mycobot_ros/Mybuddy/mybuddy/launch" REGEX "/setup\\_assistant\\.launch$" EXCLUDE)
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/share/mybuddy" TYPE DIRECTORY FILES "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/src/mycobot_ros/Mybuddy/mybuddy/config")
endif()

