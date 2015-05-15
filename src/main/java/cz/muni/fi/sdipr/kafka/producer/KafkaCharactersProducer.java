package cz.muni.fi.sdipr.kafka.producer;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaCharactersProducer {

	private static final Logger log = LoggerFactory
			.getLogger(KafkaCharactersProducer.class);

	private final String topic;
	private final Producer<String, String> producer;

	public KafkaCharactersProducer(String topic, String kafkaHost, String kafkaPort) {
		this.topic = topic;
		
		Properties props = new Properties();
		props.put("metadata.broker.list",  kafkaHost + ":" + kafkaPort);
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("request.required.acks", "1");
		
		producer = new Producer<String, String>(new ProducerConfig(props));
		
		log.info("New producer for topic: " + topic + " was created");
	}

	/*
	 * Send message msg to the Kafka Message Broker 
	 * 
	 * @param msg Message that should be sent to Kafka
	 */
	public void sendMessage(String msg) {
		KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, msg);
		producer.send(data);
	}

	public void close() {
		if (producer != null) {
			producer.close();
		}
		
		log.info("Producer for topic: " + topic + " was closed.");
	}
	
	public String getTopic() {
		return topic;
	}
}