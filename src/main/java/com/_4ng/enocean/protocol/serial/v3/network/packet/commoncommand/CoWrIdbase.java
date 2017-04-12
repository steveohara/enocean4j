package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Write ID range base number
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrIdbase extends ESP3Packet {
    public CoWrIdbase(int baseId) {
        packetType = COMMON_COMMAND;
        data[0] = 0x07;
        data[1] = (byte) (baseId & 0xff00);
        data[2] = (byte) ((baseId & 0xff00) >> 8);
        data[3] = (byte) ((baseId & 0xff0000) >> 16);
        data[4] = (byte) 0xff;
        buildPacket();
    }
}