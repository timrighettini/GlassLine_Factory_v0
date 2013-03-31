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
import factory_v0_Tim.test.Mock.MockAnimation;
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
		 *  7.  Check postconditions: PopUp should have glass with state doneProcessing, MockMachines have no glass, MockConveyor Has no glass, popUp should be up
		 *  8.  Run scheduler
		 *  9.  Check postconditions: PopUp should not have glass, MockMachines have no glass, MockConveyor Has glass, popUp should be down
		 * */
		
		System.out.println("/****************Test: testConveyorOneGlass****************/");
		
		// Create a piece of glass to use for the test
		Glass glass = new Glass(); // Since processing is not an issue with this test, let's just leave that field blank
		
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
		MockConveyor conveyor = new MockConveyor("Conveyor", transducer);
		
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
		ConveyorFamily realCF = new ConveyorFamilyImp("realCF", conveyor, sensors, popUp);
		
		// Link up the conveyor families
		realCF.setPrevCF(mockPrevCF);
		realCF.setNextCF(mockNextCF);
		mockNextCF.setPrevCF(realCF);
		
		mockMachine0.setCF(realCF);
		mockMachine1.setCF(realCF);
		
		/**********/
		
		// Now that the conveyor families are actually set up, let's begin
		
		// Check the following preconditions:
		// There should be no glass within the popUp
		// None of the machines should have glass
		// The conveyor should not have glass
		// popUp should be down
		
		
		
		/*
		 * Outline:
		 * 	1.  Test the following preconditions:  PopUp has no glass, MockMachine have no glass, MockConveyor Has no glass, popUp should be down
		 *  2.  Pass in glass to the popUp
		 *  3.  Check Postconditions: PopUp should have glass with state unprocessed, MockMachines have no glass, MockConveyor Has no glass, popUp should be down
		 *  4.  Run scheduler
		 *  5.  Check postconditions: PopUp should not have glass, MockMachine 0 has glass && MM 1 does not, MockConveyor Has no glass, popUp should be down (it should go up, be passed to machine, and then go back down)
		 *  6.  Pass glass from MockMachine to popUp after "finishing" processing
		 *  7.  Check postconditions: PopUp should have glass with state doneProcessing, MockMachines have no glass, MockConveyor Has no glass, popUp should be up
		 *  8.  Run scheduler
		 *  9.  Check postconditions: PopUp should not have glass, MockMachines have no glass, MockConveyor Has glass, popUp should be down
		 * */
	}
	
	@Test
	public void testPopUpOneGlassProcessingNo() {

	}

}
