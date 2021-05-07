@echo off

@REM set JAVA_HOME=C:\Program Files\Java\jdk-9.0.4
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.10
call mvn -pl .,backend,gui clean compile dependency:build-classpath -Dmdep.outputFile=cp.txt
@REM call mvn clean compile dependency:build-classpath -Dmdep.outputFile=cp.txt
@REM pause
@REM cls
@REM "%JAVA_HOME%"\bin\java -p backend\target;C:\users\cwf\.m2\repository\??? -m backend/com.fostecar000.backend.Book