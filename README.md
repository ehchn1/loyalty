**Introduction**

This is the Valiantlytix Loyalty application for version 1.0.0

**Pre-installation requirements**

Below are the minimum version and neccesary software and tools required to build and run the application:

1. Java Development Kit 11
2. Maven 3
3. MySQL Database Community Server 8 

**Installation instructions**

1. Download and install the Java Development Kit
2. Download and install Maven
3. Install MySQL Community Server Database and create a database called "loyalty"

   ```mysql> create database loyalty```

4. Apply and run the database script provided - src/main/resources/scripts/loyalty-1.0.0.sql

   ```mysql -u root -p loyalty < loyalty-1.0.0.sql```

5. Checkout the source code from Github
6. Run mvn clean install to build and install the application

   ```mvn clean install```

7. The built binaries called loyalty-1.0.0.jar is located at /target folder

   ```/target/loyalty-1.0.0.jar```

8. To start up the application, run the below:

   ```java -jar /target/loyalty-1.0.0.jar```

**Configuration settings**

These are the available properties for the application:
|Configuration     |Parameter                    |Default value                                                                                      |
|------------------|-----------------------------|--------------------------------------------------------------------------------------------|
|Context Path      |server.servlet.context-path  |/loyalty                                                                                    |
|Database URL      |spring.datasource.url        |jdbc:mysql://localhost:3306/loyalty?serverTimezone=Asia/Kuala_Lumpur&characterEncoding=utf8 |
|Database Driver   |spring.datasource.driver     |com.mysql.cj.jdbc.Driver                                                                    |
|Database User     |spring.datasource.username   |root                                                                                        |
|Database Password |spring.datasource.password   |p@ssword                                                                                    |
|JWT Secret        |jwt.secret                   |javainuse                                                                                   |

To override these configuration, create a file call application.properties with the expected values at the **location used to start the application**. Sample content as below:

```
server.servlet.context-path=/loyalty

spring.datasource.url=jdbc:mysql://localhost:3306/loyalty?serverTimezone=Asia/Kuala_Lumpur&characterEncoding=utf8
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=p@ssw0rd

jwt.secret=javainuse
```

**Notes**
1. During build, only unit tests are executed. API tests are provided using REST assured library
2. Service layer are omitted to reduce the completixity for the assignment
