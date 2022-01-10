#!/bin/bash
raspistill -o /home/pi/ThirdEye/TextRecognition/image.jpg -w 640 -h 480 # for vga res
espeak-ng -v mb/mb-fr2 "Image capturée, début du traitement de l'image"
python3 /home/pi/ThirdEye/TextRecognition/TextDetection.py
espeak-ng -v mb/mb-fr2 "Traitement de l'image terminé"
espeak-ng -v mb/mb-fr2 -f /home/pi/ThirdEye/TextRecognition/recognized.txt
espeak-ng -v mb/mb-fr2 "Lecture terminée"