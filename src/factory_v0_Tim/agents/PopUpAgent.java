package factory_v0_Tim.agents;

import java.util.*;

import engine.agent.Agent;
import factory_v0_Tim.misc.ConveyorFamilyImp;
import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.PopUp;
import transducer.TChannel;
import transducer.TEvent;

public class PopUpAgent extends Agent implements PopUp {

	// Name: PopUpAgent

	// Description:  Will act as a mediator between the conveyor agent and the robot agents for getting glass to the processing machines, if necessary.

	// Data:	
	enum processState {unprocessed, doneProcessing}; 

	private class MyGlass {
		Glass glass;
		processState processState;
		public MyGlass(Glass glass, processState processState) {
			this.glass = glass;
			this.processState = processState;
		}
	}
	// The reason why there is not a middle stage is because this glass is removed from the pop-up 
	// during processing – there should be no reference to a glass sheet that is being processed in the 
	// pop-up agent when it is not with the pop-up agent and with the robot or machine agents

	private class RobotCom { // Will hold a communication channel to a robot, allowing for the possibility to communicate to multiple robots at once
		RobotAgent robot; // Robot reference
		boolean inUse; // Is this channel currently occupied by a piece of glass
		MachineType processType; // What process does this robot do?  Does the glass need to undergo this process?
		MyGlass glassBeingProcessed; // This reference needs to be held so PopUpAgents know which piece of glass is being processed by the robot.  This name will be abbreviated to glassBeingProcessed.
	}

	List<MyGlass> glassToBeProcessed; // This name will be abbreviated as glassToBeProcessed in many functions to save on space and complexity
	List<RobotCom> robotComs; 

	// Positional variable for whether the Pop-Up in the GUI is up or down, and it will be changed through the transducer and checked within one of the scheduler rules
	boolean popUpDown; // Is this value is true, then the associated popUp is down (will be changed through the appropriate transducer eventFired(args[]) function.
	
	ConveyorFamilyImp cf;

	//Messages:
	public void msgGiveGlassToPopUp(Glass g) { // Get Glass from conveyor to PopUp
		glassToBeProcessed.add(new MyGlass(g, processState.unprocessed));
		stateChanged();
	}

	public void msgDoneProcessingGlass(Glass g) {
		glassToBeProcessed.add(new MyGlass(g, processState.doneProcessing));
		for (RobotCom com: robotComs) {
			if (com.glassBeingProcessed.glass.getId() == g.getId()) {
				com.inUse = false;
				com.glassBeingProcessed = null;
				stateChanged();
				return;
			}
		}
		System.out.println("Hey, this is a bug, I should not be here");
	}

	//Scheduler:
	public boolean pickAndExecuteAnAction() {
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.unprocessed) {
				for (RobotCom com: robotComs) {
					if (com.inUse == false && popUpDown == true) {
						actPassGlassToRobot(g, com); return true;
					}
				}
			}
		}
		for (MyGlass g: glassToBeProcessed) {
			if (g.processState == processState.doneProcessing) {
				actPassGlassToConveyor(g); return true;
			}
		}
		return false;
	}

	//Actions:
	private void actPassGlassToRobot(MyGlass g, RobotCom com) {
		if (g.glass.getRecipe().containsKey(com.processType)) {
			com.robot.msgProcessGlass(g.glass);
			com. glassBeingProcessed = g;
			com.inUse = true;
			glassToBeProcessed.remove(g);	
		}
		else {
			g.processState = processState.doneProcessing;
			actPassGlassToConveyor(g);
			// Remove statement isn’t needed – it is done within the actPassGlassToConveyor
		}
	}

	private void actPassGlassToConveyor(MyGlass g) {
		cf.getConveyor().msgUpdateGlass(g.glass);
		glassToBeProcessed.remove(g);
	}

	//Other Methods:
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
	public int getFreeChannels() {
		int freeChannels = 0;
		for (RobotCom com: robotComs) {
			if (com.inUse == false)
				freeChannels++;
		}
		
		// Make sure to augment the free channels number by the amount of glasses that are currently within the popUp, so that two glasses do not come up when there shoulkd only be one
		
		freeChannels -= glassToBeProcessed.size();
		
		return freeChannels;
	}
}
