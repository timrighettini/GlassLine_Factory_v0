package factory_v0_Tim.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import shared.interfaces.ConveyorFamily;
import transducer.Transducer;

import factory_v0_Tim.agents.SensorAgent;
import factory_v0_Tim.interfaces.Conveyor;
import factory_v0_Tim.interfaces.PopUp;
import factory_v0_Tim.interfaces.Sensor;
import factory_v0_Tim.misc.ConveyorFamilyImp;
import factory_v0_Tim.test.Mock.MockAnimation;
import factory_v0_Tim.test.Mock.MockConveyor;
import factory_v0_Tim.test.Mock.MockConveyorFamily;
import factory_v0_Tim.test.Mock.MockPopUp;

public class SensorTestCases {

	// These cases will only test to make sure that the INNER functionality of this agent works.  These tests will not involve the entire coveyor family
	
	@Test
	public void sensorAgentTest() {
		/**
		 * This test will complete the following objectives:  Test all of the sensor agent functionality LOCAL to the agent so that we can see that the sensor agents work
		 * How this test will work:
		 * A.  Three instances of the sensor agent will be be used: entry, popup, and exit along with a transducer
		 * B.  Mocks to be used: Conveyor, ConveyorFamily, PopUp, Animation
		 * C.  A transducer will be used to simulate the animation events for the sensors
		 * D.  Outline:
		 * 	1.  Entry Sensor Test:
		 * 		a.  Test the following preconditions:  Entry sensor has no glass, Mock Conveyor Has no glass
		 * 		b.  Send the glass from a previous conveyor family (test code) to the entry sensor
		 * 		c.  Test the following postconditions:  Entry sensor has a glass with state justEntered, Mock Conveyor Has no glass
		 * 		d.  Run sensor scheduler
		 * 		e.  Test postconditions: Entry sensor still has glass with the Yes onSensor state, Mock Conveyor Has glass, GUI conveyor should have been turned on in MockAnimation
		 * 		f.  Have transducer call that glass has left sensor
		 * 		g.  Check postconditions:  Entry sensor still has glass with changed state, Mock Conveyor Has glass
		 * 		h.  Run scheduler
		 * 		i.  Check postconditions:  Entry sensor has no glass, Mock Conveyor Has glass, Mock Conveyor Family received positionFree msg()
		 * 
		 * 	2.  PopUp Sensor Test:
		 * 		a.  Test the following preconditions:  PopUp sensor has no glass, Mock Conveyor Has glass
		 * 		b.  Have transducer send a message to this sensor agent that glass has hit GUI popUp sensor
		 * 		c.  Test the following postconditions:  PopUp sensor has a glass with state justEntered, Mock Conveyor Has glass
		 * 		d.  Run sensor scheduler
		 * 		e.  Test postconditions: PopUp sensor still has glass with the Yes onSensor state, Mock Conveyor Has glass with updated state, GUI conveyor should still be turned on in MockAnimation
		 * 		f.  Have transducer call that glass has left sensor
		 * 		g.  Check postconditions:  PopUp sensor still has glass with changed state, Mock Conveyor Has glass
		 * 		h.  Run scheduler
		 * 		i.  Check postconditions:  PopUp sensor has no glass, Mock Conveyor Has glass
		 * 
		 * 	3.  Exit Sensor Test:
		 * 		a.  Test the following preconditions:  Exit sensor has no glass, Mock Conveyor Has glass
		 * 		b.  Have transducer send a message to this sensor agent that glass has hit GUI exit sensor
		 * 		c.  Test the following postconditions:  Exit sensor has a glass with state justEntered, Mock Conveyor Has glass
		 * 		d.  Run sensor scheduler
		 * 		e.  Test postconditions: Exit sensor still has glass with the Yes onSensor state, Mock Conveyor Has no glass
		 * 		f.  Have transducer call that glass has left sensor
		 * 		g.  Check postconditions:  Exit sensor still has glass with changed state, Mock Conveyor Has no glass
		 * 		h.  Run scheduler
		 * 		i.  Check postconditions:  Exit sensor has no glass, Mock Conveyor Has glass, GUI conveyor should be turned off
		 * 
		 *  4.  GUI conveyor tests:  Test all of the turnConveyorOnOff() condititons to make sure that the right transducer calls are made -- this is self-explanatory 
		 * */
		
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
		Conveyor mockConveyor = new MockConveyor("mockConveyor", transducer);
		
		// Make the Mock PopUp
		PopUp mockPopUp = new MockPopUp("mockPopUp", transducer);
		
		// Make the Mock Animation for the Tests outlined in 4.
		MockAnimation mockAnimation = new MockAnimation(transducer);
		
		// Instantiate the conveyorFamilies and place everything inside them
		ConveyorFamily mockCF = new MockConveyorFamily("mockCF");
		ConveyorFamily realCF = new ConveyorFamilyImp("realCF", mockConveyor, sensors, mockPopUp);
		
		// Link up the conveyor families
		realCF.setNextCF(mockCF);
		mockCF.setPrevCF(realCF);
		
		// Now that everything has been set up within the conveyor families, let us begin the first set of entry sensor tests
		
		// Check for the following preconditions:
		assertTrue(realCF.getSensor("entry").getGlassSheets().size() == 0); // There should not be any glass within this array
		assertTrue(realCF.getConveyor().getGlassSheets().size() == 0); // There should not be any glass within this array either
		
		/*
		 * 	1.  Entry Sensor Test:
		 * 		a.  Test the following preconditions:  Entry sensor has no glass, Mock Conveyor Has no glass
		 * 		b.  Send the glass from a previous conveyor family (test code) to the entry sensor
		 * 		c.  Test the following postconditions:  Entry sensor has a glass with state justEntered, Mock Conveyor Has no glass
		 * 		d.  Run sensor scheduler
		 * 		e.  Test postconditions: Entry sensor still has glass with the Yes onSensor state, Mock Conveyor Has glass, GUI conveyor should have been turned on in MockAnimation
		 * 		f.  Have transducer call that glass has left sensor
		 * 		g.  Check postconditions:  Entry sensor still has glass with changed state, Mock Conveyor Has glass
		 * 		h.  Run scheduler
		 * 		i.  Check postconditions:  Entry sensor has no glass, Mock Conveyor Has glass, Mock Conveyor Family received positionFree msg()
		 */
	}

}
