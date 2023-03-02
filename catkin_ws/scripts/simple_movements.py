from pymycobot import MyCobotSocket
import time

mc = MyCobotSocket("192.168.1.106", 9000)

for i in range(1):
    mc.send_angles([88.68, -138.51, 155.65, -128.05, -90, -15.29], 50)
