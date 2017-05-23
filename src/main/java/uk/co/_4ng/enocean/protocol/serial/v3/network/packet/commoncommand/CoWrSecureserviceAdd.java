/*
 * Copyright 2017 enocean4j development teams
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

package uk.co._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Add secure device to coltroller. It is possible to add ony one or more rocker with this function
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */
public class CoWrSecureserviceAdd extends ESP3Packet {
    /**
     * @param slf         : Security Level Format
     * @param id          : Device ID
     * @param privateKey  : 16 bytes private key of the device
     * @param rollingCode : If a 16 bit rolling code is defined in SLF, the MSB is undefined
     * @param direction   : Add device security information to: 0x00 = Inbound table (default)
     *                    0x01 = Outbound table ID = Device ID
     *                    0x02 = Outbound table broadcast ID = Gateway SourceID which can be ChipID or one of BaseIDs 0x02..0xFF = not used
     */
    public CoWrSecureserviceAdd(byte slf, int id, byte[] privateKey, int rollingCode, byte direction) {
        packetType = COMMON_COMMAND;
        data[0] = 0x19;
        data[1] = slf;

        data[2] = (byte) (id & 0xff);
        data[3] = (byte) ((id & 0xff00) >> 8);
        data[4] = (byte) ((id & 0xff0000) >> 16);
        data[5] = (byte) ((id & 0xff000000) >> 24);

        System.arraycopy(privateKey, 0, data, 6, privateKey.length);

        data[6 + privateKey.length] = (byte) (rollingCode & 0xff);
        data[6 + privateKey.length + 1] = (byte) ((rollingCode & 0xff00) >> 8);
        data[6 + privateKey.length + 2] = (byte) ((rollingCode & 0xff0000) >> 16);
        optData[0] = direction;
        buildPacket();
    }

}