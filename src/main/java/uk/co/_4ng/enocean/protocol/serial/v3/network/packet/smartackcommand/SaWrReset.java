/*
 * Copyright $DateInfo.year enocean4j development teams
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co._4ng.enocean.protocol.serial.v3.network.packet.smartackcommand;

import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

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