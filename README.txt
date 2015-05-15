List of the machines:
huzvak01 - Node with Kafka broker and ZooKeeper
huzvak02 - Node aimed for Esper project, also contains ZooKeeper
huzvak03 - Node aimed for Esper project, also contains ZooKeeper
huzvak04 - Node aimed for Esper project, also contains ZooKeeper

How to connect to the machines: 
- you have to have your public ssh key on the server (currently only author, supervisor and oponent)
- connect through following commands from command line:
ssh huzvak-01@147.251.43.240
ssh huzvak-02@147.251.43.218
ssh huzvak-03@147.251.43.236
ssh huzvak-04@147.251.43.219

How to run Kafka broker:
- you have to be connected to huzvak01 machine
- move to diplomka/kafka/kafka_2.9.2-0.8.1.1/ folder located in user home folder with following command:
cd diplomka/kafka/kafka_2.9.2-0.8.1.1/

- run Kafka broker with following command:
./bin/kafka-server-start.sh ./config/server.properties

- to stop Kafka broker just press Control + C

How to run ZooKeeper command line interface (ZK cli):
- you have to be connected to any from above mentioned four machines (probably it will works from any machine that has installed ZooKeeper and is in the Masaryl University network)
- move to folder /usr/share/zookeeper/bin/ located in user home folder with following command:
cd /usr/share/zookeeper/bin/
- run ZK cli with following command:
./zkCli.sh -server 147.251.43.240:2181,147.251.43.218:2181,147.251.43.236:2181,147.251.43.219:2181

Commands for cli ZK:
- for all commands just type "help" and press enter
- here are the most used commands:
create /path/to/znode data - this command create new node on given path and store given data into it
get /path/to/znode - return all the information together with data of the znode on given path
ls /path/to/znode - list all children of the znode
set /path/to/znode data dataversion - set the data into given znode. Valid only if correct dataversion is provided, otherwise fails. The dataversion number can be read from output from get command
delete /path/to/znode dataversion - delete znode on given path if correct dataversion is provided


Producer: (can be run from any computer connected to the MU network, and with at least Java 1.7 installed)
- move to the folder where the jar file with dependencies is located
- run following command (the number at the end, is the number of topic to which data should be produced):
java -jar kafka-producer-1.0-SNAPSHOTar-with-dependencies.jar 1


Esper and consumer: (should be run from machines huzvak02, huzvak03 and huzvak04) 
- currently cannot be run from other machines in MU network, because ZK timeout exception is set to 400 ms which is too low
- when connected to the machine, move to folder "projekt" in home folder. You can use following command:
cd ./projekt

- to start consumer, use the following command, where argumenst are following: 
   - 1st argument is the znode from which configuration should be read
   - 2nd is the consumer group id
   - 3rd is the string with IP addresses and ports to ZooKeeper nodes
- example of the command is:
java -jar CEP-esper-consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar /esper/node1 1 147.251.43.240:2181,147.251.43.218:2181,147.251.43.236:2181,147.251.43.219:2181
