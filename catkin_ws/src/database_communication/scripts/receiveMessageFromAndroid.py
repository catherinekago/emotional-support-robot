# info https://towardsdatascience.com/essentials-for-working-with-firestore-in-python-372f859851f7


# !/usr/bin/env python3
import rospy
from std_msgs.msg import String
from pymycobot import MyCobot
from pymycobot.genre import Angle
from pymycobot.genre import Coord
from pymycobot import MyCobotSocket

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
cred = credentials.Certificate(
    "/home/ubunutu/Desktop/emotional-support-robot/emotional-support-robot/catkin_ws/src/database_communication/scripts/firebase-credentials/emotional-support-robot-firebase-adminsdk-pvqeh-82d0908a80.json")
app = firebase_admin.initialize_app(cred)
db = firestore.client()
# Initiate a MyCobot object

# ----Local connection (if there is an error, try ttyACM1):
# mc = MyCobot('/dev/ttyACM0', 115200)

# ----WIFI:
mc = MyCobotSocket("192.168.1.106", 9000)

# Create Firebase Firestore listener
# Create an Event for notifying main thread.
callback_done = threading.Event()
stop = False

bodyValue = "SNAKE"
breakLoop = False


# Create a callback on_snapshot function to capture changes
def on_snapshot(doc_snapshot, changes, read_time):
    print("Initialize Robot")

    print(doc_snapshot[0])
    bodyFromSnapshot = doc_snapshot[0].get("body")
    print("retrieved body ", bodyFromSnapshot)

    # set global variable to trigger publishing received emotion
    bodyValue = bodyFromSnapshot

    if (bodyValue == "SNAKE"):
        # idle state == snake
        print("snakey")
        breakLoop = False
        mc.stop()
        pulsingLight()
        goToSnakeMode()
    elif bodyValue == "WAKEWORD":
        print("Body is wakeword")
        global stop
        stop = False
        wakeWordDetected()
    elif bodyValue == "HAPPY_244" or bodyValue == "HAPPY_208" or bodyValue == "ANXIOUS":
        stop = False
        emotionDetected(bodyValue)
    elif bodyValue == "STOP":
        stop = True
        breakLoop = True
        stopRobot()

    callback_done.set()


def pulsingLight():
    # TODO
    # COLOR = off
    mc.set_color(1, 1, 1)
    # while (body=="SNAKE"):
    #   mc.set_color(240,240,240)
    #  time.sleep(0.3)
    # mc.set_color()
    # time.sleep(0.3)


def wakeWordDetected():
    # activate robot --> wake word by app
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'body': "AWAKE"})

    # THIS IS FOR TESTING ROBOT CODE (should be moved to actOnMessageFromAndroid eventually)
    print("Robot awakened")

    ##listening state
    mc.send_angles([0, 0, 0, 0, 0, 0], 50)
    time.sleep(1.1)

    # COLOR = white
    mc.set_color(240, 240, 240)

    # listening routine
    # TODO: add more listening signs (tilting the head) -- should be loop
    mc.send_angles([0, 0, 0, -10, -20, 0], 50)
    # mc.send_angle(Angle.J5.value, -20, 80)
    # mc.send_angle(Angle.J4.value, -10, 80)
    time.sleep(0.5)
    # mc.send_angle(Angle.J5.value, 20, 80)
    # mc.send_angle(Angle.J4.value, 10, 80)
    mc.send_angles([0, 0, 0, 10, 20, 0], 50)
    time.sleep(0.5)
    # mc.send_angle(Angle.J5.value, -10, 80)
    # mc.send_angle(Angle.J4.value, 0, 80)
    mc.send_angles([0, 0, 0, 0, 0, 0], 50)
    time.sleep(1)


def emotionDetected(emotion):
    print("Received an emotion " + emotion)

    ##active state
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'body': "PLAYING"})

    # Reaction to emotion initiated here
    # HAPPY_BPM
    if emotion == "HAPPY_244" or emotion == "HAPPY_208":
        happyDance(emotion)
    elif emotion == "ANXIOUS":
        startBreathingExercise()
    else:
        # TODO Default case?
        print("Emotion: " + emotion)

    # TODO: user can interrupt routine
    # call stopRobot() or pauseRobot() and break (if you're in a loop)


docs_ref = db.collection(u'android-robot-communication').document("MESSAGE")

# Watch the document
doc_watch = docs_ref.on_snapshot(on_snapshot)


### EMOTION FUNCTIONS ###

def startBreathingExercise():
    # COLOR = blue
    mc.set_color(0, 150, 255)
    speed_to_start = 50

    for i in range(9):

        docs_ref.on_snapshot(on_snapshot)
        if stop:
            break

        print("down left")
        mc.send_angles([-75, 45, 90, -130, 75, 0], speed_to_start)
        time.sleep(4)

        docs_ref.on_snapshot(on_snapshot)
        if stop:
            break

        print("up left")
        mc.send_angles([-90, 30, 15, -45, 90, 0], 10)
        time.sleep(4)

        docs_ref.on_snapshot(on_snapshot)
        if stop:
            break

        print("up right")
        mc.send_angles([45, 30, 15, -45, -45, 0], 17)
        time.sleep(4)

        docs_ref.on_snapshot(on_snapshot)
        if stop:
            break

        print("down right")
        mc.send_angles([30, 45, 90, -130, -30, 0], 10)
        time.sleep(4)

        speed_to_start = 17

    goToSnakeMode(40)
    updateBodyToSnake()


def happyDance(emotion):
    if emotion == "HAPPY_208":
        # COLOR = pink
        mc.set_color(255, 20, 147)
        start = time.time()
        minute = 60
        song_duration = 192
        while time.time() - start < song_duration:
            if stop:
                break
            docs_ref.on_snapshot(on_snapshot)
            start_2 = time.time()
            while time.time() - start_2 < minute:
                if stop:
                    break
                docs_ref.on_snapshot(on_snapshot)
                x = 3
                while x > 0:
                    if stop:
                        break
                    docs_ref.on_snapshot(on_snapshot)
                    if time.time() - start < song_duration:
                        mc.send_angles([-1.49, 115, -153.45, 30, -33.42, 137.9], 100)
                        time.sleep(0.15)
                        mc.send_angles([-1.49, 55, -153.45, 80, 33.42, 137.9], 100)
                        time.sleep(0.15)
                    else:
                        break
                    x -= 1
                if stop:
                    break
                x = 3
                while x > 0:
                    if stop:
                        break
                    docs_ref.on_snapshot(on_snapshot)
                    if time.time() - start < song_duration:
                        mc.send_angles([50, 15, 5, 20, 40, 0], 100)
                        time.sleep(0.15)
                        mc.send_angles([-50, -15, -5, -30, -56, 0], 100)
                        time.sleep(0.15)
                    else:
                        break
                    x -= 1
                if stop:
                    break
                x = 3
                while x > 0:
                    if stop:
                        break
                    docs_ref.on_snapshot(on_snapshot)
                    if time.time() - start < song_duration:
                        mc.send_angles([70, 25, 15, 15, 50, 0], 100)
                        time.sleep(0.15)
                        mc.send_angles([-70, -25, -15, -30, -56, 0], 100)
                        time.sleep(0.15)
                    else:
                        break
                    x -= 1
                if stop:
                    break
                x = 3
                # good
                time.sleep(0.35)
                while x > 0:
                    if stop:
                        break
                    docs_ref.on_snapshot(on_snapshot)
                    if time.time() - start < song_duration:
                        mc.send_angles([0, 0, 0, 20, 30, 0], 100)
                        time.sleep(0.15)
                        mc.send_angles([-10, 20, 0, -45, -30, 0], 100)
                        time.sleep(0.15)
                    else:
                        break
                    x -= 1
                if stop:
                    break
                if time.time() - start < song_duration:
                    mc.send_angles([-90.0, 3.16, 0.08, -3.07, 90.52, -43.94], 100)
                    time.sleep(0.15)
                    mc.send_angles([22.58, 13.62, 22.5, 23.2, -88.24, -38.75], 100)
                    time.sleep(0.15)
                    mc.send_angles([-99.84, 3.86, -19.51, -36.29, -162.24, -25.4], 100)
                    time.sleep(0.15)
                    mc.send_angles([-99.84, -21.18, -23.73, -45.7, 87.53, -25.57], 100)
                    time.sleep(0.15)
                    mc.send_angles([87.53, -5.18, 7.11, -2.63, 87.18, -25.66], 100)
                    time.sleep(0.15)
                    mc.send_angles([88.15, -30.05, 47.19, -2.37, 87.09, -25.66], 100)
                    time.sleep(0.15)
                    mc.send_angles([88.15, 30.84, -50.44, -2.37, 87.01, -25.66], 100)
                    time.sleep(0.15)
                    mc.send_angles([88.06, -30.41, 57.83, -2.37, 87.01, -25.66], 100)
                    time.sleep(0.15)
                    mc.send_angles([96.32, 11.42, 71.36, 58.79, -91.49, -18.54], 100)
                    time.sleep(0.15)
                    mc.send_angles([-73.03, -75.67, 77.69, 54.14, 80.77, -20.83], 100)
                    time.sleep(0.15)
                    mc.send_angles([-97.64, -1.4, -17.4, -24.52, 81.38, -20.83], 100)
                    time.sleep(0.15)
                    mc.send_angles([-97.47, -1.4, -49.04, -27.94, 80.77, -20.91], 100)
                    time.sleep(0.15)
                    mc.send_angles([-98.26, -1.31, 33.83, -22.41, 80.94, -20.91], 100)
                    time.sleep(0.15)
                    mc.send_angles([-2.98, -50.44, 51.67, 36.38, 82.26, -21.35], 100)
                    time.sleep(0.15)
                    mc.send_angles([-85.16, -6.67, 6.15, -4.3, -79.62, -5.97], 100)
                    time.sleep(0.15)
                    mc.send_angles([-97.47, -1.4, -49.04, -27.94, 80.77, -20.91], 100)
                else:
                    break
            if stop:
                break
    else:
        start = time.time()
        # COLOR = yellow
        mc.set_color(255, 140, 0)
        song_duration = 175
        minute = 60
        while time.time() - start < song_duration:
            if stop:
                break
            docs_ref.on_snapshot(on_snapshot)
            start_2 = time.time()
            while time.time() - start_2 < minute:
                if stop:
                    break
                docs_ref.on_snapshot(on_snapshot)
                x = 3
                while x > 0:
                    if stop:
                        break
                    docs_ref.on_snapshot(on_snapshot)
                    if time.time() - start < song_duration:
                        mc.send_angles([0, -15, -20, 20, 40, 50], 100)
                        time.sleep(0.15)
                        mc.send_angles([0, 15, 20, 20, -40, 50], 100)
                        time.sleep(0.15)
                        x -= 1
                    else:
                        break
                x = 3
                if stop:
                    break
                while x > 0:
                    if stop:
                        break
                    docs_ref.on_snapshot(on_snapshot)
                    if time.time() - start < song_duration:
                        mc.send_angles([-90.0, 3.16, 0.08, -3.07, 90.52, -43.94], 100)
                        time.sleep(0.15)
                        mc.send_angles([-89.47, 24.52, -35.41, -2.02, 78.92, -34.27], 100)
                        time.sleep(0.1)
                        mc.send_angles([-89.56, -27.86, 49.13, -15.38, 80.59, -34.27], 100)
                        time.sleep(0.1)
                        mc.send_angles([2.72, -0.79, 2.28, -0.17, -6.06, -2.19], 100)
                        time.sleep(0.1)
                        mc.send_angles([2.9, 4.21, 84.37, 41.66, -6.06, -2.19], 100)
                        x -= 1
                    else:
                        break
                x = 3
                if stop:
                    break
                while x > 0:
                    if stop:
                        break
                    docs_ref.on_snapshot(on_snapshot)
                    if time.time() - start < song_duration:
                        mc.send_angles([-1.49, 55, -150, 80, 100, 137.9], 100)
                        time.sleep(0.15)
                        mc.send_angles([-1.49, 115, -150, 30, -100, 137.9], 100)
                        time.sleep(0.15)
                        x -= 1
                    else:
                        break
                if stop:
                    break
                x = 3
                while x > 0:
                    if stop:
                        break
                    docs_ref.on_snapshot(on_snapshot)
                    mc.send_angles([0, 0, 0, 20, 30, 0], 100)
                    time.sleep(0.15)
                    mc.send_angles([-10, 20, 0, -45, -30, 0], 100)
                    time.sleep(0.15)
                    x -= 1
                if stop:
                    break

    print("Happy dance")
    stopRobot()


### HELPER FUNCTIONS ###

def updateBodyToSnake():
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'body': "SNAKE"})


def goToSnakeMode(speed=50):
    ##idle state == snake, speed accordingly
    pulsingLight()
    mc.send_angles([88.68, -138.51, 155.65, -128.05, -9.93, -15.29], speed)
    time.sleep(4)


def pauseRobot(sleepTime=0):
    time.sleep(sleepTime)
    mc.pause()


def resumeRobot(sleepTime=0):
    time.sleep(sleepTime)
    mc.resume()
    db.collection(u'android-robot-communication').document("MESSAGE").update({u'body': "PLAYING"})


def stopRobot(sleepTime=0):
    global stop
    stop = True
    time.sleep(sleepTime)
    mc.stop()
    goToSnakeMode()
    updateBodyToSnake()


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
