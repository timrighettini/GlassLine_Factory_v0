package factory_v0_Tim.agents;

import java.util.*;

import engine.agent.Agent;
import factory_v0_Tim.interfaces.Machine;
import factory_v0_Tim.interfaces.PopUp;
import factory_v0_Tim.misc.ConveyorFamilyImp;
import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class PopUpAgent extends Agent implements PopUp {

	// Name: PopUpAgent

	// Description:  Will act as a mediator between the conveyor agent and the robot agents for getting glass to the processing machines, if necessary.
	// Of course, this agent may not be needed because there is NO ROBOT in the animation. but I will leave it in for now.

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
	// The reason why there is not a middle stage is because this glass is removed from the pop-up 
	// during processing � there should be no reference to a glass sheet that is being processed in the 
	// pop-up agent when it is not with the pop-up agent and with the robot or machine agents

	private class MachineCom { // Will hold a communication channel to a robot, allowing for the possibility to communicate to multiple robots at once
		MachineAgent machine; // Robot reference
		boolean inUse; // Is this channel currently occupied by a piece of glass
		MachineType processType; // What process does this robot do?  Does the glass need to undergo this process?
		MyGlass glassBeingProcessed; // This reference needs to be held so PopUpAgents know which piece of glass is being processed by the robot.  This name will be abbreviated to glassBeingProcessed.
		
		public MachineCom(Machine machine) {
			this.machine = (MachineAgent) machine;
			this.inUse = false; // At start, this channel is obviously not being used, so it has to be false
			this.processType = machine.getProcessType();
			this.glassBeingProcessed = null; // Currently, there is no glass being processed within this channel
		}
	}

	List<MyGlass> glassToBeProcessed; // This name will be abbreviated as glassToBeProcessed in many functions to save on space and complexity
	List<MachineCom> machineComs; 

	// Positional variable for whether the Pop-Up in the GUI is up or down, and it will be changed through the transducer and checked within one of the scheduler rules
	boolean popUpDown; // Is this value is true, then the associated popUp is down (will be changed through the appropriate transducer eventFired(args[]) function.
	
	ConveyorFamilyImp cf;
	
	// Constructors:
	public PopUpAgent(String name, Transducer transducer, ConveyorFamily cf, List<Machine> machines) {  
		// Set the passed in values first
		super(name, transducer);
		this.cf = (ConveyorFamilyImp) cf;		
		
		// Then set the values that need to be initialized within this class, specifically
		glassToBeProcessed = Collections.synchronizedList(new ArrayList<MyGlass>());
		machineComs = Collections.synchronizedList(new ArrayList<MachineCom>());
		
		// This loop will go for the number of machines that are in the amchines argument
		for (Machine m: machines) {
			machineComs.add(new MachineCom(m));
		}
		
		popUpDown = false; // The popUp has to be down when the system starts...
		initializeTransducerChannels();		
	}
	
	private void initializeTransducerChannels() { // Initialize the transducer channels and everything else related to it
		// Register any appropriate channels
		transducer.register(this, TChannel.POPUP); // Set this agent to listen to the POPUP channel of the transducer
	}


	//Messages:
	public void msgGiveGlassToPopUp(Glass g) { // Get Glass from conveyor to PopUp
		glassToBeProcessed.add(new MyGlass(g, processState.unprocessed));
		print("Glass with ID (" + g.getId() + ") added");
		stateChanged();
	}

	public void msgDoneProcessingGlass(Glass g) {
		glassToBeProcessed.add(new MyGlass(g, processState.doneProcessing));
		for (MachineCom com: machineComs) {
			if (com.glassBeingProcessed.glass.getId() == g.getId()) {
				com.inUse = false;
				com.glassBeingProcessed = null;
				print("Glass with ID (" + g.getId() + ") recieved from machine");
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
					if (com.inUse == false && popUpDown == true) {
						actPassGlassToRobot(g, com); return true;
					}
				}
			}
		}
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.doneProcessing) {
				actPassGlassToConveyor(g); return true;
			}
		}
		return false;
	}

	//Actions:
	private void actPassGlassToRobot(MyGlass g, MachineCom com) {
		if (g.glass.getRecipe().containsKey(com.processType)) {
			com.machine.msgProcessGlass(g.glass);
			print("Glass with ID (" + g.glass.getId() + ") passed to Machine " + com.machine.getName() + "for processing");
			com.glassBeingProcessed = g;
			com.inUse = true;
			glassToBeProcessed.remove(g);	
		}
		else {
			g.processState = processState.doneProcessing;
			actPassGlassToConveyor(g);
			// Remove statement isn�t needed � it is done within the actPassGlassToConveyor
		}
	}

	private void actPassGlassToConveyor(MyGlass g) {
		cf.getConveyor().msgUpdateGlass(g.glass);
		print("Glass with ID (" + g.glass.getId() + ") passed to conveyor");
		glassToBeProcessed.remove(g);
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (args[0] instanceof Boolean) { // There will only be one boolean argument as of now, and that tells whether the popUp is UP or DOWN
			popUpDown = (Boolean) args[0];
		}			
	}
	
	public int getFreeChannels() {
		int freeChannels = 0;
		for (MachineCom com: machineComs) {
			if (com.inUse == false)
				freeChannels++;
		}
		
		// Make sure to augment the free channels number by the amount of glasses that are currently within the popUp, so that two glasses do not come up when there shoulkd only be one
		
		freeChannels -= glassToBeProcessed.size();
		
		return freeChannels;
	}
}
