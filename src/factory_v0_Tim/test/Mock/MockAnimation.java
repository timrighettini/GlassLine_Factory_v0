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
	private boolean conveyorMoving;
	
	// A mock pop up that will only simulate the RISING,DROPPING,UP,DOWN actions;
	private enum PopUpHeightState{UP,DOWN};
	PopUpHeightState popUpHeight;
	
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
		// arg[0] will hold who fired the event
		
		
	}

}
