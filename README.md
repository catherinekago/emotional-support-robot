
To install rviz: 
(https://docs.elephantrobotics.com/docs/gitbook-en/12-ApplicationBaseROS/12.1-ROS1/12.1.4-rivz%E4%BB%8B%E7%BB%8D%E5%8F%8A%E4%BD%BF%E7%94%A8/)

sudo apt-get install ros-noetic-rviz

To launch and run:
Terminal 1: go into catkin_ws folder and type:
roscore


To show the mycobot robotor in rviz
Terminal 1: roscore
Terminal 2: roslaunch mycobot_280 test.launch

(idk about the cobot version, though)

# emotional-support-robot

## Starting the Robot

## Usign the app


### Starting the app
You can either open the emotional-support-robot-app in Android studio, connect your phone via USB to the computer and run the code on your computer (which opens the app on your phone), or install the APK directly on your phone. You can find the APK in \emotional-support-robot-app\app\build\intermediates\apk\debug. 

### Requirements
* Use smartphone in light mode (night mode not implemented, interface might not be readable)
* Connect your phone to WIFI
* Always allow microphone during usage within app info settings. 

### How to control the robot
* Say HEY ESRA to wake it up
* Say ANXIOUS / NERVOUS or HAPPY / GOOD / GREAT / AWESOME to select an emotion 
* For anxious mode, say SHORT / MEDIUM / LONG to select a cycle length and start box breathing
* After box breathing has finished, say YES to do another cycle, say NO if you don't want to continue
* For happy, say WALKING ON SUNSHINE / SEXY AND I KNOW IT to select a song for the happy dance
* To stop interaction, say "STOP" - known bug, does not work reliably. Alternatively, tap anywhere on the screen to stop robot interaction. 
