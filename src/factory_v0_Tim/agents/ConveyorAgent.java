package factory_v0_Tim.agents;

import java.util.*;

import javax.swing.Popup;

import shared.Glass;
import shared.interfaces.Conveyor;
import shared.interfaces.ConveyorFamily;
import transducer.TChannel;
import transducer.TEvent;
import engine.agent.Agent;
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
	ConveyorFamilyImp cf;

	//Messages:
	public void msgGiveGlassToConveyor(Glass g) {
		glassSheets.add(new MyGlass(g, conveyorState.onConveyor)); // conveyorState will always initializes to onConveyor
		stateChanged();
	}
	
	public void msgGiveGlassToPopUp(Glass g) {
		for (MyGlass glass: glassSheets) {
			if (glass.glass.getId() == g.getId()) {
				glass.conveyorState = conveyorState.passPopUp;
				stateChanged();
				break;
			}
		}
	}

	public void msgPassOffGlass(Glass g) {
		for (MyGlass glass: glassSheets) {
			if (glass.glass.getId() == g.getId()) {
				glass.conveyorState = conveyorState.passCF;
				stateChanged();
			}
		}
	}

	public void msgPositionFree() {
		positionFreeNextCF = true;
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
					// This rule will only work when the glassSheet is supposed to go to the PopUp, when there is nothing on the pop-up, and when there is a available robot to process the glass
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
		glassSheets.remove(g);
	}

	private void actPassGlassToNextCF(MyGlass g) {
		cf.getNextCF().msgHereIsGlass(g.glass);
		glassSheets.remove(g);
		positionFreeNextCF = false;
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
}
