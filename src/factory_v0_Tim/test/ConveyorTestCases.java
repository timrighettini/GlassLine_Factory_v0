package factory_v0_Tim.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConveyorTestCases {

	// These cases will only test to make sure that the INNER functionality of this agent works.  These tests will not involve the entire conveyor family
	
	@Test
	public void testConveyorOneGlass() {
		/**
		 * This test will complete the following objectives: It will test the messaging system of the ConveyorAgent, along with the scheduler and action taking
		 * How this test will work:
		 * A.  It will not take into account whether glass has been processed or not, because the conveyorAgent only looks at the ID, and not the glass reference, for comparison
		 *     The assumption will be that all glass, whether processed or not, is the same in the conveyorAgent's eyes
		 * B.  Since sensor functionality for the glass is already tested, and working, within the sensor tests file, I will use the test code to simulate sensor use and simplify things
		 * C.  Note that turning the conveyor on or off is controlled by the popUp and Sensor Agents, thus, no code to test this will be within this case. 
		 *     It is already assumed to work based upon the testing of the other conveyor family agents
		 * D.  Glass will Enter the conveyor, be passed to the popUp, be passed back from the popUp, and then be passed to a mock conveyor family 
		 * */
		
		/*
		 * Conveyor Glass Test:
		 * 1.  Test preconditions:  No glass within the conveyor, positionFreeCF is true
		 * 2.  Pass in glass from "entry" sensor
		 * 3.  Test postconditions:  Glass within conveyor, check glass state is onConveyor, positionFreeCF should not have changed
		 * 4.  Run Scheduler
		 * 5.  Test postConditions:  Nothings should have changed from the last check
		 * 6.  Pass in glass from "popUp" sensor
		 * 7.  Test postconditions:  Glass within conveyor, check glass state is passPopUp, positionFreeCF should not have changed
		 * 8.  Run Scheduler
		 * 9.  Test postconditions:  Glass should not be in conveyor but in Mock Popup, positionFreeCF should not have changed
		 * 10.  Have Mock PopUp pass glass back to the conveyor in a message
		 * 11.  Test postconditions:  Glass within conveyor, check glass state is onConveyor, positionFreeCF should not have changed
		 * 12.  Run Scheduler
		 * 13.  Test postConditions:  Nothings should have changed from the last check
		 * 14.  Pass in glass from "exit" sensor
		 * 15.  Test postconditions:  Glass within conveyor, check glass state is passCF, positionFreeCF should not have changed
		 * 16.  Run Scheduler
		 * 17.  Check postconditions: No glass on conveyor, positionFreeCF should be false, glass should be in nextCF
		 * 18.  Have nextCF message positionFree to the conveyor
		 * 19.  Check Postconditions: No glass in conveyor, positionFreeCF should be true
		 */
	}

}
