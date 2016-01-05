package com.timreset.arduino;

import com.timreset.arduino.library.IRremote.IRrecv;
import com.timreset.arduino.library.IRremote.IRsend;
import com.timreset.arduino.library.IRremote.decode_results;

/**
 * Пример использования библитек.
 *
 * @author Tim
 * @date 29.11.2015
 */
public class IrReceiverLib extends BaseArduino {

    // Коды кнопок соответсвуют пульту №0925 (В книге это блок DVD, производитель NEC)
    public static final long REMOTE_CONTROL_POWER = 0xFF906F;
    public static final long REMOTE_CONTROL_VOL_UP = 0xFFA857;
    public static final long REMOTE_CONTROL_VOL_DOWN = 0xFFE01F;
    public static final long REMOTE_CONTROL_REPEAT = 0xFFFFFFFF;

    // Коды кнопок соответсвуют пульту №4456 (В книге это блок DVD, производитель NEC)
/*Список констант инфрокрасного порта аудиоколонок.
 * Тип сигнала - NEC (в термитологии IRremote), количество бит - 32, для всех сигналов,
 * кроме сигнала повторения (SPEAKER_IR_REPEAT), для него это 0 бит*/

    public static final long SPEAKER_IR_POWER = 2155823295L;

    public static final long SPEAKER_IR_VOL_DOWN = 2155809015L;
    public static final long SPEAKER_IR_VOL_UP = 2155841655L;

    public static final long SPEAKER_IR_BASS_UP = 2155843695L;
    public static final long SPEAKER_IR_BASS_DOWN = 2155851855L;

    public static final long SPEAKER_IR_TONE_UP = 2155827375L;
    public static final long SPEAKER_IR_TONE_DOWN = 2155835535L;

    public static final long SPEAKER_IR_AUX_PC = 2155815135L;
    /**
     * Сигнал повторения. 0 бит.
     */
    public static final long SPEAKER_IR_REPEAT = 4294967295L;
    //Номер порта, к которому подключён ИК приёмник.
    public static final int IR_PIN = A0;

    public final IRrecv irrecv = new IRrecv(IR_PIN);

    public final IRsend irsend = new IRsend();

    // Переменная для хранения последнего декодируемого сигнала. Нужна что бы потом в случае повторения сигнала,
    // если присылали сигнал для колонок, можно было отправить этот сигнал колонкам, а не в serial порт.
    long last_value = 0;

    @Override
    public void setup() {
        // SlyControl хорошо воспринимает только сигналы переданные только с большой скоростью. 
        // Поэтому выставляем максимально поддерживаемую SlyControl скорость.
        Serial.begin(256000);
        irrecv.enableIRIn();
    }

    @Override
    public void loop() {
        // Переменная для хранения результата декодирования сигнала.
        decode_results results = new decode_results();
        // Декодируем сигнал
        if (irrecv.decode(results) != 0) {
            final long value = results.value;
            // Определям по значению сигнала, какой это сигнал - для колонок или нет.
            if (value == REMOTE_CONTROL_POWER) {
                last_value = SPEAKER_IR_POWER;
                irsend.sendNEC(SPEAKER_IR_POWER, 32);
                irrecv.enableIRIn();
            } else if (value == REMOTE_CONTROL_VOL_DOWN) {
                last_value = SPEAKER_IR_VOL_DOWN;
                irsend.sendNEC(SPEAKER_IR_VOL_DOWN, 32);
                irrecv.enableIRIn();
            } else if (value == REMOTE_CONTROL_VOL_UP) {
                last_value = SPEAKER_IR_VOL_UP;
                irsend.sendNEC(SPEAKER_IR_VOL_UP, 32);
                irrecv.enableIRIn();
            } else if (value == REMOTE_CONTROL_REPEAT) {
                // В случае сигнала повторения, проверяем прошлый сигнал, относится ли он к тем сигналам, которые мы должны отправлять колонкам.
                // Если да (т.е. прошлый сигнал был сигнал колонкам), то отправляем колонкам сигнал повторения.
                if (last_value != 0) {
                    // Для колонок передаём не сигнал повторения, а сигнал, который был. 
                    // Т.к. сигнал повторения почему то ими не воспринимается, если его передавать
                    irsend.sendNEC(last_value, 32);
                    irrecv.enableIRIn();
                } else {
                    // Если прошлый сигнал был не колонкам, то отправляем сигнал в serial порт.
                    // Сигнал повтора в компьютер на передаём, т.к. он его не воспринимает.
                    // Serial.write((byte*) &results.value, sizeof(results.value));
                }
            } else {
                // Если значение не опознано логикой, то обнуляем старое значение. 
                last_value = 0;
            }
        }
    }

}
