package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Read supplies filter
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoRdFilter extends ESP3Packet {
    public CoRdFilter() {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x0F;
        buildPacket();
    }
}