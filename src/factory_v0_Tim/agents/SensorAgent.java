package factory_v0_Tim.agents;

import java.util.*;

import shared.Glass;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Agent;

public class SensorAgent extends Agent {

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
	ConveyorFamily cf; // Reference to the current conveyor family

	//Messages:
	public void msgHereIsGlass(Glass glass) {
		glassSheets.add(new MyGlass(glass, location.entry, onSensor.justEntered));
		stateChanged();
	}

	// The following messages will be special to transducer events, and will be called after parsing arguments in the EventFired(args[]) function.

	public void msgGlassOffSensor(Glass glass) {
		if ($ g in glassSheets s.t. g.glass.id == glass.id) then
			g.onSensor = onSensor.no;
			stateChanged();
	}

	public void msgHereIsGlassTransducer(Glass glass) {
		glassSheets.add(new MyGlass(glass, getLocationFromSensor(), onSensor.justEntered));
		stateChanged();
	}
	
	//Scheduler:
	@Override
	public boolean pickAndExecuteAnAction() {		
		if ($ g in glassSheets) then
			if (g.onSensor == onSensor.justEntered) then
				actPassGlassToConveyor(g); return true;
			if (g.onSensor == onSensor.no) then
				actRemoveGlass(g); return true;

		return false;	
	}


	//Actions:
	private void actPassGlassToConveyor(MyGlass g) {
		if (g.location == location.entry) then
			cf.conveyor.msgGiveGlassToConveyor(g.glass);
		if (g.location == location.popup) then
			cf.conveyor.msgGiveGlassToPopUp(g.glass);	
		if (g.location == location.exit) then
			cf.conveyor.msgPassOffGlass(g.glass);	
	}

	private void actRemoveGlass(MyGlass g) {
		glassSheets.remove(g);
		if (g.location == location.entry && $ cf.prevCF) then // Tell the previous conveyor family that the sensor currently has nothing on it
			cf.prevCF.msgPositionFree();
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
}
