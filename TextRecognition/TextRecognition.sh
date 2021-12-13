#!/bin/bash
raspistill -o image.jpg -w 2560 -h 1920 # for 5MP res
espeak "Image captured"
python3 TextDetection.py
espeak "Processing done"
espeak `$cat recognized.txt`
