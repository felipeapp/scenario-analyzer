:: This file will run an ant build file N times
:: How to run:
::		run_ant_file.bat target n [file]
:: target: an ant target from build file
:: n: number of executions
:: file: an file to log stdout and stderr

@echo off

IF "%1" == "" GOTO HelpMessage
IF "%2" == "" GOTO HelpMessage

FOR /L %%I IN (1,1,%2) DO (
	echo "%%I: Running ant build file..."
	IF "%3" == "" (
		echo "%%I: ant %1"
		ant %1
	) ELSE (
		echo "%%I: ant %1 > exec%%I_%3 2>&1"
		ant %1 > exec%%I_%3 2>&1
	)
	
	echo "%%I: Dumping database..."
	echo "%%I: pg_dump --host localhost --port 5432 --username postgres --no-password --format custom --blobs --file scenario_analyzer_db_%%I.backup scenario_analyzer_db"
	pg_dump --host localhost --port 5432 --username postgres --no-password --format custom --blobs --file scenario_analyzer_db_%%I.backup scenario_analyzer_db
)

GOTO EndScript

:HelpMessage
echo "Ex 1: run_ant_file.bat tests 10"
echo "Ex 2: run_ant_file.bat tests 10 tests.txt"

:EndScript
