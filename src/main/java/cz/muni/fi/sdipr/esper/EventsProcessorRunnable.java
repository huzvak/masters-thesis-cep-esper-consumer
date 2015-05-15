package cz.muni.fi.sdipr.esper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;

import cz.muni.fi.sdipr.esper.eventbean.IncommingEvent;
import cz.muni.fi.sdipr.esper.listeners.MsgCountOverThresholdWindowListener;
import cz.muni.fi.sdipr.esper.statements.MsgCountOverThresholdWindowStatement;
import cz.muni.fi.sdipr.zookeeper.readers.SimpleZkReader;

public class EventsProcessorRunnable implements Runnable {

	private static final Log log = LogFactory.getLog(EventsProcessorRunnable.class);

	private EPServiceProvider epService;
	private String engineURI;
	private final SimpleZkReader zooKeeper;

	public EventsProcessorRunnable(String engineURI, boolean continuousSimulation, SimpleZkReader zooKeeper) {
		log.info("New EventsProcessorRunnable created");
		this.engineURI = engineURI;
		this.zooKeeper = zooKeeper;
	}
	
	public void run() {
		Configuration configuration = new Configuration();
		configuration.addEventType("IncommingEvent",
				IncommingEvent.class.getName());

		log.info("Setting up EPL");
		epService = EPServiceProviderManager.getProvider(
				engineURI, configuration);

		//registering of the statement
		MsgCountOverThresholdWindowStatement simpleStm = new MsgCountOverThresholdWindowStatement(
				epService.getEPAdministrator());
		//adding listener to the statement
		simpleStm.addListener(new MsgCountOverThresholdWindowListener(zooKeeper));

		log.info("Done.");
	}

	public EPServiceProvider getEpService() {
		return epService;
	}
}
