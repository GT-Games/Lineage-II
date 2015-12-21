#!/bin/bash
cd C:/Users/Anthony/Source/dist/login
err=1
until [ $err == 0 ];
do
[ -f log/java0.log.0 ] && mv log/java0.log.0 "log/`date +%Y-%m-%d_%H-%M-%S`_java.log"
[ -f log/stdout.log ] && mv log/stdout.log "log/`date +%Y-%m-%d_%H-%M-%S`_stdout.log"
nice -n -2 java -Xmx1g -cp ../libs/*:AuthLogin.jar com.l2jserver.loginserver.L2LoginServer
err=$?
# /etc/init.d/mysql restart
sleep 10;
done
