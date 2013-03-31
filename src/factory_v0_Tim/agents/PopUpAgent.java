package factory_v0_Tim.agents;

import java.util.*;

import engine.agent.Agent;
import factory_v0_Tim.interfaces.Machine;
import factory_v0_Tim.interfaces.PopUp;
import factory_v0_Tim.misc.ConveyorFamilyImp;
import factory_v0_Tim.misc.MyGlassPopUp;
import factory_v0_Tim.misc.MyGlassPopUp.processState;
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
	private class MachineCom { // Will hold a communication channel to a robot, allowing for the possibility to communicate to multiple robots at once
		Machine machine; // Robot reference
		boolean inUse; // Is this channel currently occupied by a piece of glass
		MachineType processType; // What process does this robot do?  Does the glass need to undergo this process?
		MyGlassPopUp glassBeingProcessed; // This reference needs to be held so PopUpAgents know which piece of glass is being processed by the robot.  This name will be abbreviated to glassBeingProcessed.
		
		public MachineCom(Machine machine) {
			this.machine = machine;
			this.inUse = false; // At start, this channel is obviously not being used, so it has to be false
			this.processType = machine.getProcessType();
			this.glassBeingProcessed = null; // Currently, there is no glass being processed within this channel
		}
	}

	private List<MyGlassPopUp> glassToBeProcessed; // This name will be abbreviated as glassToBeProcessed in many functions to save on space and complexity
	private List<MachineCom> machineComs; 

	// Positional variable for whether the Pop-Up in the GUI is up or down, and it will be changed through the transducer and checked within one of the scheduler rules
	private boolean popUpDown; // Is this value is true, then the associated popUp is down (will be changed through the appropriate transducer eventFired(args[]) function.
	
	private ConveyorFamily cf;
	
	// Constructors:
	public PopUpAgent(String name, Transducer transducer, List<Machine> machines) {  
		// Set the passed in values first
		super(name, transducer);
		
		// Then set the values that need to be initialized within this class, specifically
		glassToBeProcessed = Collections.synchronizedList(new ArrayList<MyGlassPopUp>());
		machineComs = Collections.synchronizedList(new ArrayList<MachineCom>());
		
		// This loop will go for the number of machines that are in the amchines argument
		for (Machine m: machines) {
			machineComs.add(new MachineCom(m));
		}
		
		popUpDown = true; // The popUp has to be down when the system starts...
		initializeTransducerChannels();		
	}
	
	private void initializeTransducerChannels() { // Initialize the transducer channels and everything else related to it
		// Register any appropriate channels
		transducer.register(this, TChannel.POPUP); // Set this agent to listen to the POPUP channel of the transducer
	}


	//Messages:
	public void msgGiveGlassToPopUp(Glass g) { // Get Glass from conveyor to PopUp
		glassToBeProcessed.add(new MyGlassPopUp(g, processState.unprocessed));
		print("Glass with ID (" + g.getId() + ") added");
		stateChanged();
	}

	public void msgDoneProcessingGlass(Glass g) {
		glassToBeProcessed.add(new MyGlassPopUp(g, processState.doneProcessing));
		synchronized (machineComs) {
			for (MachineCom com: machineComs) {
				if (com.glassBeingProcessed != null) {
					if (com.glassBeingProcessed.glass.getId() == g.getId()) {
						com.inUse = false;
						com.glassBeingProcessed = null;
						print("Glass with ID (" + g.getId() + ") recieved from machine");
						stateChanged();
						break;
					}
				}
			}
		}
	}

	//Scheduler:
	public boolean pickAndExecuteAnAction() {
		MyGlassPopUp glass = null;
		MachineCom machCom = null;
		
		synchronized(glassToBeProcessed) {
			for (MyGlassPopUp g: glassToBeProcessed) {
				if (g.processState == processState.unprocessed) {
					synchronized(machineComs) {
						for (MachineCom com: machineComs) {
							if ((com.inUse == false && popUpDown == true)) {
								glass = g;
								machCom = com;
								break;
							}
						}
					}
					if (glass != null && machCom != null) {break;} // Make sure to break out of the other loop as well
					if (g.glass.getRecipe().containsKey(machineComs.get(0).processType) && g.glass.getRecipe().containsValue(false)) {
						// Since both machineComs point to the same machine type, this code will fine
						glass = g;
						break;
					}
				}
			}
		}
		if (glass != null) {
			if (machCom != null) {
				actPassGlassToMachine(glass, machCom); return true;
			}
			else {
				actPassGlassToConveyor(glass); return true;
			}
		}
		
		synchronized(glassToBeProcessed) {
			for (MyGlassPopUp g: glassToBeProcessed) {
				if (g.processState == processState.doneProcessing) {
					glass = g;
					break;
				}
			}
		}
		if (glass != null) {
			actPassGlassToConveyor(glass); return true;
		}
		
		return false;
	}

	//Actions:
	private void actPassGlassToMachine(MyGlassPopUp g, MachineCom com) {
		if (g.glass.getRecipe().containsKey(com.processType) && g.glass.getRecipe().containsValue(true)) {
			transducer.fireEvent(TChannel.ALL_GUI, TEvent.POPUP_DO_MOVE_UP, null); // Make sure to move the GUI popUp up
			print("Glass with ID (" + g.glass.getId() + ") passed to Machine " + com.processType + " for processing");
			com.machine.msgProcessGlass(g.glass);
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

	private void actPassGlassToConveyor(MyGlassPopUp g) {
		cf.getConveyor().msgUpdateGlass(g.glass);
		transducer.fireEvent(TChannel.ALL_GUI, TEvent.POPUP_DO_MOVE_DOWN, null); // Make sure to move the GUI popUp down
		if (!cf.getConveyor().isConveyorOn()) { // Make sure that the conveyor is also turned on if it is off
			transducer.fireEvent(TChannel.ALL_GUI, TEvent.CONVEYOR_DO_START, null);
		}
		print("Glass with ID (" + g.glass.getId() + ") passed to conveyor");
		glassToBeProcessed.remove(g);
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// Move the PopUp up or down, depending on what the protocol is -- This is an update from the animation, Mock or not
		if (event == TEvent.POPUP_DO_MOVE_DOWN) { // There will only be one boolean argument as of now, and that tells whether the popUp is UP or DOWN
			popUpDown = true;
		}
		else if (event == TEvent.POPUP_DO_MOVE_UP) { // There will only be one boolean argument as of now, and that tells whether the popUp is UP or DOWN
			popUpDown = false;
		}
		
	}
	
	public int getFreeChannels() {
		int freeChannels = 0;
		synchronized(machineComs) {	
			for (MachineCom com: machineComs) {
				if (com.inUse == false)
					
					freeChannels++;
			}
		}
		
		// Make sure to augment the free channels number by the amount of glasses that are currently within the popUp, so that two glasses do not come up when there shoulkd only be one
		
		freeChannels -= glassToBeProcessed.size();
		
		return freeChannels;
	}

	/**
	 * @return the glassToBeProcessed
	 */
	public List<MyGlassPopUp> getGlassToBeProcessed() {
		return glassToBeProcessed;
	}

	/**
	 * @return the popUpDown
	 */
	public boolean isPopUpDown() {
		return popUpDown;
	}

	@Override
	public void setCF(ConveyorFamily conveyorFamilyImp) {
		cf = conveyorFamilyImp;		
	}

	@Override
	public void runScheduler() {
		pickAndExecuteAnAction();		
	}

	@Override
	public boolean doesGlassNeedProcessing(Glass glass) {
		if (glass.getRecipe().containsKey(machineComs.get(0).processType) && glass.getRecipe().containsValue(true)) { // Both machines on every offline process do the same process
			return true;
		}
		else {
			return false;
		}
	}
}
