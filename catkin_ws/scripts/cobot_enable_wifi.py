from pymycobot.mycobot import MyCobot
import time

print("     Activating Cobot...")
mc = MyCobot('/dev/ttyACM0', 115200)
mc.power_on()
time.sleep(5)

print("Active")

mc.set_ssid_pwd("PEM-HRI", "Human-Robot Interaction")

print(mc.get_ssid_pwd())