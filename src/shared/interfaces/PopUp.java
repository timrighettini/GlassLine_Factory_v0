package shared.interfaces;

import shared.Glass;

public interface PopUp {
	public abstract void msgGiveGlassToPopUp(Glass glass); 
	public abstract void msgDoneProcessingGlass(Glass glass);
	public abstract int getFreeChannels();
}
