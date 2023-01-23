# info https://towardsdatascience.com/essentials-for-working-with-firestore-in-python-372f859851f7


#!/usr/bin/env python
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

# Use the private key file of the service account directly.
cred = credentials.Certificate("firebase-credentials/emotional-support-robot-firebase-adminsdk-pvqeh-b7cd18640e.json")
app = firebase_admin.initialize_app(cred)
db = firestore.client()

# Create Firebase Firestore listener

# Create an Event for notifying main thread.
callback_done = threading.Event()

# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
    print(f'Received message from: {doc_snapshot[0].get("sender")}')
    sender = doc_snapshot[0].get("sender");
    emotion = doc_snapshot[0].get("emotion");
    print("retrieved emotion ", emotion)

    # TODO: execute robot code depending on emotion
    if (sender == "ANDROID"):
        time.sleep(5)
        print("sleep timer as placeholder for robot interactions:  5 seconds")

        # and once robot execution is finished, update message:
        db.collection(u'android-robot-communication').document("MESSAGE").update({u'sender': "ROBOT"})
        callback_done.set()

docs_ref = db.collection(u'android-robot-communication').document("MESSAGE");

# Watch the document
doc_watch = docs_ref.on_snapshot(on_snapshot)


# Publisher code
def talker():
    pub = rospy.Publisher('chatter', String, queue_size=10)
    rospy.init_node('talker', anonymous=True)
    rate = rospy.Rate(10) # 10hz
    while not rospy.is_shutdown():
        hello_str = "hello world %s" % rospy.get_time()
        rospy.loginfo(hello_str)
        pub.publish(hello_str)
        rate.sleep()

if __name__ == '__main__':
    try:
        talker()
    except rospy.ROSInterruptException:
        pass