package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Read the device SW version / HW version, chip-ID, etc.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoRdVersion extends ESP3Packet {
    public CoRdVersion() {
        packetType = COMMON_COMMAND;
        // Command code
        data = new byte[1];
        optData = new byte[0];
        data[0] = 0x03;
        buildPacket();
    }
}