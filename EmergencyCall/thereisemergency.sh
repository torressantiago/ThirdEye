#bin/bash
# $1: IP ADDRESS OF SERVER
# $2: SERVICE PORT
echo -n 'N' | nc -w 1 $1 $2

# example : bash thereismergency.sh 127.0.0.0 8080
