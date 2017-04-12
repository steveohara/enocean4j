package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Delete secure device from controller. It is only possible to delete ALL rockets of a secure device.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrSecuredeviceDel extends ESP3Packet {
    /**
     * @param id : Device ID. If it is the broadcast ID (0xFFFFFFFF), then delete all secure devices from controller
     */
    public CoWrSecuredeviceDel(int id) {
        packetType = COMMON_COMMAND;
        data[0] = 0x1A;
        data[1] = (byte) (id & 0xff);
        data[2] = (byte) ((id & 0xff00) >> 8);
        data[3] = (byte) ((id & 0xff0000) >> 16);
        data[4] = (byte) ((id & 0xff000000) >> 24);
        buildPacket();
    }
}