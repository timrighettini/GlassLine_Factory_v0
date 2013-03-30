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


	public List<MyGlassPopUp> glassToBeProcessed; // This name will be abbreviated as glassToBeProcessed in many functions to save on space and complexity

	// Positional variable for whether the Pop-Up in the GUI is up or down, and it will be changed through the transducer and checked within one of the scheduler rules
	public boolean popUpDown; // Is this value is true, then the associated popUp is down (will be changed through the appropriate transducer eventFired(args[]) function.
	
	public ConveyorFamily cf;
	
	// Constructors:
	public MockPopUp(String name, Transducer transducer) {  
		// Set the passed in values first
		super(name, transducer);
		
		// Then set the values that need to be initialized within this class, specifically
		glassToBeProcessed = Collections.synchronizedList(new ArrayList<MyGlassPopUp>());

		popUpDown = true; // The popUp has to be down when the system starts...
		initializeTransducerChannels();		
	}
	
	public void initializeTransducerChannels() { // Initialize the transducer channels and everything else related to it
		// Register any appropriate channels
		transducer.register(this, TChannel.POPUP); // Set this agent to listen to the POPUP channel of the transducer
	}


	//Messages:
	public void msgGiveGlassToPopUp(Glass g) { // Get Glass from conveyor to PopUp
		glassToBeProcessed.add(new MyGlassPopUp(g, processState.unprocessed));
		print("Glass with ID (" + g.getId() + ") added");
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
	public void msgDoneProcessingGlass(Glass glass) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFreeChannels() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCF(ConveyorFamily conveyorFamilyImp) {
		cf = conveyorFamilyImp;		
	}

	@Override
	public void runScheduler() {
		// TODO Auto-generated method stub
		
	}	
}
