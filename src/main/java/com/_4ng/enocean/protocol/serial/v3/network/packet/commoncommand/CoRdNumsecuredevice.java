package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Read number of teached in secure devices
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoRdNumsecuredevice extends ESP3Packet {
    public CoRdNumsecuredevice() {
        packetType = COMMON_COMMAND;
        data[0] = 0x1D;
        buildPacket();
    }
}