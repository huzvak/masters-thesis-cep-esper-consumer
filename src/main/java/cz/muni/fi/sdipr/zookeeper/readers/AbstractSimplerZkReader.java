package cz.muni.fi.sdipr.zookeeper.readers;

import cz.muni.fi.sdipr.kafka.producer.KafkaCharactersProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSimplerZkReader implements Watcher, Runnable,
		SimpleDataMonitor.DataMonitorListener {

	/**
	 * Created by huzvak on 1.2.2015.
	 */
	private static final Log log = LogFactory.getLog(SimpleZkReader.class);

	protected String znode;
	protected SimpleDataMonitor dm;
	protected ZooKeeper zk;
	protected String consumerGroupId;
	protected String zkServers;

	/*
	 * Create new Esper instance.
	 * @param engineUri Name of the engine
	 * @param continuosSimulation Boolean value for continuous simulation (currently not used)
	 */
	public void createEsperInstance(String engineUri,
			boolean continuousSimulation) {
	}

	/*
	 * Return map with the all producers
	 * @return The map with all the producers
	 */
	public Map<String, KafkaCharactersProducer> getProducers() {
		return new ConcurrentHashMap<String, KafkaCharactersProducer>();
	}

	/**
	 * ************************************************************************
	 * We do process any events ourselves, we just need to forward them on.
	 *
	 * @see org.apache.zookeeper.Watcher#process(org.apache.zookeeper.proto.WatcherEvent)
	 */
	public void process(WatchedEvent event) {
		dm.process(event);
	}

	public void run() {
		try {
			synchronized (this) {
				while (!dm.dead) {
					wait();
				}
				
				log.debug("Node: " + znode + " was deleted 2");
			}
		} catch (InterruptedException e) {
		}
	}

	public void closing(int rc) {
		synchronized (this) {
			notifyAll();
			log.debug("Node: " + znode + " was deleted");
		}
	}

	public abstract void exists(byte[] data);
}
