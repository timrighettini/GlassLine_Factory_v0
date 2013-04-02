package shared.interfaces;

import factory_Tim.agents.PopUpAgent;
import factory_Tim.interfaces.Conveyor;
import factory_Tim.interfaces.PopUp;
import factory_Tim.interfaces.Sensor;
import factory_Tim.misc.ConveyorFamilyImp;
import shared.Glass;

public interface ConveyorFamily {
	public abstract void msgHereIsGlass(Glass glass);
	public abstract void msgPositionFree();
	public abstract void msgGlassDone(Glass glass, int machineIndex);
}
