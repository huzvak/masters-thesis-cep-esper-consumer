package cz.muni.fi.sdipr.kafka.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.muni.fi.sdipr.esper.EventsProcessorRunnable;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaCharactersConsumer {
	
	private static final Log log = LogFactory.getLog(KafkaCharactersConsumer.class);

	private static final String ZK_SESSION_TIMEOUT = "400"; //ms
	private static final String ZK_SYNC_TIME = "200";  //ms
	private static final String ZK_AUTO_COMMIT_INTERVAL = "1000";  //ms

	private final ConsumerConnector consumer;
	private final String topic;
	private ExecutorService executor;
	
	private final EventsProcessorRunnable esperProcessing;

	/*
	 * a_groupdId should be unique for every node, a_topic means from which topic from Kafka should consumer read the data
	 */
	public KafkaCharactersConsumer(String a_zookeeper, String a_groupId, String a_topic, EventsProcessorRunnable esper) {
		consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
				createConsumerConfig(a_zookeeper, a_groupId));
		this.topic = a_topic;
		this.esperProcessing = esper;
	}

	public void shutdown() {
		if (consumer != null)
			consumer.shutdown();
		if (executor != null)
			executor.shutdown();
	}

	/*
	 * Messages from the Kafka topic are consumed by a fixed number of threads and send 
	 * to ConsumerWorker instance for further processing
	 */
	public void run(int a_numThreads) {
		Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
		topicCountMap.put(topic, new Integer(a_numThreads));
		Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);

		List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);

		// now launch all the threads
		executor = Executors.newFixedThreadPool(a_numThreads);

		// now create an object to consume the messages
		int threadNumber = 0;
		for (final KafkaStream<byte[], byte[]> stream : streams) {
			executor.submit(new ConsumerWorker(stream, threadNumber, esperProcessing));
			threadNumber++;
		}
	}

	private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
		Properties props = new Properties();
		props.put("zookeeper.connect", a_zookeeper);
		props.put("group.id", a_groupId);
		props.put("zookeeper.session.timeout.ms", ZK_SESSION_TIMEOUT);
		props.put("zookeeper.sync.time.ms", ZK_SYNC_TIME);
		props.put("auto.commit.interval.ms", ZK_AUTO_COMMIT_INTERVAL);
		
		log.debug("ConsumerConfig for group id: " + a_groupId + " will be created");

		return new ConsumerConfig(props);
	}
}