package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Reset the system log from device databank
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrSysLog extends ESP3Packet {
    public CoWrSysLog() {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x01;
        buildPacket();
    }
}