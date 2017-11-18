# lognot
Simple tool that scan log files periodically and notify if filters match.

Build ![alt text]( https://travis-ci.org/robertsicoie/lognot.svg?branch=master "Build status" )
====================================================================================
To run lognot run
```mvn spring-boot:run```

To build the jar run
```mvn package```

Setup and run
=============
First create a copy of the src\main\resources\application.yml file - let's 
call it lognot.yml - and set email properties, and files to scan.

Copy the jar file, lognot.sh file and the lognot.yml to the machine you 
want to run lognot on an run 
```./lognot -f lognot.yml > lognot.log 2>&1 &``` 
to start it in backgrounf.