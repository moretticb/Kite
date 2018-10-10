@echo off

if (%1)==(-a) (
	cd data
) else (
	cd scripts
	start /b runServer.bat
)

java -jar ..\jars\kiteServer.jar
