package factory_v0_Tim.misc;

import shared.Glass;

public class MyGlassConveyor {
	public enum conveyorState {onConveyor, passPopUp, passCF};
	public Glass glass;
	public conveyorState conveyorState;
	public MyGlassConveyor(Glass glass, conveyorState conveyorState) {
		this.glass = glass;
		this.conveyorState = conveyorState;
	}
}