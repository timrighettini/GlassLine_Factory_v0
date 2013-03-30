package factory_v0_Tim.test.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import factory_v0_Tim.agents.MachineAgent;
import factory_v0_Tim.interfaces.Machine;
import factory_v0_Tim.interfaces.PopUp;
import factory_v0_Tim.misc.MyGlassPopUp;
import factory_v0_Tim.misc.MyGlassPopUp.processState;

public class MockPopUp extends MockAgent implements PopUp {
	
	// Data:	
	private class MachineCom { // Will hold a communication channel to a robot, allowing for the possibility to communicate to multiple robots at once
		MachineAgent machine; // Robot reference
		boolean inUse; // Is this channel currently occupied by a piece of glass
		MachineType processType; // What process does this robot do?  Does the glass need to undergo this process?
		MyGlassPopUp glassBeingProcessed; // This reference needs to be held so PopUpAgents know which piece of glass is being processed by the robot.  This name will be abbreviated to glassBeingProcessed.
		
		public MachineCom(Machine machine) {
			this.machine = (MachineAgent) machine;
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
	public MockPopUp(String name, Transducer transducer, ConveyorFamily cf, List<Machine> machines) {  
		// Set the passed in values first
		super(name, transducer);
		this.cf = cf;		
		
		// Then set the values that need to be initialized within this class, specifically
		glassToBeProcessed = Collections.synchronizedList(new ArrayList<MyGlassPopUp>());
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
		glassToBeProcessed.add(new MyGlassPopUp(g, processState.unprocessed));
		print("Glass with ID (" + g.getId() + ") added");
	}

	public void msgDoneProcessingGlass(Glass g) {
		glassToBeProcessed.add(new MyGlassPopUp(g, processState.doneProcessing));
		for (MachineCom com: machineComs) {
			if (com.glassBeingProcessed.glass.getId() == g.getId()) {
				com.inUse = false;
				com.glassBeingProcessed = null;
				print("Glass with ID (" + g.getId() + ") recieved from machine");
				break;
			}
		}
	}
	
	//Actions:
	private void actPassGlassToMachine(MyGlassPopUp g, MachineCom com) {
		if (g.glass.getRecipe().containsKey(com.processType)) {
			com.machine.msgProcessGlass(g.glass);
			transducer.fireEvent(TChannel.ALL_GUI, TEvent.POPUP_DO_MOVE_UP, null); // Make sure to move the GUI popUp up
			print("Glass with ID (" + g.glass.getId() + ") passed to Machine " + com.machine.getName() + "for processing");
			com.glassBeingProcessed = g;
			com.inUse = true;
			glassToBeProcessed.remove(g);	
		}
		else {
			g.processState = processState.doneProcessing;
			actPassGlassToConveyor(g);
			// Remove statement isn’t needed – it is done within the actPassGlassToConveyor
		}
	}

	private void actPassGlassToConveyor(MyGlassPopUp g) {
		cf.getConveyor().msgUpdateGlass(g.glass);
		transducer.fireEvent(TChannel.ALL_GUI, TEvent.POPUP_DO_MOVE_DOWN, null); // Make sure to move the GUI popUp down
		if (!cf.getConveyor().isConveyorOn()) { // Make sure that the conveyor is also turned on if it is off
			transducer.fireEvent(TChannel.ALL_GUI, TEvent.POPUP_DO_MOVE_DOWN, null);
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
		for (MachineCom com: machineComs) {
			if (com.inUse == false)
				freeChannels++;
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

	
}
