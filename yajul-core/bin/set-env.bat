@echo off
rem set-env.bat - Sets environment variables required for building yajul-core
rem Edit this file to point the build process at your ANT and JDK installations
if "%ANT_HOME%"=="" goto noAntHome
if "%JAVA_HOME%"=="" goto noJavaHome
rem Add all the bundled libraries to the classpath.
set LOCALCLASSPATH=%CLASSPATH%
set SCRIPT_HOME=%~dp0
for %%i in ("%SCRIPT_HOME%\..\lib\*.jar") do call ".\bin\lcp.bat" %%i
set CLASSPATH=%LOCALCLASSPATH%
goto finished
:noAntHome
echo [set-env] ANT_HOME has not been defined!
goto finished
:noJavaHome
echo [set-env] JAVA_HOME has not been defined!
goto finished
:finished
echo [set-env] finished
