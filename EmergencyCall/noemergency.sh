#bin/bash
# $1: MESSAGE TO EMIT
# $2: IP ADDRESS OF SERVER
# $3: SERVICE PORT
echo -n 'Y' | nc -w 1 $2 $3

# example : bash webput.sh '1' 127.0.0.0 8080
