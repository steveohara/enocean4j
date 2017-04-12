package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Read secure device
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoRdSecuredevice extends ESP3Packet {
    /**
     * @param index : Index of secure device to read, starting with 1�255
     */
    public CoRdSecuredevice(byte index) {
        packetType = COMMON_COMMAND;
        data[0] = 0x1B;
        data[1] = index;
        buildPacket();
    }
}