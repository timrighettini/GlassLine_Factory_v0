package factory_v0_Tim.interfaces;

import java.util.List;

import factory_v0_Tim.misc.MyGlassPopUp;

import shared.Glass;
import shared.interfaces.ConveyorFamily;

public interface PopUp {
	public abstract void msgGiveGlassToPopUp(Glass glass); 
	public abstract void msgDoneProcessingGlass(Glass glass);
	public abstract int getFreeChannels();
	
	// Getters/Setters
	public abstract boolean isPopUpDown();
	public abstract List<MyGlassPopUp> getGlassToBeProcessed();
	public abstract void setCF(ConveyorFamily conveyorFamilyImp);

}
