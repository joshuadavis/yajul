@echo off
rem set-env.bat - Sets environment variables required for building yajul-core
rem Edit this file to point the build process at your ANT and JDK installations
set ANT_HOME=C:\java\apache-ant-1.5.3-1
set JAVA_HOME=C:\java\j2sdk1.4.2_01
rem Add all the bundled libraries to the classpath.
set LOCALCLASSPATH=%CLASSPATH%
set SCRIPT_HOME=%~dp0
for %%i in ("%SCRIPT_HOME%\..\lib\*.jar") do call ".\bin\lcp.bat" %%i
set CLASSPATH=%LOCALCLASSPATH%
PATH=%ANT_HOME%\bin;%JAVA_HOME%\bin;%PATH%