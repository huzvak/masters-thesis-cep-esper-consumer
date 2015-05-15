package cz.muni.fi.sdipr.esper.eventbean;

public enum CharacterEnum
{
    A("A"),
    B("B"),
    C("C"),
    D("D"),
    A1("A1"),
    B1("B1"),
    C1("C1"),
    D1("D1"),
    A2("A2"),
    B2("B2"),
    C2("C2"),
    D2("D2"),
    A3("A3"),
    B3("B3"),
    C3("C3"),
    D3("D3");
    
    private String character;
     
    private CharacterEnum(String character) {
    	this.character = character;
    }
    
    public String getCharString() {
    	return character;
    }
}