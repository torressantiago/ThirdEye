#bin/bash
# $1: MESSAGE TO EMIT
# $2: IP ADDRESS OF SERVER
# $3: SERVICE PORT
echo -n 'Y' | nc -w 1 $1 $2

# example : bash noemergency.sh 127.0.0.0 8080
