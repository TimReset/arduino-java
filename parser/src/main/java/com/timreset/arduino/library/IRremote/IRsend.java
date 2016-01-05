package com.timreset.arduino.library.IRremote;

/**
 * @author Tim
 * @date 30.11.2015
 */
public class IRsend {

	private long lastSignal;

	public void sendNEC(long signal, int bit) {
		lastSignal = signal;
	}

	public long getLastSignal() {
		return lastSignal;
	}
}
