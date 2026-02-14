@echo off
cd /d "%~dp0"
start /wait uqm.exe -x
echo EXIT_CODE=%ERRORLEVEL% > test_result.txt
