### TextDetection.py
Code that uses the captured image "image.jpg" to detect text using a pretrained Artificial Intelligence (Tesseract) and save said text to recognizedtext.txt

### TextRecognition.sh
Script that captures the image, then executes TextDetection.py and speaks the recognizedtext.txt outloud.

### samplePhotos
Repository to test TextDetection.py

### image.jpg, index.png, testflou.png
Since the Raspberry Pi's Camera V2 is quite bad, some sample pictures taken with a smartphone had to be uploaded in order to test TextDetection.py. In a further iteration of this project, we expect to use a better camera.

### recognized.txt
Recognized text produced bt TextDetection.py