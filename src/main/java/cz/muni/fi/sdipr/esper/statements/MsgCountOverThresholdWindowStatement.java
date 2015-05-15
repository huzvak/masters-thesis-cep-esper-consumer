package cz.muni.fi.sdipr.esper.statements;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.UpdateListener;

public class MsgCountOverThresholdWindowStatement {

	private EPStatement statement;

	public MsgCountOverThresholdWindowStatement(EPAdministrator admin) {

		// Query with use of Batch window
		String stmt = "select msg, count(*) as cnt from IncommingEvent.win:time_batch(20 sec) "
				+ "group by msg having count(*) >= 5";

		// Query with use of Sliding window
		// String stmt =
		// "select msg, count(*) as cnt from IncommingEvent.win:time(1 sec) "
		// + "group by msg "
		// + "having count(*) >= 60000";

		statement = admin.createEPL(stmt);
	}

	public void addListener(UpdateListener listener) {
		statement.addListener(listener);
	}
}