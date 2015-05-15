package cz.muni.fi.sdipr.esper.listeners;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import cz.muni.fi.sdipr.kafka.producer.KafkaCharactersProducer;
import cz.muni.fi.sdipr.zookeeper.readers.SimpleZkReader;

public class MsgCountOverThresholdWindowListener implements UpdateListener {
	private static final Log log = LogFactory.getLog(MsgCountOverThresholdWindowListener.class);
	
	private final SimpleZkReader zooKeeper;
	
	public MsgCountOverThresholdWindowListener(SimpleZkReader zooKeeper) {
		this.zooKeeper = zooKeeper;
	}
	
    public void update(EventBean[] newEvents, EventBean[] oldEvents)
    {
    	for (int i = 0; i < newEvents.length; i++) {
			logEvent(newEvents[i]);
			sendToKafka(newEvents[i]);
		}
    }
    
    private void logEvent(EventBean theEvent) {
    	log.info("Event " + theEvent.get("msg").toString() +
                " was recognized as suspicious because it's occurence in last 20 seconds was " + theEvent.get("cnt") + 
                " times");
    }
    
    /*
     * Send message to the Kafka to the topics that are in ZK config for this node.
     */
    private void sendToKafka(EventBean theEvent) {
    	Map<String, KafkaCharactersProducer> producers = zooKeeper.getProducers();
    	
    	for(KafkaCharactersProducer producer : producers.values()) {
    		String msg = theEvent.get("msg") + "M";
    		
    		producer.sendMessage(msg);
    		
    		log.debug("A bit modified msg: " + msg + " was sent to Kafka topic: " + producer.getTopic());
    	}
    }
}
