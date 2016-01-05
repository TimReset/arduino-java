package com.timreset.arduino.ir_receiver;

import com.timreset.arduino.IrReceiverLib;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

/**
 * Тест для {@link IrReceiverLib}
 *
 * @author Tim
 * @date 20.12.2015
 */
@RunWith(Parameterized.class)
public class IRReceiverTest {
    @Parameterized.Parameters(name = "{index}: Type={0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"Power", IrReceiverLib.REMOTE_CONTROL_POWER, IrReceiverLib.SPEAKER_IR_POWER},
                {"Vol down", IrReceiverLib.REMOTE_CONTROL_VOL_DOWN, IrReceiverLib.SPEAKER_IR_VOL_DOWN},
                {"Vol up", IrReceiverLib.REMOTE_CONTROL_VOL_UP, IrReceiverLib.SPEAKER_IR_VOL_UP}
        });
    }

    private final long remoteSignal;
    private final long speakerSignal;

    public IRReceiverTest(String type, long remoteSignal, long speakerSignal) {
        this.remoteSignal = remoteSignal;
        this.speakerSignal = speakerSignal;
    }

    @Test
    public void test() {
        IrReceiverLib irReceiverLib = new IrReceiverLib();
        irReceiverLib.setup();
        Assert.assertTrue(irReceiverLib.irrecv.isEnabled());

        irReceiverLib.irrecv.receive(remoteSignal);
        irReceiverLib.loop();
        Assert.assertEquals(speakerSignal, irReceiverLib.irsend.getLastSignal());
    }
}
