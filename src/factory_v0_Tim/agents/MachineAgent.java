package factory_v0_Tim.agents;

import java.util.*;

import engine.agent.Agent;
import factory_v0_Tim.misc.ConveyorFamilyImp;
import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.ConveyorFamily;
import shared.interfaces.Machine;
import transducer.TChannel;
import transducer.TEvent;

public class MachineAgent extends Agent implements Machine {

	//Name: MachineAgent

	//Description:  Will compete a process upon a piece of glass.

	//Data:
	enum processState {unprocessed, doneProcessing, processing}; 

	private class MyGlass {
		Glass glass;
		processState processState;
		public MyGlass(Glass glass, processState processState) {
			this.glass = glass;
			this.processState = processState;
		}
	}

	List<MyGlass> glassToBeProcessed;
	RobotAgent robot; // Need a reference to the attached robot
	MachineType processType; // Designates what process this machine performs
	
	ConveyorFamilyImp cf; // Reference to the conveyor family.  This was not previously needed, because thr robot handled this, but now the robot agent is not being used, so the machine agent needs a reference to the conveyor family
	
	//Constructors:
	public MachineAgent(String name, MachineType processType, ConveyorFamily cf) { // Will exclude the robot unless it is needed
		// Initialize the variables based upon the constructor parameters first
		this.name = name;
		this.cf = (ConveyorFamilyImp) cf;
		this.processType = processType;
		
		// Then initialize everything else
		glassToBeProcessed = Collections.synchronizedList(new ArrayList<MyGlass>());
	}
	
	//Messages:
	public void msgProcessGlass(Glass g) {
		glassToBeProcessed.add(new MyGlass(g, processState.unprocessed));
		print("Glass with ID (" + g.getId() + ") recieved");
		stateChanged();
	}

	// Transducer specific message
	public void msgDoneProcessingGlass(Glass g) {
		for (MyGlass glass: glassToBeProcessed) {
			if (glass.glass.getId() == g.getId()) {
				glass.processState = processState.doneProcessing;
				print("Glass with ID (" + g.getId() + ") done being processed");
			}
		}
	}

	//Scheduler:
	public boolean pickAndExecuteAnAction() {	
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.unprocessed) {
				actProcessGlass(g); return true;
			}
		}
		
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.doneProcessing) {
				actPassGlassToRobot(g); return true;
			}
		}
	
		return false;
	}

	//Actions:
	private void actProcessGlass(MyGlass g) {
		//transducer.sendProcessGlassMessage(); // Stub for when the transducer is set up to send a processing message to the animation
		g.processState = processState.processing;
		print("Glass with ID (" + g.glass.getId() + ") currently processing...");
	}

	private void actPassGlassToRobot(MyGlass g) {
		g.glass.getRecipe().remove(this.processType); // Done with process, does not need to be in recipe anymore
		print("Glass with ID (" + g.glass.getId() + ") passed to PopUp");
		//robot.msgDoneProcessingGlass(g.glass);
		cf.getPopUp().msgDoneProcessingGlass(g.glass);
		glassToBeProcessed.remove(g);
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MachineType getProcessType() {
		return processType;
	}
}
