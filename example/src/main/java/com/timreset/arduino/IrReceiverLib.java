package com.timreset.arduino;

import com.timreset.arduino.library.IRremote.IRrecv;
import com.timreset.arduino.library.IRremote.IRsend;
import com.timreset.arduino.library.IRremote.decode_results;

/**
 * @author Tim
 * @date 29.11.2015
 */
public class IrReceiverLib extends BaseArduino {
	// Do not remove the include below
	//	#include "ir_receiver.h"

	//					#include "IRremote.h"

	//#define DEBUG

	// Коды кнопок соответсвуют пульту №0925 (В книге это блок DVD, производитель NEC)
	final long REMOTE_CONTROL_POWER = 0xFF906F;
	final long REMOTE_CONTROL_VOL_UP = 0xFFA857;
	final long REMOTE_CONTROL_VOL_DOWN = 0xFFE01F;
	final long REMOTE_CONTROL_REPEAT = 0xFFFFFFFF;

	// Коды кнопок соответсвуют пульту №4456 (В книге это блок DVD, производитель NEC)
/*Список констант инфрокрасного порта аудиоколонок.
 * Тип сигнала - NEC (в термитологии IRremote), количество бит - 32, для всех сигналов,
 * кроме сигнала повторения (SPEAKER_IR_REPEAT), для него это 0 бит*/ long SPEAKER_IR_POWER = 2155823295L;

	final long SPEAKER_IR_VOL_DOWN = 2155809015L;
	final long SPEAKER_IR_VOL_UP = 2155841655L;

	final long SPEAKER_IR_BASS_UP = 2155843695L;
	final long SPEAKER_IR_BASS_DOWN = 2155851855L;

	final long SPEAKER_IR_TONE_UP = 2155827375L;
	final long SPEAKER_IR_TONE_DOWN = 2155835535L;

	final long SPEAKER_IR_AUX_PC = 2155815135L;
	/*Сигнал повторения. 0 бит.*/ long SPEAKER_IR_REPEAT = 4294967295L;
	//Номер порта, к которому подключён ИК приёмник.
	int IR_PIN = A0;

	IRrecv irrecv = new IRrecv(IR_PIN);

	IRsend irsend = new IRsend();

	// Переменная для хранения последнего декодируемого сигнала. Нужна что бы потом в случае повторения сигнала,
	// если присылали сигнал для колонок, можно было отправить этот сигнал колонкам, а не в serial порт.
	long last_value;
	boolean speaker_code = false;

	@Override
	public void setup() {
		//		#ifdef DEBUG
		//		Serial.begin(9600);
		//		#endif
		//		#ifndef DEBUG
		//SlyControl хорошо воспринимает только сигналы переданные только с большой скоростью. Поэтому выставляем максимально поддерживаемую SlyControl скорость.
		Serial.begin(256000);
		//		#endif
		irrecv.enableIRIn();
	}

	@Override
	public void loop() {
		//	irsend.sendNEC(SPEAKER_IR_POWER, 32);

		// Переменная для хранения результата декодирования сигнала.
		decode_results results = new decode_results();

		// Декодируем сигнал
		if (irrecv.decode(results) != 0) {
			final long value = results.value;
			//			#ifndef DEBUG
			// Определям по значению сигнала, какой это сигнал - для колонок или нет.
			if (value == REMOTE_CONTROL_POWER) {
				//			Serial.println("case REMOTE_CONTROL_POWER");
				last_value = SPEAKER_IR_POWER;
				irsend.sendNEC(SPEAKER_IR_POWER, 32);
				irrecv.enableIRIn();
				speaker_code = true;
			} else if (value == REMOTE_CONTROL_VOL_DOWN) {
				//			Serial.println("case REMOTE_CONTROL_VOL_DOWN");
				last_value = SPEAKER_IR_VOL_DOWN;
				irsend.sendNEC(SPEAKER_IR_VOL_DOWN, 32);
				irrecv.enableIRIn();
				speaker_code = true;
			} else if (value == REMOTE_CONTROL_VOL_UP) {
				//			Serial.println("case REMOTE_CONTROL_VOL_UP");
				last_value = SPEAKER_IR_VOL_UP;
				irsend.sendNEC(SPEAKER_IR_VOL_UP, 32);
				irrecv.enableIRIn();
				speaker_code = true;
			} else if (value == REMOTE_CONTROL_REPEAT) {
				// В случае сигнала повторения, проверяем прошлый сигнал, относится ли он к тем сигналам, которые мы должны отправлять колонкам.
				// Если да (т.е. прошлый сигнал был сигнал колонкам), то отправляем колонкам сигнал повторения.
				//			Serial.println("case REMOTE_CONTROL_REPEAT");
				if (speaker_code) {
					//				Serial.println("if REMOTE_CONTROL_REPEAT");
					//				irsend.sendNEC(SPEAKER_IR_REPEAT, 0);
					// Для колонок передаём не сигнал повторения, а сигнал, который был. Т.к. сигнал повторения почему то ими не воспринимается, если его передавать
					irsend.sendNEC(last_value, 32);
					irrecv.enableIRIn();
				} else {
					//				Serial.println("else REMOTE_CONTROL_REPEAT");
					// Если прошлый сигнал был не колонкам, то отправляем сигнал в serial порт.
					// Сигнал повтора в компьютер на передаём, т.к. он его не воспринимает.
					// Serial.write((byte*) &results.value, sizeof(results.value));
				}
			} else {
				//			Serial.println("defaut");
				//					Serial.write((byte*)&results.value, sizeof(results.value));

				//			Serial.write(results.value >> 24);
				//			Serial.write(results.value >> 16);
				//			Serial.write(results.value >> 8);
				//			Serial.write((results.value & 0xFF));
				last_value = results.value;
				speaker_code = false;
			}
			//			#endif
			//			#ifdef DEBUG Serial.println(results.value, 16);
			//			Serial.println(results.bits, 16);
			//			Serial.println(results.rawlen, 16);
			//			Serial.println(results.decode_type, 16);
			//			#endif irrecv.resume();
		}

	/*	String content = "";
	 char character;

	 while (Serial.available()) {
	 character = Serial.read();
	 content.concat(character);
	 }

	 if (content != "") {
	 irsend.sendNEC(SPEAKER_IR_VOL_UP, 32);
	 }*/

	}

}
