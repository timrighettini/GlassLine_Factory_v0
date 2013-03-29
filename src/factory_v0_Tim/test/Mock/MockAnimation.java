package factory_v0_Tim.test.Mock;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class MockAnimation implements TReceiver { // This will hold the mock animation for the unit testing
	// This will contain the following Mocks
	
	// Mock Messages that will result in the addition or removal of glass within the sensor agents
	Transducer transducer;
	
	// A mock conveyor that will only turn on or off
	/** a boolean used to turn conveyor movement on or off */
	public boolean conveyorMoving;
	
	// A mock pop up that will only simulate the RISING,DROPPING,UP,DOWN actions;
	public enum PopUpHeightState{UP,DOWN};
	public PopUpHeightState popUpHeight;
	
	//Constructors:
	public MockAnimation(Transducer t) {
		this.transducer = t;
		
		// Initialize the transducer channels
		transducer.register(this, TChannel.ALL_GUI);
		
		// Initialize the other Mock Animation values
		popUpHeight = PopUpHeightState.DOWN;
		conveyorMoving = false;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (event == TEvent.CONVEYOR_DO_START) {
			conveyorMoving = true; // Set the height of the PopUp accordingly
			transducer.fireEvent(TChannel.CONVEYOR, event, args); // Send a follow up message to the popUp
		}
		else if (event == TEvent.CONVEYOR_DO_STOP) {
			conveyorMoving = false; // Set the height of the PopUp accordingly
			transducer.fireEvent(TChannel.CONVEYOR, event, args); // Send a follow up message to the popUp
		}
		else if (event == TEvent.POPUP_DO_MOVE_DOWN) {
			popUpHeight = PopUpHeightState.DOWN; // Set the height of the PopUp accordingly
			transducer.fireEvent(TChannel.CONVEYOR, event, args); // Send a follow up message to the popUp
		}
		else if (event == TEvent.POPUP_DO_MOVE_UP) {
			popUpHeight = PopUpHeightState.UP;
			transducer.fireEvent(TChannel.CONVEYOR, event, args); // Send a follow up message to the popUp
		}
		
	}

}
