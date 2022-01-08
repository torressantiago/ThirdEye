#!/bin/bash

# Creating the service to execute scheduler automatically
sudo cp Service/ThirdEye.service /etc/systemd/system/
sudo systemctl enable ThirdEye.service
sudo systemctl start ThirdEye.service

# Install improved voice synthetizer
sudo apt-get update
sudo apt install espeak-ng
sudo apt install mbrola
sudo apt-get install mbrola-fr2

# Render scripts executable
chmod +x ObjectDetection/ObjectDetection.sh
chmod +x TextRecognition/TextDetection.sh