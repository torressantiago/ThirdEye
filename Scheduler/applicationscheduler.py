#!/bin/python
# Script to schedule programs on the press of a button
import RPi.GPIO as GPIO
import time
import os

# setup with internal pullups and pin in READ mode
PORT_NUM = 23 # Port where button is connected
counter = 1

GPIO.setmode(GPIO.BCM)
GPIO.setup(23, GPIO.IN, pull_up_down = GPIO.PUD_UP)

#  Main Loop
while 1:
    if (GPIO.input(23) == False && counter == 1):
        if(counter == 1):
		os.system("<Launch Program 1>")
		counter = 2
		time.sleep(1) # wait for a second
		break
	elif(counter == 2):
		os.system("<Launch Program 2>")
		counter = 3
		time.sleep(1) # wait for a second
		break
	elif(counter == 3):
		os.system("<Launch Program 3>")
		counter = 1
		time.sleep(1) # wait for a second
		break

# NB : The button has to be connected in pull down mode.
# This can be run a systemd task or cronjob
