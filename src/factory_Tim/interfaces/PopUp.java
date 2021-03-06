package factory_Tim.interfaces;

import java.util.List;

import factory_Tim.misc.MyGlassPopUp;

import shared.Glass;
import shared.interfaces.ConveyorFamily;

public interface PopUp {
	public abstract void msgGiveGlassToPopUp(Glass glass); 
	public abstract void msgDoneProcessingGlass(Glass glass);
	public abstract int getFreeChannels();
	public abstract boolean doesGlassNeedProcessing(Glass glass); // This method will be used by the conveyor to see if a piece of glass needs to processing from a machine
	
	// Getters/Setters
	public abstract boolean isPopUpDown();
	public abstract List<MyGlassPopUp> getGlassToBeProcessed();
	public abstract void setCF(ConveyorFamily conveyorFamilyImp);

	// These methods will specifically be used for testing purposes -- do not have to be always be implemented
	public abstract void runScheduler();
}
