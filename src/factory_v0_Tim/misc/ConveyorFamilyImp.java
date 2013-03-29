package factory_v0_Tim.misc;

import java.util.*;

import factory_v0_Tim.agents.*;
import factory_v0_Tim.interfaces.*;
import shared.Glass;
import shared.interfaces.*;

public class ConveyorFamilyImp implements ConveyorFamily {
	//Name: ConveyorFamilyImp

	//Description:  Will act as a wrapper class for a set of conveyors, sensors, and pop-ups.  It will also contain a reference to robots and machines through its components

	//Data:
	private ConveyorFamily nextCF; // reference to the next ConveyorFamily – this could even be the final truck at the end of the line
	private ConveyorFamily prevCF; // reference to the previous conveyor family, will be NULL if it does not exist
	private Conveyor conveyor;
	private List<Sensor> sensors; // Will hold all of the sensors of different types in one place – adds to the modularity of the system
	private PopUp popUp;
	private String name;
	
	//Constructors:
	public ConveyorFamilyImp(String name, ConveyorFamilyImp prevCF, ConveyorFamilyImp nextCF, Conveyor conveyor, List<Sensor> sensors, PopUp popUp) {
		this.name = name;
		this.prevCF = prevCF;
		this.nextCF = nextCF;
		this.conveyor = (ConveyorAgent) conveyor;
		this.sensors = sensors;
		this.popUp = popUp;
	}

	//Messages:
	public void msgHereIsGlass(Glass g) {
		for (Sensor s: sensors) {
			if (s.getType().contains("entry")) {
				s.msgHereIsGlass(g);
				System.out.println(name + ": Found the entry sensor and sent the message with the glass!");
				break;
			}
		}
	}

	public void msgPositionFree() {
		conveyor.msgPositionFree();
		System.out.println(name + ": Messaged conveyor that glass can to passed to next conveyor system.");
	}

	public void msgGlassDone(Glass g, int machineIndex) {
		getPopUp().msgDoneProcessingGlass(g);
		System.out.println(name + ": Messaged pop up with processed glass.");
	}
	
	//Other Methods:

	public Conveyor getConveyor() {
		return conveyor;
	}

	public void setConveyor(Conveyor conveyor) {
		this.conveyor = conveyor;
	}

	public ConveyorFamily getPrevCF() {
		return prevCF;
	}

	public void setPrevCF(ConveyorFamily prevCF) {
		this.prevCF = (ConveyorFamilyImp) prevCF;
	}

	public PopUp getPopUp() {
		return popUp;
	}

	public void setPopUp(PopUp popUp) {
		this.popUp = popUp;
	}

	public ConveyorFamily getNextCF() {
		return nextCF;
	}

	public void setNextCF(ConveyorFamily nextCF) {
		this.nextCF = nextCF;
	}

	public String getName() {
		return name;
	}
	
	public Sensor getSensor(String arg) {
		for (Sensor s: sensors) {
			if (s.getType().contains(arg)) {
				return s;
			}
		}
		return null;
	}
}
