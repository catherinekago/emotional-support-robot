from pymycobot import MyCobotSocket
import time
from datetime import datetime

mc = MyCobotSocket("192.168.1.106", 9000)

print(mc.get_ssid_pwd())

# COLOR = blue
mc.set_color(0, 0, 255)
mc.send_angles([-90, 30, 15, -45, 90, 0], 50)
time.sleep(2)

while True:
    start = datetime.now()
    print(start.strftime("Start breathing in: %H:%M:%S"))
    mc.send_angles([45, 30, 15, -45, -45, 0], 17)
    #V1.1: from dark blue to light blue
    """
    mc.set_color(0, 40, 255)
    mc.set_color(0, 85, 255)
    mc.set_color(0, 130, 255)
    mc.set_color(0, 175, 255)
    """
    #V1.2: from dark blue to light blue
    mc.set_color(0, 60, 255)
    mc.set_color(0, 120, 255)
    mc.set_color(0, 180, 255)
    mc.set_color(0, 240, 255)
    #V2: from blue to green
    """
    mc.set_color(0, 65, 195)
    mc.set_color(0, 130, 130)
    mc.set_color(0, 195, 65)
    mc.set_color(0, 255, 0)
    """
    stop = datetime.now()
    print(stop.strftime("Stop breathing in: %H:%M:%S"))
    print("Difference: " + str((stop - start).seconds))

    start = datetime.now()
    print(start.strftime("Hold start: %H:%M:%S"))
    mc.sync_send_angles([30, 45, 90, -130, -30, 0], 10)
    stop = datetime.now()
    print(stop.strftime("Hold stop: %H:%M:%S"))
    print("Difference: " + str((stop - start).seconds))

    start = datetime.now()
    print(start.strftime("Start breathing out: %H:%M:%S"))
    mc.send_angles([-75, 45, 90, -130, 75, 0], 17)
    #V1.1: from light blue to dark blue
    """
    mc.set_color(0, 130, 255)
    mc.set_color(0, 85, 255)
    mc.set_color(0, 40, 255)
    mc.set_color(0, 0, 255)
    """
    #V1.2: from light blue to dark blue
    mc.set_color(0, 180, 255)
    mc.set_color(0, 120, 255)
    mc.set_color(0, 60, 255)
    mc.set_color(0, 0, 255)
    #V2: from green to blue
    """
    mc.set_color(0, 195, 65)
    mc.set_color(0, 130, 130)
    mc.set_color(0, 65, 195)
    mc.set_color(0, 0, 255)
    """
    stop = datetime.now()
    print(stop.strftime("Stop breathing out: %H:%M:%S"))
    print("Difference: " + str((stop - start).seconds))
    
    start = datetime.now()
    print(start.strftime("Hold start: %H:%M:%S"))
    mc.sync_send_angles([-90, 30, 15, -45, 90, 0], 10)
    stop = datetime.now()
    print(stop.strftime("Hold stop: %H:%M:%S"))
    print("Difference: " + str((stop - start).seconds))

