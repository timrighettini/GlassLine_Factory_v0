package factory_v0_Tim.test.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import factory_v0_Tim.agents.RobotAgent;
import factory_v0_Tim.interfaces.Machine;
import factory_v0_Tim.misc.MyGlassMachine;
import factory_v0_Tim.misc.MyGlassMachine.processState;

public class MockMachine extends MockAgent implements Machine {
	
	//Data:
	private List<MyGlassMachine> glassToBeProcessed;
	private RobotAgent robot; // Need a reference to the attached robot
	private MachineType processType; // Designates what process this machine performs
	
	private ConveyorFamily cf; // Reference to the conveyor family.  This was not previously needed, because thr robot handled this, but now the robot agent is not being used, so the machine agent needs a reference to the conveyor family
	
	private int machineChannel; // The channel number for this machine, which will be 0 or 1
	
	// Timer for waking up the agent
    Timer timer = new Timer();
    boolean timerCalled = false; // Makes sure that the timer is not called too many times 
	
	//Constructors:
	public MockMachine(String name, Transducer transducer, MachineType processType, int machineChannel, ConveyorFamily cf) { // Will exclude the robot unless it is needed
		// Initialize the variables based upon the constructor parameters first
		super(name, transducer);
		this.cf = cf;
		this.processType = processType;
		this.machineChannel = machineChannel;
		
		// Then initialize everything else
		glassToBeProcessed = Collections.synchronizedList(new ArrayList<MyGlassMachine>());
		initializeTransducerChannels();		
	}
	
	private void initializeTransducerChannels() { // Initialize the transducer channels and everything else related to it
		// Register any appropriate channels
		transducer.register(this, TChannel.CROSS_SEAMER); // Set this agent to listen to the CROSS_SEAMER channel of the transducer
		// This machine will be used for testing purposes this time around
	}
	
	//Messages:
	public void msgProcessGlass(Glass g) {
		glassToBeProcessed.add(new MyGlassMachine(g, processState.unprocessed));
		transducer.fireEvent(TChannel.ALL_GUI, TEvent.POPUP_DO_MOVE_DOWN, null); // Make sure to move the GUI popUp down
		print("Glass with ID (" + g.getId() + ") recieved");
	}

	// Transducer specific message
	public void msgDoneProcessingGlass(Glass g) {
		for (MyGlassMachine glass: glassToBeProcessed) {
			if (glass.glass.getId() == g.getId()) {
				glass.processState = processState.doneProcessing;
				transducer.fireEvent(TChannel.ALL_GUI, TEvent.POPUP_DO_MOVE_UP, null); // Make sure to move the GUI popUp up
				print("Glass with ID (" + g.getId() + ") done being processed");
				break;
			}
		}
	}
	
	//Actions:
	private void actProcessGlass(MyGlassMachine g) {
		//transducer.sendProcessGlassMessage(); // Stub for when the transducer is set up to send a processing message to the animation
		g.processState = processState.processing;
		print("Glass with ID (" + g.glass.getId() + ") currently processing...");
	}

	private void actPassGlassToCF(MyGlassMachine g) {
		g.glass.getRecipe().remove(this.processType); // Done with process, does not need to be in recipe anymore
		print("Glass with ID (" + g.glass.getId() + ") passed to PopUp");
		//robot.msgDoneProcessingGlass(g.glass);
		cf.msgGlassDone(g.glass, machineChannel);
		glassToBeProcessed.remove(g);
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (args[0] instanceof Glass) { // There should be a glass reference from the GUI glass inside this array
			Glass glass = (Glass) args[0];
			msgDoneProcessingGlass(glass);						
		}			
	}

	@Override
	public MachineType getProcessType() {
		return processType;
	}
}
