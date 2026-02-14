@echo off
cd /d D:\Test\UQM_port\sc2-uqm\build
D:\Test\UQM_port\sc2-uqm\mingw64\bin\mingw32-make.exe -j8 > build_out.txt 2>&1
echo Build exit: %ERRORLEVEL% > debug_result.txt
D:\Test\UQM_port\sc2-uqm\build\uqm.exe -x 2> stderr_debug.txt
echo Run exit: %ERRORLEVEL% >> debug_result.txt
