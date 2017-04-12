/**
 * @author andreabiasi
 */
package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Read system log from device databank
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoRdSysLog extends ESP3Packet {
    public CoRdSysLog() {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x04;
        buildPacket();
    }
}