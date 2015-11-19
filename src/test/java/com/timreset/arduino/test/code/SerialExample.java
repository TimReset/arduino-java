package com.timreset.arduino.test.code;

import com.timreset.arduino.BaseArduino;
import com.timreset.arduino.Serial;

public class SerialExample implements BaseArduino {

	@Override
	public void setup() {
		Serial.begin(12);
	}

	@Override
	public void loop() {
		Serial.println("Hello");
	}
}
