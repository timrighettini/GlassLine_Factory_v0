package shared.interfaces;

import shared.Glass;

public interface Conveyor {
	public abstract void msgGiveGlassToConveyor(Glass glass);
	public abstract void msgGiveGlassToPopUp(Glass glass);
	public abstract void msgPassOffGlass(Glass glass);
	public abstract void msgUpdateGlass(Glass glass);
	public abstract void msgPositionFree();	
}
