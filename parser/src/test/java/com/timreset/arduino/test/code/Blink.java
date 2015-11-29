package com.timreset.arduino.test.code;

import com.timreset.arduino.BaseArduino;

/**
 * @author Tim
 * @date 19.11.2015
 */
public class Blink extends BaseArduino {
	@Override
	public void setup() {
		// initialize digital pin 13 as an output.
		pinMode(13, OUTPUT);
	}

	// the loop function runs over and over again forever
	@Override
	public void loop() {
		digitalWrite(13, HIGH);   // turn the LED on (HIGH is the voltage level)
		delay(1000);              // wait for a second
		digitalWrite(13, LOW);    // turn the LED off by making the voltage LOW
		delay(1000);              // wait for a second
	}
}
