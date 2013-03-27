package shared.interfaces;

import shared.Glass;

public interface ConveyorFamily {
	public abstract void msgHereIsGlass(Glass glass);
	public abstract void msgPositionFree();
	public abstract void msgGlassDone(Glass glass, int machineIndex);
}
