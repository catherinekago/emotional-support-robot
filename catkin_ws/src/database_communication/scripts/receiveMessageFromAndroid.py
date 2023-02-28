# info https://towardsdatascience.com/essentials-for-working-with-firestore-in-python-372f859851f7

import os
# !/usr/bin/env python3
import rospy
from std_msgs.msg import String
from pymycobot import MyCobot
from pymycobot.genre import Angle
from pymycobot.genre import Coord

# Step 1 Add Firebase Admin SDK to python app in terminal
# If you have pip in your PATH environment variable: pip install --upgrade firebase-admin 
# Otherwise: py -m pip install firebase-admin
# if this does not work, do: setx PATH "%PATH%;C:\Python34\Scripts"

# Step 2 connect firebase firestore instance
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from pymycobot import MyCobotSocket
import threading
import time

import os

# Use the private key file of the service account directly.
## TODO: copy PATH (ABSOLUTE, not relative of the firebase credential file to cred)
cred = credentials.Certificate(
    "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/src/database_communication/scripts/firebase-credentials/emotional-support-robot-firebase-adminsdk-pvqeh-82d0908a80.json")
app = firebase_admin.initialize_app(cred)
db = firestore.client()

# Initiate a MyCobot object
# TODO: if there is an error, try ttyACM0
mc = MyCobotSocket("192.168.1.106", 9000)

# Create Firebase Firestore listener

# Create an Event for notifying main thread.
callback_done = threading.Event()


# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
    # idle state == snake
    mc.set_color(240, 240, 240)
    mc.send_angles([88.68, -138.51, 155.65, -128.05, -9.93, -15.29], 50)
    time.sleep(4)
    # db.collection(u'android-robot-communication').document("MESSAGE").update({u'sender': "ROBOT"})
    # db.collection(u'android-robot-communication').document("MESSAGE").update({u'body': "SNAKE"})

    print(f'Received message from: {doc_snapshot[0].get("sender")}')
    sender = doc_snapshot[0].get("sender");
    bodyFromSnapshot = doc_snapshot[0].get("body");
    print("retrieved body ", bodyFromSnapshot)

    if (sender == "ANDROID"):
        # set global variable to trigger publishing received emotion
        global body
        body = bodyFromSnapshot
        if (body == "WAKEWORD"):
            print("Body is wakeword")
            wakeWordDetected()
        callback_done.set()


def wakeWordDetected():
    # activate robot --> wake word by app
    # THIS IS FOR TESTING ROBOT CODE (should be moved to actOnMessageFromAndroid eventually)
    print("Robot awakened")

    ##listening state
    # TODO why is color not changing? Why is head in wrong position?
    mc.send_angles([0, 0, 0, 0, 0, 0], 50)
    time.sleep(1.1)
    mc.set_color(0, 150, 255)
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'sender': "ROBOT"})
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'body': "AWAKE"})

    # listening routine
    # TODO: add more listening signs (tilting the head)
    time.sleep(1)
    mc.send_angle(Angle.J5.value, -20, 80)
    time.sleep(0.7)
    mc.send_angle(Angle.J5.value, 20, 80)
    time.sleep(0.7)
    mc.send_angle(Angle.J5.value, -10, 80)
    time.sleep(4)

    # TODO change emotion here based on detected emotion
    emotion = "HAPPY"

    ##active state
    mc.set_color(0, 255, 0)
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'sender': "ROBOT"})
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'body': "PLAYING"})

    # Reaction to emotion initiated here
    if (emotion == "HAPPY"):
        # TODO Happy Dances
        happyDance()
    elif (emotion == "ANXIOUS"):
        # TODO Breathing exercise
        print("Anxious dancing")

        mc.send_coord(Coord.X.value, -10, 70)
        time.sleep(2)
        mc.send_coord(Coord.Y.value, -10, 70)
        time.sleep(2)
        mc.send_coord(Coord.X.value, 10, 70)
        time.sleep(2)
        mc.send_coord(Coord.Y.value, 10, 70)
        time.sleep(2)

    else:
        # TODO Default case?
        print("Emotion: " + emotion)

    # TODO: user can interrupt routine

    ##idle state == snake
    mc.set_color(240, 240, 240)
    mc.send_angles([88.68, -138.51, 155.65, -128.05, -9.93, -15.29], 50)
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'sender': "ROBOT"})
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'body': "SNAKE"})

    time.sleep(4)


docs_ref = db.collection(u'android-robot-communication').document("MESSAGE")

# Watch the document
doc_watch = docs_ref.on_snapshot(on_snapshot)


def happyDance():
    bpm = 220
    while bpm > 0:
        if bpm % 2 == 0:
            mc.send_angles([180, 15, 10, 80, 23, 0], 90)
            time.sleep(0.7)
            mc.send_angles([-180, -5, -5, -60, -56, 0], 90)
            time.sleep(0.7)
        elif bpm % 3 == 0:
            mc.send_angles([30, 25, 15, 25, 50, 0], 90)
            time.sleep(0.7)
            mc.send_angles([-20, -20, -10, -25, -50, 0], 90)
            time.sleep(0.7)
        elif bpm % 5 == 0:
            mc.send_angles([20, -5, -5, -5, 10, 0], 90)
            time.sleep(0.7)
            mc.send_angles([-20, -20, -10, -15, -10, 0], 90)
            time.sleep(0.7)
        else:
            #good
            time.sleep(1)
            mc.send_angles([0, 0, 0, 20, 30, 0], 50)
            time.sleep(0.7)
            mc.send_angles([-10, 20, 0, -45, -30, 0], 50)
            time.sleep(0.7)
        bpm -= 1
    print("Happy dance")


# Publisher code
def receiveMessageFromAndroid():
    pub = rospy.Publisher('messageFromAndroid', String, queue_size=10)
    rospy.init_node('receiveMessageFromAndroid', anonymous=True)
    rate = rospy.Rate(3)  # 1hz

    global body
    body = ""

    while not rospy.is_shutdown():

        if body != "":
            rospy.loginfo("publishing body " + body)
            pub.publish(body)

            # reset message
            body = ""

        rate.sleep()


if __name__ == '__main__':
    try:
        receiveMessageFromAndroid()
    except rospy.ROSInterruptException:
        pass
