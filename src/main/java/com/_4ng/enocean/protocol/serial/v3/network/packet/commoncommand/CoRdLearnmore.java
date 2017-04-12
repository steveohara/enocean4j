package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Reads the learnmode state of controller
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoRdLearnmore extends ESP3Packet {
    public CoRdLearnmore() {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x18;
        buildPacket();
    }
}