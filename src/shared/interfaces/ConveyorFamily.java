package shared.interfaces;

import factory_v0_Tim.agents.PopUpAgent;
import factory_v0_Tim.interfaces.Conveyor;
import factory_v0_Tim.interfaces.PopUp;
import factory_v0_Tim.interfaces.Sensor;
import factory_v0_Tim.misc.ConveyorFamilyImp;
import shared.Glass;

public interface ConveyorFamily {
	public abstract void msgHereIsGlass(Glass glass);
	public abstract void msgPositionFree();
	public abstract void msgGlassDone(Glass glass, int machineIndex);
	
	// Getters/Setters
	public abstract Conveyor getConveyor();
	public abstract void setConveyor(Conveyor conveyor);
	public abstract ConveyorFamilyImp getPrevCF();
	public abstract void setPrevCF(ConveyorFamily prevCF);
	public abstract PopUpAgent getPopUp();
	public abstract void setPopUp(PopUp popUp);
	public abstract ConveyorFamilyImp getNextCF();
	public abstract void setNextCF(ConveyorFamilyImp nextCF);
	public abstract String getName();	
	public abstract Sensor getSensor(String arg);
}
