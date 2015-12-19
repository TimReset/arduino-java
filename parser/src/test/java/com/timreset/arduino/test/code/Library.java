package com.timreset.arduino.test.code;

import com.timreset.arduino.BaseArduino;
import com.timreset.arduino.library.LiquidCrystal.LiquidCrystal;
import com.timreset.arduino.library.TestLibrary.ClassLibrary;
import com.timreset.arduino.library.TestLibrary.OtherClassInLibrary;

/**
 * @author Tim
 * @date 29.11.2015
 */
public class Library extends BaseArduino {

	private final LiquidCrystal lcd = new LiquidCrystal(12, 11, 5, 4, 3, 2);
	private final com.timreset.arduino.library.LiquidCrystal.LiquidCrystal lcd2 = new LiquidCrystal(12, 11, 5, 4, 3, 2);

	@Override
	public void setup() {
		lcd.begin(16, 2);
		lcd.print("hello, world!");
		lcd2.begin(16, 2);
		lcd2.print("hello, world!");
	}

	@Override
	public void loop() {
		lcd.noBlink();
		delay(3000);
		lcd.blink();
		delay(3000);

		OtherClassInLibrary a1 = new OtherClassInLibrary();
		OtherClassInLibrary a2 = new OtherClassInLibrary(2);
		
		ClassLibrary l = new ClassLibrary();
		l.methodWithParameter(a1);
		l.methodWithParameter(a2);
	}
}
