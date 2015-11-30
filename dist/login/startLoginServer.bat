@echo off
title AuthLogin Console
:start
echo.
echo  Loading AuthLogin..
echo. 
java -Xmx1g -XX:+AggressiveOpts -XX:+UseConcMarkSweepGC -XX:+CMSIncrementalPacing -XX:+HeapDumpOnOutOfMemoryError -cp ../libs/*;./AuthLogin.jar; com.l2jserver.loginserver.L2LoginServer

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
