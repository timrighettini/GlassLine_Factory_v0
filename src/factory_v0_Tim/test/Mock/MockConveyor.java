package factory_v0_Tim.test.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shared.Glass;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.Transducer;
import factory_v0_Tim.interfaces.Conveyor;
import factory_v0_Tim.misc.ConveyorFamilyImp;
import factory_v0_Tim.misc.MyGlassConveyor;
import factory_v0_Tim.misc.MyGlassConveyor.conveyorState;

public class MockConveyor extends MockAgent implements Conveyor {
	//Data:
	private List<MyGlassConveyor> glassSheets; // List to hold all of the glass sheets
	private boolean positionFreeNextCF; // Will determine if a piece of glass should be passed to the next conveyor family.  This will initially be set to true.
	private boolean conveyorOn;
	private ConveyorFamily cf;
	
	// Constructors:
	public MockConveyor(String name, Transducer transducer, ConveyorFamily cf) {
		// Set the passed in values first
		super(name, transducer);
		this.cf = (ConveyorFamilyImp) cf;		
		
		// Then set the values that need to be initialized within this class, specifically
		glassSheets = Collections.synchronizedList(new ArrayList<MyGlassConveyor>());
		positionFreeNextCF = true; // Obviously, there will be nothing in the next conveyor set when the system initializes, so I can make the assumption that nothing is there too
		conveyorOn = false; // The conveyor is off when this simulation starts
		
		initializeTransducerChannels();		
	}
	
	private void initializeTransducerChannels() { // Initialize the transducer channels and everything else related to it
		// Register any appropriate channels
		transducer.register(this, TChannel.CONVEYOR); // Set this agent to listen to the CONVEYOR channel of the transducer
	}
	
	// Messages/Actions

	//Messages:
	public void msgGiveGlassToConveyor(Glass g) {
		glassSheets.add(new MyGlassConveyor(g, conveyorState.onConveyor)); // conveyorState will always initializes to onConveyor
		print("Glass with ID (" + g.getId() + ") added to conveyor");
	}
	
	public void msgGiveGlassToPopUp(Glass g) {
		for (MyGlassConveyor glass: glassSheets) {
			if (glass.glass.getId() == g.getId()) {
				glass.conveyorState = conveyorState.passPopUp;
				print("Glass with ID (" + glass.glass.getId() + ") soon going to PopUp");
				break;
			}
		}
	}

	public void msgPassOffGlass(Glass g) {
		for (MyGlassConveyor glass: glassSheets) {
			if (glass.glass.getId() == g.getId()) {
				glass.conveyorState = conveyorState.passCF;
				print("Glass with ID (" + glass.glass.getId() + ") soon going to next ConveyorFamily");
			}
		}
	}

	public void msgPositionFree() {
		positionFreeNextCF = true;
		print("Next conveyor is available for a piece of glass.");
	}

	public void msgUpdateGlass(Glass g) { // This message is akin to a stub, but I wanted to match up to my current interaction diagram – I could just call msgGiveGlassToConveyor directly, but the semantics do not look as good that way
		msgGiveGlassToConveyor(g); 
	}

	//Actions:
	private void actPassGlassToPopUp(MyGlassConveyor g) {
		cf.getPopUp().msgGiveGlassToPopUp(g.glass);
		print("Glass with ID (" + g.glass.getId() + ") passed to PopUp");
		glassSheets.remove(g);
	}

	private void actPassGlassToNextCF(MyGlassConveyor g) {
		cf.getNextCF().msgHereIsGlass(g.glass);
		print("Glass with ID (" + g.glass.getId() + ") passed to nextCF");
		glassSheets.remove(g);
		positionFreeNextCF = false;
	}
	
	// Getters and Setters

	@Override
	public boolean isConveyorOn() {
		return conveyorOn;
	}

	@Override
	public List<MyGlassConveyor> getGlassSheets() {
		return glassSheets;
	}

}
