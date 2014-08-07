@echo off

REM This file will run the maven test goal N times
REM How to run:
REM		run_maven_test.bat n
REM n: number of executions

IF "%1" == "" GOTO HelpMessage

FOR /L %%I IN (1,1,%1) DO (
	echo %%I: mvn test -Dcheckstyle.skip=true -Danimal.sniffer.skip=true -DskipAutobahnTestsuite=true -Dsurefire.reportNameSuffix=%%I
	cmd /c mvn test -Dcheckstyle.skip=true -Danimal.sniffer.skip=true -DskipAutobahnTestsuite=true -Dsurefire.reportNameSuffix=%%I
	
	echo %%I: pg_dump --host localhost --port 5432 --username postgres --no-password --format custom --blobs --file scenario_analyzer_db_%%I.backup scenario_analyzer_db
	cmd /c pg_dump --host localhost --port 5432 --username postgres --no-password --format custom --blobs --file scenario_analyzer_db_%%I.backup scenario_analyzer_db
)

GOTO EndScript

:HelpMessage
echo Ex 1: run_maven_test.bat 10

:EndScript
echo Bye...
