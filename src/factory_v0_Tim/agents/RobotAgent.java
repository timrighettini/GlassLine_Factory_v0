package factory_v0_Tim.agents;

import java.util.*;

import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Agent;
import factory_v0_Tim.interfaces.Machine;
import factory_v0_Tim.interfaces.Robot;
import factory_v0_Tim.misc.ConveyorFamilyImp;

public class RobotAgent extends Agent implements Robot {

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
		
		public MachineCom(Machine machine) {
			this.machine = (MachineAgent) machine;
			this.inUse = false; // At start, this channel is obviously not being used, so it has to be false
			this.processType = machine.getProcessType();
			this.glassBeingProcessed = null; // Currently, there is no glass being processed within this channel
		}
	}

	List<MyGlass> glassToBeProcessed; // This name will be abbreviated as glassToBeProcessed in many functions to save on space and complexity
	List<MachineCom> machineComs; 
	ConveyorFamilyImp cf;
	
	// Constructors:
	public RobotAgent(String name, ConveyorFamily cf, List<Machine> machines) {  
		// Set the passed in values first
		this.name = name;
		this.cf = (ConveyorFamilyImp) cf;		
		
		// Then set the values that need to be initialized within this class, specifically
		glassToBeProcessed = Collections.synchronizedList(new ArrayList<MyGlass>());
		machineComs = Collections.synchronizedList(new ArrayList<MachineCom>());
		
		// This loop will go for the number of machines that are in the amchines argument
		for (Machine m: machines) {
			machineComs.add(new MachineCom(m));
		}		
	}

	//Messages:
	public void msgProcessGlass(Glass g) { // Get Glass from popUp to robot
		glassToBeProcessed.add(new MyGlass(g, processState.unprocessed));
		stateChanged();
	}

	public void msgDoneProcessingGlass(Glass g) {
		glassToBeProcessed.add(new MyGlass(g, processState.doneProcessing));
		for (MachineCom com: machineComs) {
			if (com.glassBeingProcessed.glass.getId() == g.getId()) {
				com.inUse = false;
				com.glassBeingProcessed = null;
				stateChanged();
				return;
			}
		}
		System.out.println("Hey, this is a bug, I should not be here");
	}

	//Scheduler:
	public boolean pickAndExecuteAnAction() {
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.unprocessed) {
				for (MachineCom com: machineComs) {
					if (com.inUse == false) {
						actPassGlassToMachine(g, com); return true;
					}
				}
			}
		}
		
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.doneProcessing) {
				actPassGlassToCF(g); return true;
			}
		}	
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
		cf.msgDoneProcessingGlass(g.glass, 0);
		glassToBeProcessed.remove(g);
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
}
