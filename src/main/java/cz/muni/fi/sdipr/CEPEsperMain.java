package cz.muni.fi.sdipr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.muni.fi.sdipr.zookeeper.readers.AbstractSimplerZkReader;
import cz.muni.fi.sdipr.zookeeper.readers.KafkaZkReader;
import cz.muni.fi.sdipr.zookeeper.readers.SimpleZkReader;

/**
 * Created by huzvak on 18.2.2015.
 */
/*
 * In this class:
 * Initialization of KafkaConfReader that reads config information about Kafka is done. 
 * Initialization of SimpleZkReader that reads config information from given znode id sone.
 * Initialization of Esper instance is done.
 */
public class CEPEsperMain {

	private static final Log log = LogFactory.getLog(CEPEsperMain.class);

	private static final String ENGINE_URI = "EventsProcessor";
	private static final boolean CONTINUOS_SIMULATION = false;
	
	private static final String ZNODE_KAFKA = "/brokers/ids/0";

	private AbstractSimplerZkReader consumerProducerReader;
	private KafkaZkReader kafkaConfReader;

	public static void main(String[] args) {
		if(args.length < 3) {
			System.out.println("3 arguments are required: znode from which data for Esper should be read, "
					+ "consumer group id and zooKeeper's host:port (format: 'host:port,host:port,...')");
			System.exit(0);
		}
		
		String znodeEsper = args[0];
		String consumerGroupId = args[1];
		String zooKeeper = args[2];
		
		CEPEsperMain cepInstance = new CEPEsperMain();

		try {
			cepInstance.kafkaConfReader = new KafkaZkReader(zooKeeper, ZNODE_KAFKA);
			log.debug("Kafka reader created");
			
			Thread.sleep(5000);
			
			String kafkaHost = cepInstance.kafkaConfReader.getKafkaHost();
			String kafkaPort = cepInstance.kafkaConfReader.getKafkaPort();
			
			log.info("KafkaHost: " + kafkaHost);
			
			cepInstance.consumerProducerReader = 
					new SimpleZkReader(zooKeeper, znodeEsper, consumerGroupId, kafkaHost, kafkaPort);
			cepInstance.consumerProducerReader.createEsperInstance(ENGINE_URI, CONTINUOS_SIMULATION);
			
			Thread t3 = new Thread(cepInstance.consumerProducerReader);
			t3.start();
			log.debug("Consumer producer created");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
