package factory_v0_Tim.interfaces;

import java.util.List;

import factory_v0_Tim.agents.PopUpAgent.MyGlassPopUp;

import shared.Glass;

public interface PopUp {
	public abstract void msgGiveGlassToPopUp(Glass glass); 
	public abstract void msgDoneProcessingGlass(Glass glass);
	public abstract int getFreeChannels();
	
	// Getters/Setters
	public abstract boolean isPopUpDown();
	public abstract List<MyGlassPopUp> getGlassToBeProcessed();
	
}
