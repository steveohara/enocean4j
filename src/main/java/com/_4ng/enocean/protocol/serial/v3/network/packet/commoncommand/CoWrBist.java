package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Perform flash BIST operation (Built-in-self-test)
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrBist extends ESP3Packet {
    public CoWrBist() {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x06;
        buildPacket();
    }
}