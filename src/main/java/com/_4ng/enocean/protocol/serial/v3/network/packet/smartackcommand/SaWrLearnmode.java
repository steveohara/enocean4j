package com._4ng.enocean.protocol.serial.v3.network.packet.smartackcommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Enables or disables learn mode of Smart Ack Controller.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class SaWrLearnmode extends ESP3Packet {
    /**
     * @param enable   Start Learnmode = 1
     *                 End Learnmode = 0
     * @param extended Simple Learnmode = 0
     *                 Advance Learnmode = 1
     *                 Advance Learnmode select Rep. = 2
     * @param timeout  Time-Out for the learn mode in ms.
     *                 When time is 0 then default period of 60�000 ms is used
     */
    public SaWrLearnmode(byte enable, byte extended, int timeout) {
        packetType = SMART_ACK_COMMAND;
        //Smart ack code
        data[0] = 0x01;
        //Enable
        data[1] = enable;
        //Extended
        data[2] = extended;
        //Timeout
        data[3] = (byte) (timeout & 0xff);
        data[4] = (byte) ((timeout & 0xff00) >> 8);
        data[5] = (byte) ((timeout & 0xff0000) >> 16);
        data[6] = (byte) ((timeout & 0xff000000) >> 24);
        buildPacket();
    }
}