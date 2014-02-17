set cp=lib\log4j-1.2.17.jar
c:\Programme\Java\jdk1.7.0_45\bin\javac.exe -cp %cp% -d classes src\*.java
rem if not errorlevel 1 copy data.log data.log.old
if not errorlevel 1 java -cp classes;%cp%;. CPD %*
