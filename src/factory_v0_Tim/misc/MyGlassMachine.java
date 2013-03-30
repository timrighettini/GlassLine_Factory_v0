package factory_v0_Tim.misc;

import shared.Glass;

public class MyGlassMachine {
	public enum processState {unprocessed, doneProcessing, processing};
	public Glass glass;
	public processState processState;
	public MyGlassMachine(Glass glass, processState processState) {
		this.glass = glass;
		this.processState = processState;
	}
}