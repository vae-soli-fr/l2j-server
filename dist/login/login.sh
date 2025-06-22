#!/bin/sh
. setEnv.sh

# exit codes of LoginServer:
#  0 normal shutdown
#  2 reboot attempt
while :; do
	curl --silent -o l2jlogin.jar https://client.vae-soli.fr/experimental/l2jlogin.jar
	[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
	/usr/bin/java $JAVA_OPTS \
	-Xms$JAVA_MIN_MEMORY -Xmx$JAVA_MAX_MEMORY \
	-XX:+UseG1GC \
	-Dhttps.proxyHost=$JAVA_PROXY_HOST \
	-Dhttps.proxyPort=$JAVA_PROXY_PORT \
	-Djava.net.preferIPv4Stack=true \
	-jar l2jlogin.jar &
	wait $!
	[ $? -ne 2 ] && break
	sleep 20
done
