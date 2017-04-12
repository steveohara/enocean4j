package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Delete all filter from filter list
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrFilterDelAll extends ESP3Packet {
    public CoWrFilterDelAll() {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x0D;
        buildPacket();
    }
}