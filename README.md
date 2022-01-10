# ThirdEye
This project aims to be of assistance to blind people that want to stay autonomous in their day to day lives.

# Project configuration
In order to configure the project, the systemd service must be operational. This can operation can be acheived through `config.sh` which will enable the service and start it. The script will configure as well `espeak-ng` which will allow the embedded system to talk to the user. 
```
$ bash config.sh
```
*NOTE*: To use the text detection software Google's tesseract must be compiled and installed from source and language packages must be downloaded and put in tesseract's folder.