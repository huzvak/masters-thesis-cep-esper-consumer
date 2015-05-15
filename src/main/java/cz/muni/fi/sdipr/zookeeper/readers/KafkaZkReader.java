package cz.muni.fi.sdipr.zookeeper.readers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

import org.json.JSONObject;

public class KafkaZkReader implements Watcher,
		SimpleDataMonitor.DataMonitorListener {

	private static final Log log = LogFactory.getLog(KafkaZkReader.class);
	private String kafkaHost;
	private String kafkaPort;

	private String znode;
	private SimpleDataMonitor dm;
	private ZooKeeper zk;

	public KafkaZkReader(String zkServers, String znode) throws KeeperException,
			IOException {
		zk = new ZooKeeper(zkServers, 3000, this);
		dm = new SimpleDataMonitor(zk, znode, null, this);
		this.znode = znode;
	}

	/*
	 * check if new data from ZK are not empty and if not process them, shutdown
	 * old consumers and create new ones
	 * 
	 * @see
	 * cz.muni.fi.sdipr.zookeeper.readers.SimpleDataMonitor.DataMonitorListener
	 * #exists(byte[])
	 */
	public void exists(byte[] data) {

		if (data != null) {
			String info = new String(data);
			log.debug("Output data from KafkaZkReader Executor: " + info);
			parseHostAndPort(info);
		} else {
			log.debug("Data are null");
		}
	}

	private void parseHostAndPort(String data) {
		JSONObject obj = new JSONObject(data);

		this.kafkaHost = obj.getString("host");
		this.kafkaPort = obj.getInt("port") + "";
	}

	public String getKafkaHost() {
		return kafkaHost;
	}

	public String getKafkaPort() {
		return kafkaPort;
	}

	public void process(WatchedEvent event) {
		dm.process(event);
	}

	public void closing(int rc) {
		synchronized (this) {
			notifyAll();
			log.debug("Node: " + znode + " was deleted");
		}
	}
}