package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Read security information (level, keys). This function does not support the actual security concept and should not be used any more
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoRdSecurity extends ESP3Packet {
    public CoRdSecurity() {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x15;
        buildPacket();
    }
}