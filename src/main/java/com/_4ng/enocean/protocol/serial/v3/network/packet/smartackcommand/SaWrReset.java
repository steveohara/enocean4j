package com._4ng.enocean.protocol.serial.v3.network.packet.smartackcommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Send reset command to a Smart Ack Client
 *
 * @author andreabiasi
 */
public class SaWrReset extends ESP3Packet {
    /**
     * @param deviceId : Device ID of the Smart Ack Client
     */
    public SaWrReset(int deviceId) {
        packetType = SMART_ACK_COMMAND;
        //Smart ack code
        data[0] = 0x05;
        data[1] = (byte) (deviceId & 0xff);
        data[2] = (byte) ((deviceId & 0xff00) >> 8);
        data[3] = (byte) ((deviceId & 0xff0000) >> 16);
        data[4] = (byte) ((deviceId & 0xff000000) >> 24);
        buildPacket();
    }
}