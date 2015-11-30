@echo off
title Server Console
:start
echo.
echo. Loading Server..
echo.
REM -------------------------------------
REM Default parameters for a basic server. 
java -Xmx1g -XX:+AggressiveOpts -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalPacing -XX:+HeapDumpOnOutOfMemoryError -cp ../libs/*;./Server.jar; com.l2jserver.gameserver.GameServer
REM
REM If you have a big server and lots of memory, you could experiment for example with
REM java -server -Xmx1536m -Xms1024m -Xmn512m -XX:PermSize=256m -XX:SurvivorRatio=8 -Xnoclassgc -XX:+AggressiveOpts
REM -------------------------------------
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
pause
