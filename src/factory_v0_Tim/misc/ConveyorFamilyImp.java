package factory_v0_Tim.misc;

import java.util.*;
import factory_v0_Tim.agents.*;
import shared.Glass;
import shared.interfaces.*;

public class ConveyorFamilyImp {
	//Name: ConveyorFamilyImp

	//Description:  Will act as a wrapper class for a set of conveyors, sensors, and pop-ups.  It will also contain a reference to robots and machines through its components

	//Data:
	private ConveyorFamilyImp nextCF; // reference to the next ConveyorFamily – this could even be the final truck at the end of the line
	private ConveyorFamilyImp prevCF; // reference to the previous conveyor family, will be NULL if it does not exist
	private ConveyorAgent conveyor;
	List<SensorAgent> sensors; // Will hold all of the sensors of different types in one place – adds to the modularity of the system
	private PopUpAgent popUp; 

	//Messages:
	public void msgHereIsGlass(Glass g) {
		for (SensorAgent s: sensors) {
			if (s.getType().contains("entry")) {
				s.msgHereIsGlass(g);
				break;
			}
		}
	}

	public void msgPositionFree() {
		conveyor.msgPositionFree();
	}

	public void msgDoneProcessingGlass(Glass g, int machineIndex) {
		getPopUp().msgDoneProcessingGlass(g);
	}

	public ConveyorAgent getConveyor() {
		return conveyor;
	}

	public void setConveyor(Conveyor conveyor) {
		this.conveyor = (ConveyorAgent) conveyor;
	}

	public ConveyorFamilyImp getPrevCF() {
		return prevCF;
	}

	public void setPrevCF(ConveyorFamily prevCF) {
		this.prevCF = (ConveyorFamilyImp) prevCF;
	}

	public PopUpAgent getPopUp() {
		return popUp;
	}

	public void setPopUp(PopUp popUp) {
		this.popUp = (PopUpAgent) popUp;
	}

	public ConveyorFamilyImp getNextCF() {
		return nextCF;
	}

	public void setNextCF(ConveyorFamilyImp nextCF) {
		this.nextCF = nextCF;
	}

}
