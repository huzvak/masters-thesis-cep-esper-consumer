package cz.muni.fi.sdipr.esper.eventbean;

public class IncommingEvent {
	private CharacterEnum msg;

    public IncommingEvent(CharacterEnum msg) {
        this.msg = msg;
    }

    public String toString() {
    	return "Msg:" + msg.getCharString();
    }
    
    public CharacterEnum getMsg() {
    	return msg;
    }
}
