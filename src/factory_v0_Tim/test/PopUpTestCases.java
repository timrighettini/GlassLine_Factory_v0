package factory_v0_Tim.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.ConveyorFamily;
import transducer.Transducer;
import factory_v0_Tim.agents.ConveyorAgent;
import factory_v0_Tim.agents.PopUpAgent;
import factory_v0_Tim.agents.SensorAgent;
import factory_v0_Tim.interfaces.Machine;
import factory_v0_Tim.interfaces.Sensor;
import factory_v0_Tim.misc.ConveyorFamilyImp;
import factory_v0_Tim.misc.MyGlassPopUp.processState;
import factory_v0_Tim.test.Mock.MockAnimation;
import factory_v0_Tim.test.Mock.MockAnimation.PopUpHeightState;
import factory_v0_Tim.test.Mock.MockConveyor;
import factory_v0_Tim.test.Mock.MockConveyorFamily;
import factory_v0_Tim.test.Mock.MockMachine;
import factory_v0_Tim.test.Mock.MockPopUp;

public class PopUpTestCases {

	// These cases will only test to make sure that the INNER functionality of this agent works.  These tests will not involve the entire conveyor family
	
	@Test
	public void testPopUpOneGlassProcessingYes() {
		/**
		 * This test will complete the following objectives:  Test the pop up when it gets glass, sends it to a mock machine for processing, and then gets it back
		 * How this test will work:
		 * A.  PopUp Agent + Mock Animation, Mock Machine, and Mock Conveyor will be used
		 * B.  Transducer will be used to simulate the animation of the popUp and the conveyor, if necessary
		 * Outline:
		 * 	1.  Test the following preconditions:  PopUp has no glass, MockMachine have no glass, MockConveyor Has no glass, popUp should be down
		 *  2.  Pass in glass to the popUp
		 *  3.  Check Postconditions: PopUp should have glass with state unprocessed, MockMachines have no glass, MockConveyor Has no glass, popUp should be down
		 *  4.  Run scheduler
		 *  5.  Check postconditions: PopUp should not have glass, MockMachine 0 has glass && MM 1 does not, MockConveyor Has no glass, popUp should be down (it should go up, be passed to machine, and then go back down)
		 *  6.  Pass glass from MockMachine to popUp after "finishing" processing
		 *  7.  Check postconditions: PopUp should have glass with state doneProcessing and removed recipe item, MockMachines have no glass, MockConveyor Has no glass, popUp should be up
		 *  8.  Run scheduler
		 *  9.  Check postconditions: PopUp should not have glass, MockMachines have no glass, MockConveyor Has glass, popUp should be down
		 * */
		
		System.out.println("/****************Test: testPopUpOneGlassProcessingYes****************/");
		
		// Create a piece of glass to use for the test
		
		// Make the list of processes
		
		List<MachineType> processTypes = new ArrayList<MachineType>();
		processTypes.add(MachineType.CROSS_SEAMER);
		
		Glass glass = new Glass(processTypes); // Processing
		
		// Instantiate the transducer
		Transducer transducer = new Transducer();
		
		// List of types for each sensor
		List<String> typesA = new ArrayList<String>();
		typesA.add("entry");
		
		// List of types for each sensor
		List<String> typesB = new ArrayList<String>();
		typesB.add("popUp");
		
		// List of types for each sensor
		List<String> typesC = new ArrayList<String>();
		typesC.add("exit");
		
		// Create the three sensor agents
		Sensor entrySensor = new SensorAgent("entrySensor", transducer, typesA);
		Sensor popUpSensor = new SensorAgent("popUpSensor", transducer, typesB);
		Sensor exitSensor = new SensorAgent("exitSensor", transducer, typesC);
		
		// Add these sensors to a list and then put them within a conveyor family
		List<Sensor> sensors = new ArrayList<Sensor>();
		sensors.add(entrySensor);
		sensors.add(popUpSensor);
		sensors.add(exitSensor);
		
		// Make the Mock Conveyor
		MockConveyor mockConveyor = new MockConveyor("Conveyor", transducer);
		
		// Make the two machines for the PopUp
		MockMachine mockMachine0 = new MockMachine("mockMachine0", transducer, MachineType.CROSS_SEAMER, 0);
		MockMachine mockMachine1 = new MockMachine("mockMachine1", transducer, MachineType.CROSS_SEAMER, 1);
		
		// Make a list of these machines, and then send it to the 
		List<Machine> mockMachines = new ArrayList<Machine>();
		mockMachines.add(mockMachine0);
		mockMachines.add(mockMachine1);
		
		// Make the PopUp
		PopUpAgent popUp = new PopUpAgent("popUp", transducer, mockMachines);
		
		// Make the Mock Animation for the Tests outlined in 4.
		MockAnimation mockAnimation = new MockAnimation(transducer);
		
		// Instantiate the conveyorFamilies and place everything inside them
		MockConveyorFamily mockPrevCF = new MockConveyorFamily("mockPrevCF");
		MockConveyorFamily mockNextCF = new MockConveyorFamily("mockNextCF");
		ConveyorFamily realCF = new ConveyorFamilyImp("realCF", mockConveyor, sensors, popUp);
		
		// Link up the conveyor families
		realCF.setPrevCF(mockPrevCF);
		realCF.setNextCF(mockNextCF);
		mockNextCF.setPrevCF(realCF);
		
		mockMachine0.setCF(realCF);
		mockMachine1.setCF(realCF);
		
		/**********/
		
		// Now that the conveyor families are actually set up, let's begin
		
		// Check the following preconditions:
		assertTrue(popUp.getGlassToBeProcessed().size() == 0); // There should be no glass within the popUp
		assertTrue(mockMachine0.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockMachine1.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockConveyor.glassSheets.size() == 0); // The conveyor should not have glass
		assertTrue(mockAnimation.popUpHeight == PopUpHeightState.DOWN); // popUp should be down
		
		// Pass in one piece of glass to the popUp that requires processing
		popUp.msgGiveGlassToPopUp(glass);
		
		// Check postconditions
		assertTrue(popUp.getGlassToBeProcessed().size() == 1); // There should be glass within the popUp
		assertTrue(popUp.getGlassToBeProcessed().get(0).processState == processState.unprocessed); // There should be glass within the popUp
		assertTrue(mockMachine0.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockMachine1.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockConveyor.glassSheets.size() == 0); // The conveyor should not have glass
		assertTrue(mockAnimation.popUpHeight == PopUpHeightState.DOWN); // popUp should be down
		
		// Run the scheduler
		popUp.pickAndExecuteAnAction();
		
		// Process the transducer events
		while(transducer.processNextEvent());
		
		// Check postconditions
		assertTrue(popUp.getGlassToBeProcessed().size() == 0); // There should not be glass within the popUp		
		assertTrue(mockMachine0.getGlassToBeProcessed().size() == 1); // Machine 0 should have the glass
		assertTrue(mockMachine0.log.containsString("Glass with ID (" + glass.getId() + ") recieved"));
		assertTrue(mockMachine1.getGlassToBeProcessed().size() == 0); // Machine 1 should not have glass
		assertTrue(mockConveyor.glassSheets.size() == 0); // The conveyor should not have glass
		assertTrue(mockAnimation.popUpHeight == PopUpHeightState.DOWN); // popUp should still be down -- Look in the print I/O for this test to actually see that the popUp moves up then down to get the glass onto the machine
		
		// Have the MockMachine pass the glass after it is done processing
		mockAnimation.glassDoneProcessing(glass);
		
		// Process transducer events
		while(transducer.processNextEvent());

		// Check postconditions
		assertTrue(popUp.getGlassToBeProcessed().size() == 1); // PopUp should have glass now 
		assertTrue(popUp.getGlassToBeProcessed().get(0).processState == processState.doneProcessing); // PopUp glass should have donePrcessing state
		assertTrue(popUp.getGlassToBeProcessed().get(0).glass.getRecipe().containsKey(MachineType.CROSS_SEAMER) 
				&&
				   popUp.getGlassToBeProcessed().get(0).glass.getRecipe().containsValue(false));// PopUp glass should have recipe item "removed"
		// MockMachines should have no glass
		assertTrue(mockMachine0.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockMachine1.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		// MockConveyor should have no glass
		assertTrue(mockConveyor.glassSheets.size() == 0); // The conveyor should not have glass		
		assertTrue(mockAnimation.popUpHeight == PopUpHeightState.UP); // Pop Up should be Up
		
		// Run scheduler
		popUp.pickAndExecuteAnAction();
		
		// Process transducer events
		while(transducer.processNextEvent());
		
		// Check postconditions
		assertTrue(popUp.getGlassToBeProcessed().size() == 0); // Pop up should not have glass
		// MockMachines should have no glass
		assertTrue(mockMachine0.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockMachine1.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		// MockConveyor should have glass
		assertTrue(mockConveyor.glassSheets.size() == 1); // The conveyor should have glass		
		assertTrue(mockAnimation.popUpHeight == PopUpHeightState.DOWN); // Pop Up should be down
		
		// From this point, the popUp is how it was at the beginning of the test
	}
	
	@Test
	public void testPopUpOneGlassProcessingNo() {
		/** This is the same test as the one with processing, except steps 5-8 are taken out */
		System.out.println("/****************Test: testPopUpOneGlassProcessingNo****************/");
		
		// Create a piece of glass to use for the test
		
		// Make the list of processes
		
		List<MachineType> processTypes = new ArrayList<MachineType>();
		processTypes.add(MachineType.CROSS_SEAMER);
		
		Glass glass = new Glass(); // No processing
		
		// Instantiate the transducer
		Transducer transducer = new Transducer();
		
		// List of types for each sensor
		List<String> typesA = new ArrayList<String>();
		typesA.add("entry");
		
		// List of types for each sensor
		List<String> typesB = new ArrayList<String>();
		typesB.add("popUp");
		
		// List of types for each sensor
		List<String> typesC = new ArrayList<String>();
		typesC.add("exit");
		
		// Create the three sensor agents
		Sensor entrySensor = new SensorAgent("entrySensor", transducer, typesA);
		Sensor popUpSensor = new SensorAgent("popUpSensor", transducer, typesB);
		Sensor exitSensor = new SensorAgent("exitSensor", transducer, typesC);
		
		// Add these sensors to a list and then put them within a conveyor family
		List<Sensor> sensors = new ArrayList<Sensor>();
		sensors.add(entrySensor);
		sensors.add(popUpSensor);
		sensors.add(exitSensor);
		
		// Make the Mock Conveyor
		MockConveyor mockConveyor = new MockConveyor("Conveyor", transducer);
		
		// Make the two machines for the PopUp
		MockMachine mockMachine0 = new MockMachine("mockMachine0", transducer, MachineType.CROSS_SEAMER, 0);
		MockMachine mockMachine1 = new MockMachine("mockMachine1", transducer, MachineType.CROSS_SEAMER, 1);
		
		// Make a list of these machines, and then send it to the 
		List<Machine> mockMachines = new ArrayList<Machine>();
		mockMachines.add(mockMachine0);
		mockMachines.add(mockMachine1);
		
		// Make the PopUp
		PopUpAgent popUp = new PopUpAgent("popUp", transducer, mockMachines);
		
		// Make the Mock Animation for the Tests outlined in 4.
		MockAnimation mockAnimation = new MockAnimation(transducer);
		
		// Instantiate the conveyorFamilies and place everything inside them
		MockConveyorFamily mockPrevCF = new MockConveyorFamily("mockPrevCF");
		MockConveyorFamily mockNextCF = new MockConveyorFamily("mockNextCF");
		ConveyorFamily realCF = new ConveyorFamilyImp("realCF", mockConveyor, sensors, popUp);
		
		// Link up the conveyor families
		realCF.setPrevCF(mockPrevCF);
		realCF.setNextCF(mockNextCF);
		mockNextCF.setPrevCF(realCF);
		
		mockMachine0.setCF(realCF);
		mockMachine1.setCF(realCF);
		
		/**********/
		
		// Now that the conveyor families are actually set up, let's begin
		
		// Check the following preconditions:
		assertTrue(popUp.getGlassToBeProcessed().size() == 0); // There should be no glass within the popUp
		assertTrue(mockMachine0.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockMachine1.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockConveyor.glassSheets.size() == 0); // The conveyor should not have glass
		assertTrue(mockAnimation.popUpHeight == PopUpHeightState.DOWN); // popUp should be down
		
		// Pass in one piece of glass to the popUp that requires processing
		popUp.msgGiveGlassToPopUp(glass);
		
		// Check postconditions
		assertTrue(popUp.getGlassToBeProcessed().size() == 1); // There should be glass within the popUp
		assertTrue(popUp.getGlassToBeProcessed().get(0).processState == processState.unprocessed); // There should be glass within the popUp
		assertTrue(mockMachine0.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockMachine1.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockConveyor.glassSheets.size() == 0); // The conveyor should not have glass
		assertTrue(mockAnimation.popUpHeight == PopUpHeightState.DOWN); // popUp should be down
		
		// Run the scheduler
		popUp.pickAndExecuteAnAction();
		
		// Process the transducer events
		while(transducer.processNextEvent());		
	
		// Check postconditions
		assertTrue(popUp.getGlassToBeProcessed().size() == 0); // Pop up should not have glass
		// MockMachines should have no glass
		assertTrue(mockMachine0.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		assertTrue(mockMachine1.getGlassToBeProcessed().size() == 0); // None of the machines should have glass
		// MockConveyor should have glass
		assertTrue(mockConveyor.glassSheets.size() == 1); // The conveyor should have glass		
		assertTrue(mockAnimation.popUpHeight == PopUpHeightState.DOWN); // Pop Up should be down
		
		// From this point, the popUp is how it was at the beginning of the test
	}

}
