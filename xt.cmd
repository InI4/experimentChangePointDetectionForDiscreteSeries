@echo off
del plot*.dat
for /L %%i in (0,1,3) do (
	grep "^ *%1:%%i  " data/data.log > plot%%i.dat
	head -1 plot%%i.dat
)
