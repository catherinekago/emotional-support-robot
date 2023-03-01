from pymycobot import MyCobotSocket
import time

mc = MyCobotSocket("192.168.1.106", 9000)

# COLOR = blue
mc.set_color(255, 0, 0)

mc.send_angles([-75, 45, 90, -130, 75, 0], 50)
print("Position 4")
time.sleep(2)

while True:
    mc.send_angles([-90, 30, 15, -45, 90, 0], 10)
    print("Position 1")
    time.sleep(4)

    mc.send_angles([45, 30, 15, -45, -45, 0], 17)
    print("Position 2")
    time.sleep(4)

    mc.send_angles([30, 45, 90, -130, -30, 0], 10)
    print("Position 3")
    time.sleep(4)

    mc.send_angles([-75, 45, 90, -130, 75, 0], 17)
    print("Position 4")
    time.sleep(4)