package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Enable/Disable transmitting additional subtelegram info.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrSubtel extends ESP3Packet {
    /**
     * @param enable : Enable = 1 Disable = 0
     */
    public CoWrSubtel(byte enable) {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x11;
        data[1] = enable;
        buildPacket();
    }
}