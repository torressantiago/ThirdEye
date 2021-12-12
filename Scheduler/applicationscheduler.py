#!/bin/python3
# Script to schedule programs on the press of a button
import RPi.GPIO as GPIO
import time
import subprocess
import os

def process_stop(pname):
    # get name of process
    name = pname
    try:
        # iterating through each instance of the process
        for line in os.Popen("ps ax | grep " + name + " | grep -v grep"):
            fields = line.split()
             
            # extracting Process ID from the output
            pid = fields[0]
             
            # terminating process
            os.kill(int(pid), signal.SIGKILL)
        print("Process Successfully terminated")
         
    except:
        print("No process to close")

# setup with internal pullups and pin in READ mode
PORT_NUM = 11 # Port where button is connected
counter = 0
helpcounter = 0

EMRGCY_PORT_NUM = 9 # Port where the emergency button is connected

GPIO.setmode(GPIO.BCM)
GPIO.setup(PORT_NUM, GPIO.IN, pull_up_down = GPIO.PUD_DOWN)
GPIO.setup(EMRGCY_PORT_NUM, GPIO.IN, pull_up_down = GPIO.PUD_DOWN)

#  Main Loop
while 1:
	if (GPIO.input(PORT_NUM) == True):
		counter = counter + 1
		if(counter == 1):
			process_stop("test2.sh")
			subprocess.Popen(["/home/pi/ThirdEye/Scheduler/test1.sh","&"])
			subprocess.Popen(["espeak","test1 running"])
			time.sleep(1) # wait for a second
		elif(counter == 2):
			process_stop("test1.sh")
			subprocess.Popen(["/home/pi/ThirdEye/Scheduler/test2.sh","&"])
			subprocess.Popen(["espeak","test2 running"])
			counter = 0
			time.sleep(1) # wait for a second
	elif(GPIO.input(EMRGCY_PORT_NUM) == True):
		helpcounter = helpcounter + 1
		if(helpcounter == 1):
			subprocess.Popen(["espeak","test3 running"])
			subprocess.Popen(["/home/pi/ThirdEye/Scheduler/test3.sh","&"])
			time.sleep(1) # wait for a second
		elif(helpcounter == 2):
			subprocess.Popen(["espeak","test3 stopped"])
			process_stop("test3.sh")
			time.sleep(1) # wait for a second
			helpcounter = 0

# NOTE	The button has to be connected in pull down mode.
# NOTE	This can be run a systemd task or cronjob
