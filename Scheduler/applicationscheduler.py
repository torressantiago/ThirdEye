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
	if (GPIO.input(PORT_NUM) == True): # Service button is pressed
		counter = counter + 1
		if(counter == 1):
			try:
				# Launch of first thread - TextRecognition - All other threads are killed
				subprocess.Popen(["pkill","-f","ObjectDetection.py"])
				subprocess.Popen(["espeak-ng","-v","mb/mb-fr2","Recognition de texte en cours d'exécution"])
				subprocess.Popen(["bash","/home/pi/ThirdEye/TextRecognition/TextRecognition.sh","&"])
				time.sleep(1) # wait for a second
			except:
				print("error ocurred")
		elif(counter == 2):
			try:
				# Launch of second thread - ObjectDetection - All other threads are killed
				process_stop("TextRecognition.sh")
				subprocess.Popen(["pkill","-f","TextDetection.py"])
				subprocess.Popen(["espeak-ng","-v","mb/mb-fr2","Recognition des alentours en cours d'exécution"])
				subprocess.Popen(["python3","/home/pi/ThirdEye/ObjectDetection/ObjectDetection.py","&"])
				counter = 0
				time.sleep(1) # wait for a second
			except:
				print("error ocurred")
	if(GPIO.input(EMRGCY_PORT_NUM) == True): # Help button is pressed
		helpcounter = helpcounter + 1
		if(helpcounter == 1):
			# Request for help
			subprocess.Popen(["bash","/home/pi/ThirdEye/EmergencyCall/thereisemergency.sh","13.40.96.149","8080"])
			subprocess.Popen(["espeak-ng","-v","mb/mb-fr2","Aide demandée"])
			time.sleep(1) # wait for a second
		elif(helpcounter == 2):
			# Stop help request
			subprocess.Popen(["bash","/home/pi/ThirdEye/EmergencyCall/noemergency.sh","13.40.96.149","8080"])
			subprocess.Popen(["espeak-ng","-v","mb/mb-fr2","Arrêt de demande d'aide"])
			time.sleep(1) # wait for a second
			helpcounter = 0

# NOTE	The button has to be connected in pull down mode.
# NOTE	This can be run a systemd task or cronjob
