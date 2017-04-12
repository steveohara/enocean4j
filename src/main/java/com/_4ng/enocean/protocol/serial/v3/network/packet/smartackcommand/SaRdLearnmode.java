package com._4ng.enocean.protocol.serial.v3.network.packet.smartackcommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Reads the learnmode state of Smart Ack Controller.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */
public class SaRdLearnmode extends ESP3Packet {
    public SaRdLearnmode() {
        packetType = SMART_ACK_COMMAND;
        //Smart ack code
        data[0] = 0x02;
        buildPacket();
    }
}