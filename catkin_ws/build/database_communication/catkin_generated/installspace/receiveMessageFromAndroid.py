# info https://towardsdatascience.com/essentials-for-working-with-firestore-in-python-372f859851f7


#!/usr/bin/env python3
import rospy
from std_msgs.msg import String
from pymycobot import MyCobot

# Step 1 Add Firebase Admin SDK to python app in terminal
# If you have pip in your PATH environment variable: pip install --upgrade firebase-admin 
# Otherwise: py -m pip install firebase-admin
# if this does not work, do: setx PATH "%PATH%;C:\Python34\Scripts"

# Step 2 connect firebase firestore instance
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

import threading
import time

import os


# Use the private key file of the service account directly.
## TODO: copy PATH (ABSOLUTE, not relative of the firebase credential file to cred)
cred = credentials.Certificate("/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/src/database_communication/scripts/firebase-credentials/emotional-support-robot-firebase-adminsdk-pvqeh-82d0908a80.json")
app = firebase_admin.initialize_app(cred)
db = firestore.client()

# Create Firebase Firestore listener

# Create an Event for notifying main thread.
callback_done = threading.Event()

# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):


    # THIS IS FOR TESTING ROBOT CODE (should be moved to actOnMessageFromAndroid eventually)

    # Initiate a MyCobot object
    # TODO: if there is an error, try ttyACM0
    mc = MyCobot('/dev/ttyACM1', 115200)

    #By passing the angle parameter, let each joint of the robotic arm move to the position corresponding to [0, 0, 0, 0, 0, 0]
    mc.send_angles([0, 0, 0, 0, 0, 0], 50)
    time.sleep(4)
    mc.send_angles([88.68, -138.51, 155.65, -128.05, -9.93, -15.29], 50)
    time.sleep(4)

    # ENDO OF TESTING ROBOT CODE 



    print(f'Received message from: {doc_snapshot[0].get("sender")}')
    sender = doc_snapshot[0].get("sender");
    bodyFromSnapshot = doc_snapshot[0].get("body");
    print("retrieved body ", bodyFromSnapshot)

    if (sender == "ANDROID"):
        # set global variable to trigger publishing received emotion
        global body
        body = bodyFromSnapshot

        callback_done.set()

docs_ref = db.collection(u'android-robot-communication').document("MESSAGE");

# Watch the document
doc_watch = docs_ref.on_snapshot(on_snapshot)


# Publisher code
def receiveMessageFromAndroid():
    pub = rospy.Publisher('messageFromAndroid', String, queue_size=10)
    rospy.init_node('receiveMessageFromAndroid', anonymous=True)
    rate = rospy.Rate(3) # 1hz

    global body
    body = ""

    while not rospy.is_shutdown():
        
        if body != "":
            rospy.loginfo("publishing emotion " + body)
            pub.publish(body)
            
            # reset message
            body = ""

        rate.sleep()

if __name__ == '__main__':
    try:
        receiveMessageFromAndroid()
    except rospy.ROSInterruptException:
        pass