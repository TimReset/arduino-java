package com.timreset.arduino.test.code;

import com.timreset.arduino.BaseArduino;

/**
 * @author Tim
 * @date 19.11.2015
 */
public class AnalogReadSerial extends BaseArduino {
	// the setup routine runs once when you press reset:
	@Override
	public void setup() {
		// initialize serial communication at 9600 bits per second:
		Serial.begin(9600);
	}

	// the loop routine runs over and over again forever:
	@Override
	public void loop() {
		// read the input on analog pin 0:
		int sensorValue = analogRead(A0);
		// print out the value you read:
		Serial.println(sensorValue);
		delay(1);        // delay in between reads for stability
	}
}
