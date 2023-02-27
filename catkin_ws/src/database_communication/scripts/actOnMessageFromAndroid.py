#!/usr/bin/env python3
# Software License Agreement (BSD License)
#
# Copyright (c) 2008, Willow Garage, Inc.
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
#  * Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer.
#  * Redistributions in binary form must reproduce the above
#    copyright notice, this list of conditions and the following
#    disclaimer in the documentation and/or other materials provided
#    with the distribution.
#  * Neither the name of Willow Garage, Inc. nor the names of its
#    contributors may be used to endorse or promote products derived
#    from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
# "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
# LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
# FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
# COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
# INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
# BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
# LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
# ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
#
# Revision $Id$

## Simple talker demo that listens to std_msgs/Strings published 
## to the 'messageFromAndroid' topic

import rospy
from std_msgs.msg import String

# Import mycobot stuff
from pymycobot import MyCobot
from pymycobot.genre import Angle
import time

def callback(data):
    rospy.loginfo(rospy.get_caller_id() + 'I heard %s', data.data)

    # TODO: execute robot action depending on data.data
    
    # Initiate a MyCobot object
    # TODO: if there is an error, try ttyACM0
    mc = MyCobot('/dev/ttyACM1', 115200)

    #By passing the angle parameter, let each joint of the robotic arm move to the position corresponding to [0, 0, 0, 0, 0, 0]
    mc.send_angles([0, 0, 0, 0, 0, 0], 50)
    time.sleep(2.5)
    mc.send_angles([88.68, -138.51, 155.65, -128.05, -9.93, -15.29], 50)

    # Move joint 1 to the 90 position
    # mc.send_angle(Angle.J1.value, 90, 50)

    # TODO: once robot interaction has finished, update firestore firebase:

    # Step 1: setup 

    # from firebase_admin import credentials
    # from firebase_admin import firestore

    # cred = credentials.Certificate("catkin_ws/src/database_communication/scripts/firebase-credentials/emotional-support-robot-firebase-adminsdk-pvqeh-b7cd18640e.json")
    # app = firebase_admin.initialize_app(cred)
    # db = firestore.client()
   
    # Step 2: call update function
    #db.collection(u'android-robot-communication').document("MESSAGE").update({u'sender': "ROBOT"})

def actOnMessageFromAndroid():

    # In ROS, nodes are uniquely named. If two nodes with the same
    # name are launched, the previous one is kicked off. The
    # anonymous=True flag means that rospy will choose a unique
    # name for our 'listener' node so that multiple listeners can
    # run simultaneously.
    rospy.init_node('actOnToMessageFromAndroid', anonymous=True)

    rospy.Subscriber('messageFromAndroid', String, callback)

    # spin() simply keeps python from exiting until this node is stopped
    rospy.spin()

if __name__ == '__main__':
    actOnMessageFromAndroid()
