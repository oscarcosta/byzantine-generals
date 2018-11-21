# Byzantine Generals Problem

### Build
The system is build using *Gradle version 4.x* using the command:

`$ ./gradlew build`                                                          

The jar file will be generated inside the folder **build/lib/**.

### Run
To execute the system, just run the *java -jar* passing the system parameters on the command line:

**java -jar ByzantineGenerals \<m\> \<g1,g2,g3,...,gn\> \<o\>**                      

,where

- **\<m\>** is a Integer representing the level of recursion, assuming that m > 0

- **\<g1,g2,g3,...,gn\>** is a String representing the list of general names, separated by 	comma ','

- **\<o\>** is a String representing the commander order, that could be ATTACK or RETREAT

This execution also assumes <m> as the number of traitors, which are randomly selected.

For example:

`$ java -jar ByzantineGenerals-1.0-SNAPSHOT.jar 1 G0,G1,G2,G3 ATTACK`
                         
, runs the system for 4 generals with the command ATTACK, 1 recursion level and 1 random traitor.