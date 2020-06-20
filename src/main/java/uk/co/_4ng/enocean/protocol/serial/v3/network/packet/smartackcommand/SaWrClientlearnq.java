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

package uk.co._4ng.enocean.protocol.serial.v3.network.packet.smartackcommand;

import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Code 4 : Sends smart ack learn request telegram to smart ack controller. This function will only be in a Smart Ack Client
 *
 * @author andreabiasi
 */
public class SaWrClientlearnq extends ESP3Packet {
    /**
     * @param msbManufactorId : nnn = Most significant 3 bits of the Manufacturer ID 11111 = reserved / default values
     * @param lsbManufactorId : Least significant bits of the Manufacturer ID
     * @param eep             EEP of the Smart Ack client, who wants to Teach IN.
     */
    public SaWrClientlearnq(byte msbManufactorId, byte lsbManufactorId, int eep) {
        packetType = SMART_ACK_COMMAND;
        //Smart ack code
        data[0] = 0x04;
        data[1] = msbManufactorId;
        data[2] = lsbManufactorId;
        data[4] = (byte) (eep & 0xff);
        data[5] = (byte) ((eep & 0xff00) >> 8);
        data[6] = (byte) ((eep & 0xff0000) >> 16);
        buildPacket();
    }
}