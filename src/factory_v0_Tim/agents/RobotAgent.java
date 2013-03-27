package factory_v0_Tim.agents;

import java.util.*;

import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Agent;

public class RobotAgent extends Agent {

	//Name: RobotAgent

	//Description:  Will transfer a piece of glass from the popUpAgent to an associated machine to complete a process.

	// Data:
	enum processState {unprocessed, doneProcessing}; 

	private class MyGlass {
		Glass glass;
		processState processState;
		public MyGlass(Glass glass, processState processState) {
			this.glass = glass;
			this.processState = processState;
		}
	}

	private class MachineCom { // Will hold a communication channel to a machine, allowing for the possibility to communicate to multiple machines at once
		MachineAgent machine; // Machine reference
		boolean inUse; // Is this channel currently occupied by a piece of glass
		MachineType processType; // What process does this machine do?  Does the glass need to undergo this process?
		MyGlass glassBeingProcessed; // This reference needs to be held so RobotAgents know which piece of glass is being processed by the Machine.  This name will be abbreviated to glassBeingProcessed.
	}

	List<MyGlass> glassToBeProcessed; // This name will be abbreviated as glassToBeProcessed in many functions to save on space and complexity
	List<MachineCom> machineComs; 
	ConveyorFamily cf;

	//Messages:
	public void msgProcessGlass(Glass g) { // Get Glass from popUp to robot
		glassToBeProcessed.add(new MyGlass(g, processState.unprocessed));
		stateChanged();
	}

	public void msgDoneProcessingGlass(Glass g) {
		glassToBeProcessed.add(new MyGlass(g, processState.doneProcessing));
		if ($ com in machineComs s.t. com.glassBeingProcessed.glass.id == g.id) then
			com.inUse = false;
			com.glassBeingProcessed = null;
		else // There is a bug – this should never happen
		stateChanged();
	}

	//Scheduler:
	public boolean pickAndExecuteAnAction() {
		if ($ g in glassToBeProcessed s.t. g.processState == processState.unprocessed) then
			if ($ com in machineComs s.t. com.inUse == false) then
				actPassGlassToMachine (g, com); return true;
		if ($ g in glassToBeProcessed s.t. g.processState == processState.doneProcessing) then
			actPassGlassToCF (g); return true;
	
		return false;
	}

	//Actions:
	private void actPassGlassToMachine(MyGlass g, MachineCom com) {
		com.machine.msgProcessGlass(g.glass);
		com. glassBeingProcessed = g;
		com.inUse = true;
		glassToBeProcessed.remove(g);
	}

	private void actPassGlassToCF(MyGlass g) {
		cf.conveyor.msgDoneProcessingGlass(g.glass);
		glassToBeProcessed.remove(g);
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
}
