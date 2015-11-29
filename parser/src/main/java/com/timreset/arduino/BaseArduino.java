package com.timreset.arduino;

/**
 * @author Tim
 * @date 16.11.2015
 */
public abstract class BaseArduino {

	public abstract void setup();

	public abstract void loop();

	public static final int HIGH = 0x1;
	public static final int LOW = 0x0;

	public static final int INPUT = 0x0;
	public static final int OUTPUT = 0x1;
	public static final int INPUT_PULLUP = 0x2;

	public static final int A0 = 14;
	public static final int A1 = 15;
	public static final int A2 = 16;
	public static final int A3 = 17;
	public static final int A4 = 18;
	public static final int A5 = 19;
	public static final int A6 = 20;
	public static final int A7 = 21;

	/**
	 * TCCR2A MEM Addr=0xB0 Bits=8 <br> WGM20 0 <br> WGM21 1 <br> COM2B0 4 <br> COM2B1 5 <br> COM2A0 6 <br> COM2A1 7 <br>
	 */
	public volatile int TCCR2A;
	public static final int WGM20 = 0;
	public static final int WGM21 = 1;
	public static final int COM2B0 = 4;
	public static final int COM2B1 = 5;
	public static final int COM2A0 = 6;
	public static final int COM2A1 = 7;

	/**
	 * TCCR2B MEM Addr=0xB1 Bits=8 <br>
	 * CS20 0 <br>
	 * CS21 1 <br>
	 * CS22 2 <br>
	 * WGM22 3 <br>
	 * FOC2B 6 <br>
	 * FOC2A 7 <br>
	 */
	public volatile int TCCR2B;
	public static final int CS20 = 0;
	public static final int CS21 = 1;
	public static final int CS22 = 2;
	public static final int WGM22 = 3;
	public static final int FOC2B = 6;
	public static final int FOC2A = 7;


	/**
	 * OCR2A MEM Addr=0xB3 Bits=8 <br>
	 * OCR2_0 0 <br>
	 * OCR2_1 1 <br>
	 * OCR2_2 2 <br>
	 * OCR2_3 3 <br>
	 * OCR2_4 4 <br>
	 * OCR2_5 5 <br>
	 * OCR2_6 6 <br>
	 * OCR2_7 7 <br>
	 */
	public volatile int OCR2A;

	/**
	 * OCR2B MEM Addr=0xB4 Bits=8 <br>
	 * OCR2_0 0 <br>
	 * OCR2_1 1 <br>
	 * OCR2_2 2 <br>
	 * OCR2_3 3 <br>
	 * OCR2_4 4 <br>
	 * OCR2_5 5 <br>
	 * OCR2_6 6 <br>
	 * OCR2_7 7 <br>
	 */
	public  volatile int OCR2B;
	public static final int OCR2_0 = 0;
	public static final int OCR2_1 = 1;
	public static final int OCR2_2 = 2;
	public static final int OCR2_3 = 3;
	public static final int OCR2_4 = 4;
	public static final int OCR2_5 = 5;
	public static final int OCR2_6 = 6;
	public static final int OCR2_7 = 7;

	/**
	 * TIMSK2 MEM Addr=0x70 Bits=8 <br>
	 * TOIE2 0 <br>
	 * OCIE2A 1 <br>
	 * OCIE2B 2 <br>
	 */
	public volatile int TIMSK2;
	public static final int TOIE2 = 0;
	public static final int OCIE2A = 1;
	public static final int OCIE2B = 2;
	
	protected final HardwareSerial Serial = new HardwareSerial();

	public void pinMode(int pin, int mode) {
		//TODO implement!
	}

	public void digitalWrite(int pin, int value) {
		//TODO implement!
	}

	public int digitalRead(int pin) {
		//TODO implement
		return 0;
	}

	public int analogRead(int pin) {
		//TODO implement
		return 0;
	}

	public void analogReference(int mode) {
		//TODO implement
	}

	public void analogWrite(int pin, int value) {
		//TODO implement
	}

	public void delay(long ms) {
		//TODO implement
	}

	public void delayMicroseconds(int us) {
		//TODO implement
	}

	public int _BV(int bit) {
		return 1 << bit;
	}

	//	void pinMode(uint8_t, uint8_t);
	//	void digitalWrite(uint8_t, uint8_t);
	//	int digitalRead(uint8_t);
	//	int analogRead(uint8_t);
	//	void analogReference(uint8_t mode);
	//	void analogWrite(uint8_t, int);
	//	void delay(unsigned long);
	//	void delayMicroseconds(unsigned int us);

}

