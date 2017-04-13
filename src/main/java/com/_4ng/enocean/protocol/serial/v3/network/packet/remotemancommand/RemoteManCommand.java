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

package com._4ng.enocean.protocol.serial.v3.network.packet.remotemancommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Remote management send or receive message
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class RemoteManCommand extends ESP3Packet {

    /**
     * @param functionNumber : Range: 0x0000 ... 0x0FFF
     * @param manufacturerId : Range: 0x0000 ... 0x07FF
     * @param mexData        : 0 ... 511 bytes
     * @param destinationId  : Destination ID Broadcast ID: FF FF FF FF
     * @param sourceId       : Receive case: Source ID of the sender Send case: 0x00000000
     * @param dBm            : Send case: 0xFF Receive case: Best RSSI value of all received sub telegrams (value decimal without minus)
     * @param sendWithDelay  : 1: if the first message has to be sent with random delay. When answering to broadcast message this has to be 1, otherwise 0. Default: 0
     */

    public RemoteManCommand(int functionNumber, int manufacturerId, byte mexData[], int destinationId, int sourceId, byte dBm, byte sendWithDelay) {
        packetType = REMOTE_MAN_COMMAND;
        data[0] = (byte) (functionNumber & 0xff);
        data[1] = (byte) ((functionNumber & 0xff00) >> 8);
        data[2] = (byte) (manufacturerId & 0xff);
        data[3] = (byte) ((manufacturerId & 0xff00) >> 8);
        System.arraycopy(mexData, 0, data, 4, mexData.length);
        data[mexData.length] = (byte) (destinationId & 0xff);
        data[mexData.length + 1] = (byte) ((destinationId & 0xff00) >> 8);
        data[mexData.length + 2] = (byte) ((destinationId & 0xff0000) >> 16);
        data[mexData.length + 3] = (byte) ((destinationId & 0xff000000) >> 24);
        data[mexData.length + 4] = (byte) (sourceId & 0xff);
        data[mexData.length + 5] = (byte) ((sourceId & 0xff00) >> 8);
        data[mexData.length + 6] = (byte) ((sourceId & 0xff0000) >> 16);
        data[mexData.length + 7] = (byte) ((sourceId & 0xff000000) >> 24);
        data[mexData.length + 8] = dBm;
        data[mexData.length + 9] = sendWithDelay;
    }
}