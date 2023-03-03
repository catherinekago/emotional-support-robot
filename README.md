
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
To connect the robot to WIFI go to the catkin_ws/scripts folder and run:
python3 cobot_enable_wifi.py

Hints:
You have to be connected to the robot via USB and if you get a "permission denied" error, run:
sudo chmod 666 /dev/ttyACM0

When you succeed, set the robot to WIFI-mode (via its display), connect to the "PEM-HRI" WIFI, in receiveMessageFromAndroid.py in line 34 replace the IP-address with your robot's address and run:
rosrun database_communication receiveMessageFromAndroid.py



## Using the app


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
* To stop the dance, say "STOP" (does not work when phone speakers are playing the music). Alternatively, tap anywhere on the screen to stop robot interaction. Known bug: When waking the robot again, it will behave weirdly. To solve that restart the robot with:
rosrun database_communication receiveMessageFromAndroid.py
