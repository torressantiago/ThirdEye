#!/bin/python3
# Script to schedule programs on the press of a button
import RPi.GPIO as GPIO

# setup with internal pullups and pin in READ mode
PORT_NUM = 9 # Port where button is connected

GPIO.setmode(GPIO.BCM)
GPIO.setup(PORT_NUM, GPIO.IN, pull_up_down = GPIO.PUD_DOWN)

while 1:
    print(GPIO.input(PORT_NUM))
