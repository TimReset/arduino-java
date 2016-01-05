package com.timreset.arduino.library.IRremote;

/**
 * @author Tim
 * @date 30.11.2015
 */
public class IRrecv {

	private final int irPin;
	private boolean enabled = false;
	private long signal;

	public IRrecv(int irPin) {
		this.irPin = irPin;
	}

	public void enableIRIn() {
		enabled = true;
	}

	public int decode(decode_results decode) {
		if (signal != 0) {
			decode.value = signal;
			signal = 0;
			return 1;
		} else {
			return 0;
		}
	}

	//for test only
	public int getIrPin() {
		return irPin;
	}

	//for test only
	public boolean isEnabled() {
		return enabled;
	}

	public void receive(long signal) {
		this.signal = signal;
	}
}
