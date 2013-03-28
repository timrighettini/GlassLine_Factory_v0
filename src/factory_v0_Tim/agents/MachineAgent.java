package factory_v0_Tim.agents;

import java.util.*;
import engine.agent.Agent;
import factory_v0_Tim.agents.RobotAgent.processState;
import shared.Glass;
import shared.enums.MachineType;
import transducer.TChannel;
import transducer.TEvent;

public class MachineAgent extends Agent {

	//Name: MachineAgent

	//Description:  Will compete a process upon a piece of glass.

	//Data:
	enum processState {unprocessed, doneProcessing, processing}; 

	private class MyGlass {
		Glass glass;
		processState processState;
		public MyGlass(Glass glass, processState processState) {
			this.glass = glass;
			this.processState = processState;
		}
	}

	List<MyGlass> glassToBeProcessed;
	RobotAgent robot; // Need a reference to the attached robot
	MachineType processType; // Designates what process this machine performs

	//Messages:
	public void msgProcessGlass(Glass g) {
		glassToBeProcessed.add(new MyGlass(g, processState.unprocessed));
		stateChanged();
	}

	// Transducer specific message
	public void msgDoneProcessingGlass(Glass g) {
		for (MyGlass glass: glassToBeProcessed) {
			if (glass.glass.getId() == g.getId()) {
				glass.processState = processState.doneProcessing;
			}
		}
	}

	//Scheduler:
	public boolean pickAndExecuteAnAction() {	
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.unprocessed) {
				actProcessGlass(g); return true;
			}
		}
		
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.doneProcessing) {
				actPassGlassToRobot(g); return true;
			}
		}
	
		return false;
	}

	//Actions:
	private void actProcessGlass(MyGlass g) {
		//transducer.sendProcessGlassMessage(); // Stub for when the transducer is set up to send a processing message to the animation
		g.processState = processState.processing;
	}

	private void actPassGlassToRobot(MyGlass g) {
		g.glass.getRecipe().remove(this.processType); // Done with process, does not need to be in recipe anymore
		robot.msgDoneProcessingGlass(g.glass);
		glassToBeProcessed.remove(g);
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
}
