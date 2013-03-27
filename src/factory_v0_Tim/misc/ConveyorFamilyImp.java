package factory_v0_Tim.misc;

import java.util.*;
import factory_v0_Tim.agents.*;
import shared.Glass;
import shared.interfaces.ConveyorFamily;

public class ConveyorFamilyImp {
	//Name: ConveyorFamilyImp

	//Description:  Will act as a wrapper class for a set of conveyors, sensors, and pop-ups.  It will also contain a reference to robots and machines through its components

	//Data:
	ConveyorFamily nextCF; // reference to the next ConveyorFamily – this could even be the final truck at the end of the line
	ConveyorFamily prevCF; // reference to the previous conveyor family, will be NULL if it does not exist
	ConveyorAgent conveyor;
	List<SensorAgent> sensors; // Will hold all of the sensors of different types in one place – adds to the modularity of the system
	PopUpAgent popUp; 

	//Messages:
	public void msgHereIsGlass(Glass g) {
		if ($ s in sensors s.t. s.type == “entry”) then
			s.msgHereIsGlass(g);
	}

	public void msgPositionFree() {
		conveyor.msgPositionFree();
	}

	public void msgDoneProcessingGlass(Glass g, int machineIndex) {
		popUp.msgDoneProcessingGlass(g);
	}

}
