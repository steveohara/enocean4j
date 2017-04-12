package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Read ID range base number
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */
public class CoRdIdbase extends ESP3Packet {
    public CoRdIdbase() {
        packetType = COMMON_COMMAND;
        // Event code
        data[0] = 0x08;
        buildPacket();
    }
}