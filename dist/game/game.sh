#!/bin/sh
. setEnv.sh

# exit codes of GameServer:
#  0 normal shutdown
#  2 reboot attempt
while :; do
	curl --silent -o l2jserver.jar https://client.vae-soli.fr/experimental/l2jserver.jar
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	[ -f log/item.log ] && mv log/item.log "log/`date +%Y-%m-%d_%H-%M-%S`_item.log"
	[ -f log/chat.log ] && mv log/chat.log "log/`date +%Y-%m-%d_%H-%M-%S`_chat.log"
	/usr/bin/java $JAVA_OPTS \
	-Xms$JAVA_MIN_MEMORY -Xmx$JAVA_MAX_MEMORY \
	-XX:+UseG1GC \
	-Djava.util.logging.manager=com.l2jserver.util.L2LogManager \
	-Dpython.cachedir=../cachedir \
	-Dhttps.proxyHost=$JAVA_PROXY_HOST \
	-Dhttps.proxyPort=$JAVA_PROXY_PORT \
	-Djava.net.preferIPv4Stack=true \
	-jar l2jserver.jar &
	wait $!
	[ $? -ne 2 ] && break
	sleep 20
done
