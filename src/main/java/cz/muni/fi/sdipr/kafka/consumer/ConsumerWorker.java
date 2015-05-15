package cz.muni.fi.sdipr.kafka.consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cz.muni.fi.sdipr.esper.EventsProcessorRunnable;
import cz.muni.fi.sdipr.esper.eventbean.CharacterEnum;
import cz.muni.fi.sdipr.esper.eventbean.IncommingEvent;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class ConsumerWorker implements Runnable {

	private static final Log log = LogFactory.getLog(ConsumerWorker.class);

	private KafkaStream<byte[], byte[]> m_stream;
	private int m_threadNumber;
	private EventsProcessorRunnable esperProcessor;

	public ConsumerWorker(KafkaStream<byte[], byte[]> stream, int threadNumber,
			EventsProcessorRunnable esperProcessor) {
		m_threadNumber = threadNumber;
		m_stream = stream;
		this.esperProcessor = esperProcessor;
	}

	/*
	 * The method receives messages from Kafka, logs them and sends further to the Esper engine
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
		while (it.hasNext()) {
			String msg = new String(it.next().message());
			log.debug("Thread " + m_threadNumber + ": " + msg);

			esperProcessor.getEpService().getEPRuntime()
					.sendEvent(new IncommingEvent(getCharacterEnum(msg)));
			log.debug("Msg sent to esper");
		}
	}

	private CharacterEnum getCharacterEnum(String msg) {
		if (msg.equals("A")) {
			return CharacterEnum.A;
		} else if (msg.equals("B")) {
			return CharacterEnum.B;
		} else if (msg.equals("C")) {
			return CharacterEnum.C;
		} else if (msg.equals("D")) {
			return CharacterEnum.D;
		} else if (msg.equals("A1")) {
			return CharacterEnum.A1;
		} else if (msg.equals("B1")) {
			return CharacterEnum.B1;
		} else if (msg.equals("C1")) {
			return CharacterEnum.C1;
		} else if (msg.equals("D1")) {
			return CharacterEnum.D1;
		} else if (msg.equals("A2")) {
			return CharacterEnum.A2;
		} else if (msg.equals("B2")) {
			return CharacterEnum.B2;
		} else if (msg.equals("C2")) {
			return CharacterEnum.C2;
		} else if (msg.equals("D2")) {
			return CharacterEnum.D2;
		} else if (msg.equals("A3")) {
			return CharacterEnum.A3;
		} else if (msg.equals("B3")) {
			return CharacterEnum.B3;
		} else if (msg.equals("C3")) {
			return CharacterEnum.C3;
		} else {
			return CharacterEnum.D3;
		}
	}
}