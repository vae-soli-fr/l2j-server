#!/bin/bash
NAME=`basename "$0" ".sh"`
JAVA=`which java`
USER=`whoami`
MIN=1g
MAX=3g

if [ $USER != 'l2jserver' ]; then
	echo "This script must be run as l2jserver user not $USER."
	exit;
fi

# exit codes of GameServer:
#  0 normal shutdown
#  2 reboot attempt

while :; do
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
	[ -f log/item.log ] && mv log/item.log "log/`date +%Y-%m-%d_%H-%M-%S`_item.log"
	[ -f log/chat.log ] && mv log/chat.log "log/`date +%Y-%m-%d_%H-%M-%S`_chat.log"
	$JAVA -server \
	-Xms$MIN -Xmx$MAX \
	-XX:+UseG1GC \
	-Djava.util.logging.manager=com.l2jserver.util.L2LogManager \
	-Dpython.cachedir=../cachedir \
	-Djava.net.preferIPv4Stack=true \
	-Djava.net.preferIPv4Addresses=true \
	-Dcom.sun.management.config.file=jmx.cfg \
	-Djava.rmi.server.hostname=gameserver.lan \
	-jar l2jserver.jar > log/stdout.log 2>&1 &
	GS_PID=$!
	echo $GS_PID > $NAME.pid
	wait $GS_PID
	[ $? -ne 2 ] && break
	sleep 20
done
