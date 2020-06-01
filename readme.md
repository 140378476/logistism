# Logistism

A simple logistics system based on web server. In this system, users can post orders of delivery, 
postmen can be assigned to retrieve and deliver orders and managers can 
monitor the state of the system. 
Items will be delivered through the network
of scheduled shipments, and the shipping plans will be created in advance.
In addition, shipping logs are maintained so that users can be acknowledged of 
the latest information about their goods.

This project is mainly based on [Kotlin](https://kotlinlang.org) and [Spring Boot](https://spring.io/projects/spring-boot).



## How to run the application:

First, make sure MySQL and Java of version 11 or higher are installed.

Second, clone this repository or download it. Import it with Intellij IDEA as a gradle project.

Third, you need to configure the file `application.properties`, change the username and the password of the database to 
your own.

Finally, run the main class `LogistismApplication` and it should be done.

 


## Appendix


### Development journal

* Day0: After thinking about what to do, I finally decided to make a logistics management system. I made 
a sketch of the system and listed the critical points of the system.
    
* Day1: I learnt the basic parts of Spring Boot and spent a whole afternoon to download all the dependencies for the project, 
switching from Maven(because it didn't work well in my computer) to Gradle. Finally, it could run. 
    
* Day2: I designed the tables and created the first controller. I learnt something about Spring Security for 
login service. At that night, it was possible for users to login.

* Day3: I finished basic parts of the controllers and pages.

* Day4: Plans and logs for shipments, scheduled tasks. The system is online.
