package factory_v0_Tim.agents;

import java.util.*;

import javax.swing.Popup;

import shared.Glass;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;
import engine.agent.Agent;
import factory_v0_Tim.interfaces.Conveyor;
import factory_v0_Tim.misc.ConveyorFamilyImp;

public class ConveyorAgent extends Agent implements Conveyor {
	//Name: ConveyorAgent

	//Description:  Will hold the glass until it needs to go into the next conveyor for a different set of processes, or to leave the factory entirely.

	//Data:
	enum conveyorState {onConveyor, passPopUp, passCF};

	private class MyGlass {
		Glass glass;
		conveyorState conveyorState;
		public MyGlass(Glass glass, conveyorState conveyorState) {
			this.glass = glass;
			this.conveyorState = conveyorState;
		}
	}
	List<MyGlass> glassSheets; // List to hold all of the glass sheets
	boolean positionFreeNextCF; // Will determine if a piece of glass should be passed to the next conveyor family.  This will initially be set to true.
	boolean conveyorOn;
	ConveyorFamilyImp cf;
	
	// Constructors:
	public ConveyorAgent(String name, Transducer transducer, ConveyorFamily cf) {
		// Set the passed in values first
		super(name, transducer);
		this.cf = (ConveyorFamilyImp) cf;		
		
		// Then set the values that need to be initialized within this class, specifically
		glassSheets = Collections.synchronizedList(new ArrayList<MyGlass>());
		positionFreeNextCF = true; // Obviously, there will be nothing in the next conveyor set when the system initializes, so I can make the assumption that nothing is there too
		conveyorOn = false; // The conveyor is off when this simulation starts
		
		initializeTransducerChannels();		
	}
	
	private void initializeTransducerChannels() { // Initialize the transducer channels and everything else related to it
		// Register any appropriate channels
		transducer.register(this, TChannel.CONVEYOR); // Set this agent to listen to the CONVEYOR channel of the transducer
	}

	//Messages:
	public void msgGiveGlassToConveyor(Glass g) {
		glassSheets.add(new MyGlass(g, conveyorState.onConveyor)); // conveyorState will always initializes to onConveyor
		print("Glass with ID (" + g.getId() + ") added to conveyor");
		stateChanged();
	}
	
	public void msgGiveGlassToPopUp(Glass g) {
		for (MyGlass glass: glassSheets) {
			if (glass.glass.getId() == g.getId()) {
				glass.conveyorState = conveyorState.passPopUp;
				print("Glass with ID (" + glass.glass.getId() + ") soon going to PopUp");
				stateChanged();
				break;
			}
		}
	}

	public void msgPassOffGlass(Glass g) {
		for (MyGlass glass: glassSheets) {
			if (glass.glass.getId() == g.getId()) {
				glass.conveyorState = conveyorState.passCF;
				print("Glass with ID (" + glass.glass.getId() + ") soon going to next ConveyorFamily");
				stateChanged();
			}
		}
	}

	public void msgPositionFree() {
		positionFreeNextCF = true;
		print("Next conveoyr is available for a piece of glass.");
		stateChanged();
	}

	public void msgUpdateGlass(Glass g) { // This message is akin to a stub, but I wanted to match up to my current interaction diagram – I could just call msgGiveGlassToConveyor directly, but the semantics do not look as good that way
		msgGiveGlassToConveyor(g); 
		stateChanged();
	}

	//Scheduler:
	public boolean pickAndExecuteAnAction() {
		for (MyGlass g: glassSheets) {
			if (g.conveyorState == conveyorState.passPopUp && cf.getPopUp().glassToBeProcessed.isEmpty() == true) {
				if (cf.getPopUp().getFreeChannels() > 0) {						
					// This rule will only work when:
					// 1. the glassSheet is supposed to go to the PopUp, 
					// 2. when there is nothing on the pop-up, and
					// 3. when there is a available machine to process the glass
					actPassGlassToPopUp(g); return true;
				}	
			}
				
		}
		for (MyGlass g: glassSheets) {
			if (g.conveyorState == conveyorState.passCF && positionFreeNextCF == true) {
				actPassGlassToNextCF(g); return true;
			}
		}
		return false;
	}
	
	//Actions:
	private void actPassGlassToPopUp(MyGlass g) {
		cf.getPopUp().msgGiveGlassToPopUp(g.glass);
		print("Glass with ID (" + g.glass.getId() + ") passed to PopUp");
		glassSheets.remove(g);
	}

	private void actPassGlassToNextCF(MyGlass g) {
		cf.getNextCF().msgHereIsGlass(g.glass);
		print("Glass with ID (" + g.glass.getId() + ") passed to nextCF");
		glassSheets.remove(g);
		positionFreeNextCF = false;
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// Turn the conveyor on or off, depending on what the protocol is -- This is an update from the animation, Mock or not
		if (event == TEvent.CONVEYOR_DO_START) {
			conveyorOn = true;
		}
		else if (event == TEvent.CONVEYOR_DO_STOP) {
			conveyorOn = false;
		}
	}
}
