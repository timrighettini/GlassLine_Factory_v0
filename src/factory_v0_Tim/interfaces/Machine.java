package factory_v0_Tim.interfaces;

import shared.Glass;
import shared.enums.MachineType;
import shared.interfaces.ConveyorFamily;

public interface Machine {
	public abstract void msgProcessGlass(Glass glass);
	public abstract void msgDoneProcessingGlass(Glass Glass);
	public abstract MachineType getProcessType();
	
	// Getters/Setters
	public abstract void setCF(ConveyorFamily cf);
}
