@echo off
echo ============================================
echo   Smart Hotel Reservation System Launcher
echo ============================================
echo.
if not exist "bin\" (
    echo Compiling source files...
    mkdir bin
    for /r src %%f in (*.java) do javac -d bin -sourcepath src "%%f"
    echo Compilation complete.
    echo.
)
echo Starting application...
java -cp bin com.hotel.Main
