@echo off
if "%CENTIPEDE_HOME%"=="" goto no_centipede
%CENTIPEDE_HOME%\bin\cent.bat %*
goto exit
:no_centipede
echo You must set CENTIPEDE_HOME to point at your Centipede installation
:exit
  