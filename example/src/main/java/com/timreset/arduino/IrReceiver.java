package com.timreset.arduino;

/**
 * @author Tim
 * @date 25.11.2015
 */
public class IrReceiver extends BaseArduino {

	private static final long SYSCLOCK = 16000000L;
	// Pulse parms are *50-100 for the Mark and *50+100 for the space
	// First MARK is the one after the long gap
	// pulse parameters in usec
	static final int NEC_HDR_MARK = 9000;
	static final int NEC_HDR_SPACE = 4500;
	static final int NEC_BIT_MARK = 560;
	static final int NEC_ONE_SPACE = 1600;
	static final int NEC_ZERO_SPACE = 560;
	static final int NEC_RPT_SPACE = 2250;

	static final long TOPBIT = 0x80000000;//max_int

	static final long SPEAKER_IR_POWER = 2155823295L;

	static final byte ledPin = 3;

	private void mark(int time) {
		TIMER_ENABLE_PWM();
		//		Arduino.delayMicroseconds(time);
		delayMicroseconds(time);
	}

	/* Leave pin off for time (given in microseconds) */
	private void space(int time) {
		//		digitalWrite(ledPin, LOW);
		TIMER_DISABLE_PWM();
		//		Arduino.delayMicroseconds(time);
		delayMicroseconds(time);
	}

	private void sendNEC(long data, int nbits) {
		enableIROut(38);
		mark(NEC_HDR_MARK);
		space(NEC_HDR_SPACE);
		for (int i = 0; i < nbits; i++) {
			if ((data & TOPBIT) != 0) {
				mark(NEC_BIT_MARK);
				space(NEC_ONE_SPACE);
			} else {
				mark(NEC_BIT_MARK);
				space(NEC_ZERO_SPACE);
			}
			data <<= 1;
		}
		mark(NEC_BIT_MARK);
		space(0);
		Serial.println();
	}

	void enableIROut(int khz) {
		// Enables IR output.  The khz value controls the modulation frequency in kilohertz.
		// The IR output will be on pin 3 (OC2B).
		// This routine is designed for 36-40KHz; if you use it for other values, it's up to you
		// to make sure it gives reasonable results.  (Watch out for overflow / underflow / rounding.)
		// TIMER2 is used in phase-correct PWM mode, with OCR2A controlling the frequency and OCR2B
		// controlling the duty cycle.
		// There is no prescaling, so the output frequency is 16MHz / (2 * OCR2A)
		// To turn the output on and off, we leave the PWM running, but connect and disconnect the output pin.
		// A few hours staring at the ATmega documentation and this will all make sense.
		// See my Secrets of Arduino PWM at http://arcfn.com/2009/07/secrets-of-arduino-pwm.html for details.

		// Disable the Timer2 Interrupt (which is used for receiving IR)
		TIMER_DISABLE_INTR(); //Timer2 Overflow Interrupt

		pinMode(ledPin, OUTPUT);
		digitalWrite(ledPin, LOW); // When not sending PWM, we want it low

		// COM2A = 00: disconnect OC2A
		// COM2B = 00: disconnect OC2B; to send signal set to 10: OC2B non-inverted
		// WGM2 = 101: phase-correct PWM with OCRA as top
		// CS2 = 000: no prescaling
		// The top value for the timer.  The modulation frequency will be SYSCLOCK / 2 / OCR2A.
		TIMER_CONFIG_KHZ(khz);
	}

	void TIMER_ENABLE_PWM() {
		TCCR2A |= _BV(COM2B1);
	}

	void TIMER_DISABLE_PWM() {
		TCCR2A &= ~(_BV(COM2B1));
	}

	private void TIMER_CONFIG_KHZ(int khz) {
		int pwmval = (int) (SYSCLOCK / 2000 / khz);
		TCCR2A = _BV(WGM20);
		TCCR2B = _BV(WGM22) | _BV(CS20);
		OCR2A = pwmval;
		OCR2B = pwmval / 3;
	}

	void TIMER_DISABLE_INTR() {
		TIMSK2 = 0;
	}

	@Override
	public void setup() {
		Serial.begin(57600);
		//		while (!Serial.isOpen()) {
		//			; // wait for serial port to connect. Needed for Leonardo only
		//		}
	}

	// The loop function is called in an endless loop
	@Override
	public void loop() {
		delay(2000);
		sendNEC(SPEAKER_IR_POWER, 32);
		//        for(int i=0; i<cnt; i+=2) {
		//        	Serial.println((1000+i)+":\t"+info[i]);
		//        	Serial.println("    :\t"+info[i+1]);
		//        }
		//        cnt=0;
	}
}
