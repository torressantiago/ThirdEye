#!/bin/bash
raspistill -o image.jpg -w 2560 -h 1920 # for 5MP res
espeak-ng -v mb/mb-fr2 "Image capturée, début du traitement de l'image"
python3 TextDetection.py
espeak-ng -v mb/mb-fr2 "Traitement de l'image terminé"
espeak-ng -v mb/mb-fr2 -f recognized.txt
