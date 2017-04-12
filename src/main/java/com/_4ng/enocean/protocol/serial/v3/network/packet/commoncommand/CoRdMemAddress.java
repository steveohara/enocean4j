package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Feedback about used addres and length of the area and the Smart-Ack table
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoRdMemAddress extends ESP3Packet {
    /**
     * @param memoryArea : 	Config area = 0
     *                   Smart Ack Table = 1
     *                   System error log = 2
     */
    public CoRdMemAddress(byte memoryArea) {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x14;
        data[1] = memoryArea;
        buildPacket();
    }
}