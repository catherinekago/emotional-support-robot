from pymycobot import MyCobotSocket
import time
from datetime import datetime

mc = MyCobotSocket("192.168.1.106", 9000)

counter = 0
function = "increment"

print(mc.get_ssid_pwd())

# COLOR = white
mc.set_color(250, 250, 250)
mc.send_angles([0, 0, 0, 0, 0, 0], 50)
time.sleep(2)

while True:
    print(counter)
    mc.set_color(25*counter, 25*counter, 25*counter)
    if function == "increment":
        counter += 1
    elif function == "decrement":
        counter -= 1

    if (counter == 5):
        function = "decrement"
    elif (counter == 0):
        function = "increment"
    print(counter)
    print(function)
    if counter == 5: break

"""
while True:
    for i in range(5):
        print(i*25)
        mc.set_color(25*i,25*i,25*i)
    for i in range(5):
        print((5-i)*25)
        mc.set_color(25*(5-i),25*(5-i),25*(5-i))
    
    mc.set_color(25,25,25)
    mc.set_color(50,50,50)
    mc.set_color(75,75,75)
    mc.set_color(100,100,100)
    mc.set_color(125,125,125)
    mc.set_color(150,150,150)
    mc.set_color(175,175,175)
    mc.set_color(200,200,200)
    mc.set_color(225,225,225)
    mc.set_color(250,250,250)
    mc.set_color(225,225,225)
    mc.set_color(200,200,200)
    mc.set_color(175,175,175)
    mc.set_color(150,150,150)
    mc.set_color(125,125,125)
    mc.set_color(100,100,100)
    mc.set_color(75,75,75)
    mc.set_color(50,50,50)
    mc.set_color(25,25,25)
    mc.set_color(0,0,0)
    """