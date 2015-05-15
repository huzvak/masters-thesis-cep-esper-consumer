#Instructions: how to use
In this file you can find instructions how to use CEP-esper-consumer project as well as project from repository (masters-thesis-kafka-producer)[https://github.com/huzvak/masters-thesis-kafka-producer]

##List of the machines:
* _huzvak01_ - Node with Kafka broker and ZooKeeper
* _huzvak02_ - Node aimed for Esper project, also contains ZooKeeper
* _huzvak03_ - Node aimed for Esper project, also contains ZooKeeper
* _huzvak04_ - Node aimed for Esper project, also contains ZooKeeper

##How to connect to the machines: 
* you have to have your public ssh key on the server (currently only author, supervisor and oponent)
* connect through following commands from command line:
 * _ssh huzvak-01@147.251.43.240_
 * _ssh huzvak-02@147.251.43.218_
 * _ssh huzvak-03@147.251.43.236_
 * _ssh huzvak-04@147.251.43.219_

##How to run Kafka broker:
* you have to be connected to huzvak01 machine
* move to _diplomka/kafka/kafka_2.9.2-0.8.1.1/_ folder located in user home folder with following command:
 * _cd diplomka/kafka/kafka_2.9.2-0.8.1.1/_
* run Kafka broker with following command:
 * _./bin/kafka-server-start.sh ./config/server.properties_
* to stop Kafka broker just press Control + C

##How to run ZooKeeper command line interface (ZK cli):
* you have to be connected to any from above mentioned four machines (probably it will works from any machine that has installed ZooKeeper and is in the Masaryk University network)
* move to folder _/usr/share/zookeeper/bin/_ located in user home folder with following command:
 * _cd /usr/share/zookeeper/bin/_
* run ZK cli with following command:
 * _./zkCli.sh -server 147.251.43.240:2181,147.251.43.218:2181,147.251.43.236:2181,147.251.43.219:2181_

##Commands for ZK cli:
* for all commands just type _help_ and press enter
* here are the most used commands:
 * _create /path/to/znode data_ - this command create new node on given path and store given data into it
 * _get /path/to/znode_ - return all the information together with data of the znode on given path
 * _ls /path/to/znode_ - list all children of the znode
 * _set /path/to/znode data dataversion_ - set the data into given znode. Valid only if correct dataversion is provided, otherwise fails. The dataversion number can be read from output from get command
 * _delete /path/to/znode dataversion_ - delete znode on given path if correct dataversion is provided


##How to run (Producer)[https://github.com/huzvak/masters-thesis-kafka-producer]
* (can be run from any computer connected to the MU network, and with at least Java 1.7 installed)
* move to the folder where the jar file with dependencies is located
* run following command (the number at the end, is the number of topic to which data should be produced):
 * _java -jar kafka-producer-1.0-SNAPSHOTar-with-dependencies.jar 1_


##How to run Esper and consumer (cep-esper-consumer project)
* (should be run from machines huzvak02, huzvak03 and huzvak04) 
* currently cannot be run from other machines in MU network, because ZK timeout exception is set to 400 ms which is too low
* when connected to the machine, move to folder _projekt_ in home folder. You can use following command:
 * _cd ./projekt_
* to start consumer, use the following command, where arguments are following: 
 * 1st argument is the znode from which configuration should be read
 * 2nd is the consumer group id
 * 3rd is the string with IP addresses and ports to ZooKeeper nodes
* example of the command is:
 * _java -jar CEP-esper-consumer-0.0.1-SNAPSHOT-jar-with-dependencies.jar /esper/node1 1 147.251.43.240:2181,147.251.43.218:2181,147.251.43.236:2181,147.251.43.219:2181_
