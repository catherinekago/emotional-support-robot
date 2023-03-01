from pymycobot import MyCobotSocket
import time

mc = MyCobotSocket("192.168.1.106", 9000)

def startBreathingExercise():
    # COLOR = blue
    mc.set_color(255, 0, 0)

    for i in range(2):
        mc.send_angles([-45, 0, 90, -90, 75, 0], 10)
        print("Position 1")
        time.sleep(5)

        mc.send_angles([45, 0, 90, -90, -45, 0], 10)
        print("Position 2")
        time.sleep(5)

        # mc.send_angles([90,45,-90,90,-90,90],50)