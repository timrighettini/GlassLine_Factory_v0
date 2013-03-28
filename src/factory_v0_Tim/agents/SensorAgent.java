package factory_v0_Tim.agents;

import java.util.*;

import shared.Glass;
import shared.interfaces.Sensor;
import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Agent;
import factory_v0_Tim.misc.ConveyorFamilyImp;

public class SensorAgent extends Agent implements Sensor {

	//Name: SensorAgent

	//Description:  Will detect if a piece of glass has entered, exited, or on a popup for any given set of conveyors.  Even though all of the sensor functionality currently melded into one agent, I may split this agent up into a base agent and three inheritance agents – EntrySensorAgent, ExitSensorAgent, and PopUpSensorAgent – during implementation

	//Data:	
	public enum onSensor {justEntered, yes, no}; // Is the glass on an given sensor?
	public enum location {entry, popup, exit}; // Which sensor the glass is currently on – this will not be needed if using the multiple inheritance design paradigm
	
	private class MyGlass {
		Glass glass; // Holds a reference to the glass
		onSensor onSensor; 
		location location; 
		
		public MyGlass(Glass glass, location location, onSensor onSensor) {
			this.glass = glass;
			this.location = location;
			this.onSensor = onSensor;
		}		
	}	

	List<String> type; // Will hold the type of sensor this is, and it may be of more than one type
	List<MyGlass> glassSheets; // Will hold all glass references
	ConveyorFamilyImp cf; // Reference to the current conveyor family

	//Messages:
	public void msgHereIsGlass(Glass glass) {
		glassSheets.add(new MyGlass(glass, location.entry, onSensor.justEntered));
		stateChanged();
	}

	// The following messages will be special to transducer events, and will be called after parsing arguments in the EventFired(args[]) function.

	public void msgGlassOffSensor(Glass glass) {
		for (MyGlass g: glassSheets) {
			if (g.glass.getId() == glass.getId()) {
				g.onSensor = onSensor.no;
				stateChanged();		
			}
		}
	}

	public void msgHereIsGlassTransducer(Glass glass, String strLocation) {
		location l = location.entry;
		if (strLocation.equals("popUp")) {
			l = location.popup;
		}
		else if (strLocation.equals("exit")) {
			l = location.exit;
		}
		glassSheets.add(new MyGlass(glass, l, onSensor.justEntered));
		stateChanged();
	}
	
	//Scheduler:
	@Override
	public boolean pickAndExecuteAnAction() {		
		for (MyGlass g: glassSheets) {
			if (g.onSensor == onSensor.justEntered) { // If a piece of glass JUST ENTERED the sensor, then pass it to the conveyor, depending on the sensor
				actPassGlassToConveyor(g); return true;
			}
			if (g.onSensor == onSensor.no) { // If the glass just left the sensor, then remove it
				actRemoveGlass(g); return true;
			}
		}		
		return false;	
	}


	//Actions:
	private void actPassGlassToConveyor(MyGlass g) {
		if (g.location == location.entry) 
			cf.getConveyor().msgGiveGlassToConveyor(g.glass);
		else if (g.location == location.popup) 
			cf.getConveyor().msgGiveGlassToPopUp(g.glass);	
		else if (g.location == location.exit) 
			cf.getConveyor().msgPassOffGlass(g.glass);	
	}

	private void actRemoveGlass(MyGlass g) {
		glassSheets.remove(g);
		if (g.location == location.entry && cf.getPrevCF() != null) { // Tell the previous conveyor family that the sensor currently has nothing on it
			cf.getPrevCF().msgPositionFree();
		}
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
	public List<String> getType() {
		return type;
	}
}
