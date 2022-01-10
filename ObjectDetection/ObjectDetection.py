#!/bin/python3
# Import des librairies GPIO et time (temps et conversion) #
import RPi.GPIO as GPIO
import time
import subprocess

# Module GPIO: BOARD ou BCM (numérotation comme la sérigraphie de la carte ou comme le chip) #
GPIO.setmode(GPIO.BCM)

# Définition des broches GPIO #
GPIO_TRIGGER = 18
GPIO_ECHO = 24
GPIO_VIBRATION = 13

# Définition des broches en entrée ou en sortie #
GPIO.setup(GPIO_TRIGGER, GPIO.OUT)
GPIO.setup(GPIO_ECHO, GPIO.IN)
GPIO.setup(GPIO_VIBRATION, GPIO.OUT)

def distance():
	# Mise à l'état haut de la broche Trigger #
	GPIO.output(GPIO_TRIGGER, True)

	# Mise à l'état bas de la broche Trigger après 10 μS #
	time.sleep(0.00001)
	GPIO.output(GPIO_TRIGGER, False)

	StartTime = time.time()
	StopTime = time.time()

	# Enregistrement du temps de départ des ultrasons #
	while GPIO.input(GPIO_ECHO) == 0:
		StartTime = time.time()
	# Enregistrement du temps d'arrivée des ultrasons #
	while GPIO.input(GPIO_ECHO) == 1:
		StopTime = time.time()

	# Calcul de la durée de l'aller-retour des US #
	TimeElapsed = StopTime - StartTime
	# On multiplie la durée par la vitesse du son: 34300 cm/s #
	# Et on divise par deux car il s'agit d'un aller et retour. #
	distance = (TimeElapsed * 34300) / 2

	return distance

if __name__ == '__main__':
	try:
		while True:
			dist = distance()
			if dist < 100:
				# Faire vibrer le moteur si un objet est à proximité #
				GPIO.output(GPIO_VIBRATION, 1)
				time.sleep(0.5)
				GPIO.output(GPIO_VIBRATION, 0)
				subprocess.Popen(["espeak-ng","-v","mb/mb-fr2","Objet à proximité"])
			print ("Distance mesurée = %.1f cm" % dist)
			time.sleep(1)
		# On reset le programme via CTRL+C #
	except KeyboardInterrupt:
		print("Mesure stoppée")
		GPIO.cleanup()
