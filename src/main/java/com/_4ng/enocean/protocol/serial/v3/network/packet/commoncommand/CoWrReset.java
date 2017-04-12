package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Order to reset the device
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrReset extends ESP3Packet {
    public CoWrReset() {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x02;
        buildPacket();
    }
}