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

## Basic Tutorials

<details>
  <summary>Creating a ROS Package</summary>

#### Creating the ROS Package
    [Link](http://wiki.ros.org/ROS/Tutorials/CreatingPackage) to source

    First change to the source space directory of the catkin workspace you created:

        cd ~/catkin_ws/src

    Now use the catkin_create_pkg script to create a new package called 'NAME' which (optionally) depends on X, Y, and Z:

        catkin_create_pkg my_package X Y Z

    This creates a package with the basic structure

        my_package/
            CMakeLists.txt
            package.xml

    Now you need to build the packages in the catkin workspace:

        cd ~/catkin_ws
        catkin_make

    After the workspace has been built it has created a similar structure in the devel subfolder as you usually find under /opt/ros/$ROSDISTRO_NAME.To add the workspace to your ROS environment you need to source the generated setup file:

        catkin_ws/devel/setup.bash

### Customizing the package.xml
    Change the **description tag** to anything you like, but by convention the first sentence should be short while covering the scope of the package. If it is hard to describe the package in a single sentence then it might need to be broken up.

        <description>The beginner_tutorials package</description>

    The **maintainer tag** is a required and important tag for the package.xml because it lets others know who to contact about the package. At least one maintainer is required, but you can have many if you like. The name of the maintainer goes into the body of the tag, but there is also an email attribute that should be filled out:


        <!-- One maintainer tag required, multiple allowed, one person per tag --> 
        <!-- Example:  -->
        <!-- <maintainer email="jane.doe@example.com">Jane Doe</maintainer> -->
        <maintainer email="user@todo.todo">user</maintainer>

    The *build_depend* **dependency tags** correspond to the dependencies (X, Y, Z) you provided when creating the package.

### Customising the CMakeLists.txt
    TODO: later


</details>

<details>
  <summary>Understanding Nodes</summary> 

<ul>
  <li>Nodes: A node is an executable that uses ROS to communicate with other nodes.</li>
  <li>Messages: ROS data type used when subscribing or publishing to a topic.</li>
  <li>Topics: Nodes can publish messages to a topic as well as subscribe to a topic to receive messages.</li>
  <li>Master: Name service for ROS (i.e. helps nodes find each other)</li>
</ul>

`roscore` is the first thing you should run when using ROS.

Open up a new terminal, and let's use `rosnode list` to display what ROS nodes are currently running. `rosrun [package_name] [node_name]` allows you to use the package name to directly run a node within a package without having to know the package path (you need a new terminal for this one, too).

</details>

<details>
  <summary>Understanding ROS Topics</summary> 

### ROS Topics

Imagine we are running two nodes in two separate terminals:

    rosrun my_package X
    rosrun my_package Y

Node X and node Y are communicating with each other over a ROS Topic. One of them could be publishing information on a topic, while the other one may subscribe to the same topic to receive the information. You can use use `rosrun rqt_graph rqt_graph` in a new terminal which shows the nodes and topics currently running.

The `rostopic` tool allows you to get information about ROS topics. 

<ul>
    <li>Use rostopic -h to get all available sub-commands. </li>
    <li>rostopic echo /path/to/topic shows the data published on a specific topic. </li>
    <li>rostopic list -v returns all topics currently subscribed and published </li>
</ul>

### ROS Messages

Communication on topics happens by sending ROS messages between nodes. For the publisher and subscriber to communicate, the publisher and subscriber must send and receive the same type of message. This means that a **topic type** is defined by the **message type** published on it. The type of the message sent on a topic can be determined using `rostopic type [path/top/topic]`.

To get the details of the message, use `rosmsg show [message]`.

### Using rostopic with messages




    
</details>

