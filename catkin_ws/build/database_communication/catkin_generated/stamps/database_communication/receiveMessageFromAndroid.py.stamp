# info https://towardsdatascience.com/essentials-for-working-with-firestore-in-python-372f859851f7


#!/usr/bin/env python3
import rospy
from std_msgs.msg import String

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
cred = credentials.Certificate("catkin_ws/src/database_communication/scripts/firebase-credentials/emotional-support-robot-firebase-adminsdk-pvqeh-b7cd18640e.json")
app = firebase_admin.initialize_app(cred)
db = firestore.client()

# Create Firebase Firestore listener

# Create an Event for notifying main thread.
callback_done = threading.Event()

# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
    print(f'Received message from: {doc_snapshot[0].get("sender")}')
    sender = doc_snapshot[0].get("sender");
    emotionFromSnapshot = doc_snapshot[0].get("emotion");
    print("retrieved emotion ", emotionFromSnapshot)

    if (sender == "ANDROID"):
        # set global variable to trigger publishing received emotion
        global emotion
        emotion = emotionFromSnapshot

        callback_done.set()

docs_ref = db.collection(u'android-robot-communication').document("MESSAGE");

# Watch the document
doc_watch = docs_ref.on_snapshot(on_snapshot)


# Publisher code
def receiveMessageFromAndroid():
    pub = rospy.Publisher('messageFromAndroid', String, queue_size=10)
    rospy.init_node('receiveMessageFromAndroid', anonymous=True)
    rate = rospy.Rate(3) # 1hz

    global emotion
    emotion = ""

    while not rospy.is_shutdown():
        
        if emotion != "":
            rospy.loginfo("publishing emotion " + emotion)
            
            # reset message
            emotion = ""
            pub.publish(emotion)

        rate.sleep()

if __name__ == '__main__':
    try:
        receiveMessageFromAndroid()
    except rospy.ROSInterruptException:
        pass