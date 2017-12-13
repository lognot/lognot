lognot
======
                  
Simple tool that scan log files periodically and notify if filters match.

Build ![alt text]( https://travis-ci.org/lognot/lognot.svg?branch=master "Build status" ) [![codecov](https://codecov.io/gh/lognot/lognot/branch/master/graph/badge.svg)](https://codecov.io/gh/lognot/lognot)
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

To run lognot run
```mvn spring-boot:run```

To build the jar run
```mvn package```
It will create the distributable archives containing the lognot.sh scrip

Configuration
-------------

In order to run lognot you need to create a yml config file, similar to src/main/resources/application.yml file.
See an example bellow of how to configure lognot to send notifications for two log files.

```
lognot:
  notification:
    recipients: jane.roe@email.com, richard.doe@mail.io

# Files to scan. Mandatory configuration

files:
- key: app
  path: /opt/app/logs/console.log
  regEx: .*ERROR.*|.*Exception.*

- key: apache
  path: /var/log/httpd/error.log
  regEx: .*caught.*

# Date format patterns are allowed in file path.
- key: server
  path: /opt/server/logs/console-%d{yyyyMMdd}.log
  regEx: .*ERROR.*|.*SEVERE.*


# Email configuration
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: alert@example.com
    password: thisisyourpassword
    properties.mail.smtp:
      auth: true
      starttls.enable: true

logging:
  level:
    root: WARN
    io.lognot: WARN
    io.lognot.LognotApplication: WARN

```

Setup and run
-------------

First create a copy of the src\main\resources\application.yml file - let's 
call it lognot.yml - and set email properties, and files to scan, similar to the example above.

Copy the lognot archive machine you want to run lognot on, unarchive, add your lognot.yml configuration file and run
```
./lognot -f lognot.yml > lognot.log 2>&1 &
``` 
to start it in background.

Contributions
-------------
Contributions via pull requests are gladly accepted!
