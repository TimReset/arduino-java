package com.timreset.arduino.test.code;

import com.timreset.arduino.BaseArduino;

/**
 * @author Tim
 * @date 25.11.2015
 */
public class MethodAccess extends BaseArduino {
	@Override
	public void setup() {

	}

	@Override
	public void loop() {
		privateMethod();
	}

	private final void privateMethod() {

	}
}
