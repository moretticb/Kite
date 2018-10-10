#!/bin/bash

pid=$(ps aux | grep hsqldb | grep -v grep | sed -E "s/[ ]+/ /g" | cut -d " " -f 2)

if [ $pid ]; then
	echo "HSQLDB database already running (PID $pid)."
	cd data
else
	cd scripts
	./runServer.sh &
fi

java -jar ../jars/kiteServer.jar
