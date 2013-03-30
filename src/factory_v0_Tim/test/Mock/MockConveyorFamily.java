package factory_v0_Tim.test.Mock;

import java.util.List;

import shared.Glass;

public class MockConveyorFamily {
	//Data:
	public List<Glass> glassSheets; // Will hold the glass to be received from the previous ConveyorFamily
	private String name;
	
	//Constructors:
	public MockConveyorFamily(String name) {
		this.name = name;

	}

	//Messages:
	public void msgHereIsGlass(Glass g) {
		glassSheets.add(g);
	}
}
