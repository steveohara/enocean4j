package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Order to enter the energy saving mode.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrSleep extends ESP3Packet {
    /**
     * @param deepSleepPeriod : Period in 10 ms units 00000000 = default max. value = max. data range 00 FF FF FF (~ 46h); After waking up, the module generate an internal hardware reset
     */
    public CoWrSleep(int deepSleepPeriod) {
        packetType = COMMON_COMMAND;
        data[0] = (byte) (deepSleepPeriod & 0xff); // Command code
        data[1] = (byte) ((deepSleepPeriod & 0xff00) >> 8);
        data[2] = (byte) ((deepSleepPeriod & 0xff0000) >> 16);
        data[3] = 0x00;
        buildPacket();
    }
}