package cz.muni.fi.sdipr.zookeeper.readers;

import com.google.common.collect.Sets;

import cz.muni.fi.sdipr.esper.EventsProcessorRunnable;
import cz.muni.fi.sdipr.kafka.consumer.KafkaCharactersConsumer;
import cz.muni.fi.sdipr.kafka.producer.KafkaCharactersProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by huzvak on 1.2.2015.
 */
public class SimpleZkReader extends AbstractSimplerZkReader {
	
	private static final Log log = LogFactory.getLog(SimpleZkReader.class);

    private EventsProcessorRunnable esperProcessing;

    private final Map<String, KafkaCharactersConsumer> consumers = new HashMap<String, KafkaCharactersConsumer>();
    //I am using ConcurrentHashMap because Esper engine will iterate over the entries 
    //and at the same time whole map can be changed here in this class
    private final Map<String, KafkaCharactersProducer> producers = new ConcurrentHashMap<String, KafkaCharactersProducer>();
    private final Set<String> previousConsumers = new HashSet<String>();
    private final Set<String> previousProducers = new HashSet<String>();
    
    private final String kafkaHost;
    private final String kafkaPort;

    public SimpleZkReader(String zkServers, String znode, String consumerGroupId, String kafkaHost, String kafkaPort) 
    		throws KeeperException, IOException {
    	this.zkServers = zkServers;
        zk = new ZooKeeper(zkServers, 3000, this);
        dm = new SimpleDataMonitor(zk, znode, null, this);
        this.znode = znode;
        this.consumerGroupId = consumerGroupId;
        this.kafkaHost = kafkaHost;
        this.kafkaPort = kafkaPort;
        log.info("SimpleZkReader constructor: Host: " + kafkaHost + ", port: " + kafkaPort);
    }
    
    /*
     * @see cz.muni.fi.sdipr.zookeeper.readers.AbstractSimplerZkReader#createEsperInstance(java.lang.String, boolean)
     */
    public void createEsperInstance(String engineUri, boolean continuousSimulation) {
    	esperProcessing = new EventsProcessorRunnable(engineUri, continuousSimulation, this);
    	esperProcessing.run();
    }
    
    public Map<String, KafkaCharactersProducer> getProducers() {
    	return producers;
    }

    /*
     * check if new data from ZK are not empty and if not process them, shutdown old, not anymore relevant 
     * consumers and producers and create new ones
     * 
     * @see cz.muni.fi.sdipr.zookeeper.readers.SimpleDataMonitor.DataMonitorListener#exists(byte[])
     */
    public void exists(byte[] data) {

        if (data != null) {
            String info = new String(data);
            log.debug("Output data from Executor: " + info);

            String[] zkInfo = info.split(";");
            String[] readFrom = zkInfo[0].split(",");
            String[] writeTo = zkInfo[1].split(",");

            log.debug("Consumers: " + zkInfo[0] + ", No. of consumers: " + readFrom.length + ", consumer 1: " + readFrom[0]);
            log.debug("Producers: " + zkInfo[1] + ", No. of producers: " + writeTo.length + ", producer 1: " + writeTo[0]);
            
            /*
             * CONSUMERS - BEGIN
             */
            int tempNumberOfConsumers = consumers.size();

            Set<String> currentConsumers = new HashSet<String>(Arrays.asList(readFrom));
            
            killOldConsumers(currentConsumers);
            createNewConsumers(currentConsumers);

            previousConsumers.clear();
            previousConsumers.addAll(currentConsumers);

            int newlyCreatedConsumers = consumers.size() - tempNumberOfConsumers;
            if(newlyCreatedConsumers < 0) {
            	newlyCreatedConsumers = 0;
            }
            log.info("Newly created consumers: " + newlyCreatedConsumers + ", totally running consumers: " + consumers.size());
            /*
             * CONSUMERS - END
             */
            
            /*
             * PRODUCERS - BEGIN
             */
            int tempNumberOfProducers = producers.size();

            Set<String> currentProducers = new HashSet<String>(Arrays.asList(writeTo));
            
            killOldProducers(currentProducers);
            createNewProducers(currentProducers, kafkaHost, kafkaPort);

            previousProducers.clear();
            previousProducers.addAll(currentProducers);

            int newlyCreatedProducers = producers.size() - tempNumberOfProducers;
            if(newlyCreatedProducers < 0) {
            	newlyCreatedProducers = 0;
            }
            log.info("Newly created producers: " + newlyCreatedProducers + ", totally running producers: " + producers.size());
            /*
             * PRODUCERS - END
             */
        }
        else {
        	log.debug("Data are null");
        	killOldConsumers(new HashSet<String>());
        	killOldProducers(new HashSet<String>());
        	previousConsumers.clear();
        	previousProducers.clear();
        }
    }
    
    private void killOldProducers(Set<String> currentProducers) {
        Sets.SetView<String> diff =  Sets.difference(previousProducers, currentProducers);
        log.debug("Diff to kill: " + diff);

        /*
         * If there is some difference, those in diff variable are not relevant anymore so they I can shutdown them
         */
        if(diff.size() > 0) {
            for(String topic : diff) {
                KafkaCharactersProducer producer = producers.get(topic);
                log.debug("I am going to shutdown producer " + producer + " for topic: " + topic);
                producer.close();
                producers.remove(topic);
            }
        }
    }
    
    private void createNewProducers(Set<String> currentProducers, String kafkaHost, String kafkaPort) {
        Sets.SetView<String> diff = Sets.difference(currentProducers, previousProducers);
        log.debug("Diff to create: " + diff);

        /*
         * If there is some difference, those in diff variable are new and therefore create them
         */
        if(diff.size() > 0) {
            for(String topic : diff) {
                KafkaCharactersProducer producer = new KafkaCharactersProducer(topic, kafkaHost, kafkaPort);
                producers.put(topic, producer);

                log.info("New producer for topic " + topic + " created");
            }
        }
    }
    
    private void killOldConsumers(Set<String> currentConsumers) {
        Sets.SetView<String> diff =  Sets.difference(previousConsumers, currentConsumers);
        log.debug("Diff to kill: " + diff);

        /*
         * If there is some difference, those in diff variable are not relevant anymore so they I can shutdown them
         */
        if(diff.size() > 0) {
            for(String topic : diff) {
                KafkaCharactersConsumer consumer = consumers.get(topic);
                log.debug("I am going to shutdown consumer " + consumer + " for topic: " + topic);
                consumer.shutdown();
                consumers.remove(topic);
            }
        }
    }
    
    private void createNewConsumers(Set<String> currentConsumers) {
    	Sets.difference(previousConsumers, currentConsumers);
        Sets.SetView<String> diff = Sets.difference(currentConsumers, previousConsumers);
        log.debug("Diff to create: " + diff);

        /*
         * If there is some difference, those in diff variable are new and therefore create them
         */
        if(diff.size() > 0) {
            for(String topic : diff) {
                KafkaCharactersConsumer consumer = new KafkaCharactersConsumer(zkServers, consumerGroupId, topic, esperProcessing);
                consumers.put(topic, consumer);
                consumer.run(1);

                log.info("New consumer for topic: " + topic + " is running.");
            }
        }
    }
}