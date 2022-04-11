#!/bin/bash
NAME=`basename "$0" ".sh"`
JAVA=`which java`
USER=`whoami`
MIN=64m
MAX=128m

if [ $USER != 'l2jserver' ]; then
	echo "This script must be run as l2jserver user not $USER."
	exit;
fi

# exit codes of LoginServer:
#  0 normal shutdown
#  2 reboot attempt

while :; do
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	$JAVA -server \
	-Xms$MIN -Xmx$MAX \
	-XX:+UseG1GC \
	-Dhttps.proxyHost=proxy \
	-Dhttps.proxyPort=3128 \
	-Djava.net.preferIPv4Stack=true \
	-Djava.net.preferIPv4Addresses=true \
	-Dcom.sun.management.config.file=jmx.cfg \
	-Djava.rmi.server.hostname=loginserver.lan \
	-jar l2jlogin.jar > log/stdout.log 2>&1 &
	LS_PID=$!
	echo $LS_PID > $NAME.pid
	wait $LS_PID
	[ $? -ne 2 ] && break
	sleep 20
done
