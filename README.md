<<<<<<< HEAD
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

=======
# emotional-support-robot

<details>
  <summary>About rviz</summary>
To install rviz: 
(https://docs.elephantrobotics.com/docs/gitbook-en/12-ApplicationBaseROS/12.1-ROS1/12.1.4-rivz%E4%BB%8B%E7%BB%8D%E5%8F%8A%E4%BD%BF%E7%94%A8/)

    sudo apt-get install ros-noetic-rviz

To launch and run:
Terminal 1: go into catkin_ws folder and type: `roscore`


To show the mycobot robotor in rviz
Terminal 1: `roscore`
Terminal 2: `roslaunch mycobot_280 test.launch`
(idk about the cobot version, though)

</details>

>>>>>>> main
## Basic Tutorials

<details>
  <summary>Creating a ROS Package</summary>

<<<<<<< HEAD
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
=======
### Creating the ROS Package
[Link](http://wiki.ros.org/ROS/Tutorials/CreatingPackage) to source

First change to the source space directory of the catkin workspace you created: 
    
    cd ~/catkin_ws/src

Now use the catkin_create_pkg script to create a new package called 'NAME' which (optionally) depends on X, Y, and Z: 

    catkin_create_pkg my_package X Y Z

Now you need to build the packages in the catkin workspace:
    
    cd ~/catkin_ws
    catkin_make

After the workspace has been built it has created a similar structure in the devel subfolder as you usually find under /opt/ros/$ROSDISTRO_NAME.To add the workspace to your ROS environment you need to source the generated setup file:

    catkin_ws/devel/setup.bash

### Customizing the package.xml
Change the **description tag** to anything you like, but by convention the first sentence should be short while covering the scope of the package. If it is hard to describe the package in a single sentence then it might need to be broken up.

    <description>My package</description>

The **maintainer tag** is a required and important tag for the package.xml because it lets others know who to contact about the package. At least one maintainer is required, but you can have many if you like. The name of the maintainer goes into the body of the tag, but there is also an email attribute that should be filled out:

    <!-- One maintainer tag required, multiple allowed, one person per tag --> 
    <!-- Example:  -->
    <!-- <maintainer email="jane.doe@example.com">Jane Doe</maintainer> -->
    <maintainer email="user@todo.todo">user</maintainer>

The *build_depend* **dependency tags** correspond to the dependencies (X, Y, Z) you provided when creating the package.
>>>>>>> main

### Customising the CMakeLists.txt
    TODO: later

<<<<<<< HEAD

=======
>>>>>>> main
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
<<<<<<< HEAD




    
</details>

=======
`rostopic pub` publishes data on to a topic currently advertised:

    rostopic pub [topic] [msg_type] [args]
  
`rostopic hz` reports the rate at which data is published:

    rostopic hz [topic]
    
</details>

<details>
  <summary>Understanding ROS Services and Parameters</summary>
  
**Services** are another way that nodes can communicate with each other. Services allow nodes to **send a request and receive a response**.
`rosservice` can easily attach to ROS's client/service framework with services. rosservice has many commands that can be used on services, as shown below:

    rosservice list         print information about active services
    rosservice call         call the service with the provided args
    rosservice type         print service type (if Empty, then no data is transmitted)
    rosservice find         find services by service type
    rosservice uri          print service ROSRPC uri

`rosparam` allows you to store and manipulate data on the ROS Parameter Server. The Parameter Server can store integers, floats, boolean, dictionaries, and lists. rosparam uses the YAML markup language for syntax. In simple cases, YAML looks very natural: 1 is an integer, 1.0 is a float, one is a string, true is a boolean, [1, 2, 3] is a list of integers, and {a: b, c: d} is a dictionary. rosparam has many commands that can be used on parameters, as shown below:

    rosparam set            set parameter
    rosparam get            get parameter
    rosparam load           load parameters from file
    rosparam dump           dump parameters to file
    rosparam delete         delete parameter
    rosparam list           list parameter names

</details>

<details>
  <summary>Debugging with rqt_console</summary>
  
Installing the required packages:
  
    sudo apt-get install ros-<distro>-rqt ros-<distro>-rqt-common-plugins
    
`rqt_console` attaches to ROS's logging framework to display output from nodes. `rqt_logger_level` allows us to change the verbosity level (DEBUG, WARN, INFO, and ERROR) of nodes as they run.

Now let's look at the output in rqt_console and switch logger levels in rqt_logger_level. Before we start the turtlesim (or whatever other), in two new terminals start rqt_console and rqt_logger_level:
    
    rosrun rqt_console rqt_console
    rosrun rqt_logger_level rqt_logger_level
    
</details>

<details>
  <summary>Writing a simple Publisher Subscriber</summary>

### Writing the Publisher Node

First, change directory into your package directory:

    roscd my_package
    
Create a scripts folder to store your Python scripts in:

    mkdir scripts
    cd skripts
    
Download an example script of a very basic publisher to you rnew scripts directory:
    
    wget https://raw.github.com/ros/ros_tutorials/kinetic-devel/rospy_tutorials/001_talker_listener/talker.py
    chmod +x talker.py
    
Detailed code explanation can be found [here](http://wiki.ros.org/ROS/Tutorials/WritingPublisherSubscriber%28python%29).
    
Add the following to your `CMakeLists.txt`:

    catkin_install_python(PROGRAMS scripts/talker.py
    DESTINATION ${CATKIN_PACKAGE_BIN_DESTINATION}
    )
    
### Writing the Subscriber Node

Download the example subscriber file:

    roscd my_package/scripts/
    wget https://raw.github.com/ros/ros_tutorials/kinetic-devel/rospy_tutorials/001_talker_listener/listener.py
    chmod +x listener.py

Detailed code explanation can be found [here](http://wiki.ros.org/ROS/Tutorials/WritingPublisherSubscriber%28python%29).
 
Then, edit the `catkin_install_python()` call in your `CMakeLists.txt` so it looks like the following:

    catkin_install_python(PROGRAMS scripts/talker.py scripts/listener.py
    DESTINATION ${CATKIN_PACKAGE_BIN_DESTINATION}
    )
    
### Building your Nodes

    cd ~/catkin_ws
    catkin_make
    
 ### Running the Publisher and the Subscriber
 
 First things first:
 
     roscore
     cd ~/catkin_ws
     source ./devel/setup.bash
     
Running the publisher:

    rosrun my_package talker.py
    
Running the subscriber:

    rosrun my_package listener.py
    
</details>

<details>
  <summary>Writing a simple Service and Client</summary>

### The Service Node

A service node performs some action and returns its outcome. Once you created your service file, you have to make it executable: `chmod +x scripts/my_service.py` and then addit to the CMakeLists.txt file:

    catkin_install_python(PROGRAMS scripts/my_service.py
    DESTINATION ${CATKIN_PACKAGE_BIN_DESTINATION}
    )
  
Within the `my_service.py` service file, we need to declare our service:

    rospy.init_node('my_service_server') # name of method called in if __name__ == "__main__":
    s = rospy.Service('my_service', myServiceType, handle_my_service)
    
where all requests to `my_service` with the `myServiceType` service type are passed to `handle_my_service` function. More detailed example code can be found [here](http://wiki.ros.org/ROS/Tutorials/WritingServiceClient%28python%29). 
  
### The Client Node
Once you created your client file, you have to make it executable: `chmod +x scripts/my_client.py` and then addit to the CMakeLists.txt file:

    catkin_install_python(PROGRAMS scripts/my_service.py scripts/my_client.py
    DESTINATION ${CATKIN_PACKAGE_BIN_DESTINATION}
    )

Instead of the `init_node()` call, we have the following for the client:

    rospy.wait_for_service('my_service')
    
The handling of the called service: 

    do_service = rospy.ServiceProxy('my_service', myServiceType)

### Building your nodes

Finally, you have to build your nodes, as always:

    # In your catkin workspace
    cd ~/catkin_ws
    catkin_make
    
### Running your service

    rosrun my_package my_service_server.py
    rosrun my_package my_client.py [optArgs]

</details>
>>>>>>> main
