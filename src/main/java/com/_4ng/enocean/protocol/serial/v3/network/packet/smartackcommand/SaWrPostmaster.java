package com._4ng.enocean.protocol.serial.v3.network.packet.smartackcommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Enables/Disables postmaster function of device
 *
 * @author andreabiasi
 */
public class SaWrPostmaster extends ESP3Packet {
    /**
     * @param mailboxCount :Enables/Disables postmaster function of device.
     */
    public SaWrPostmaster(byte mailboxCount) {
        packetType = SMART_ACK_COMMAND;
        //Smart ack code
        data[0] = 0x08;
        data[1] = mailboxCount;
        buildPacket();
    }
}