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

package uk.co._4ng.enocean.protocol.serial.v3.network.packet.radiomessage;

import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * The radio message (payload data without any radio telegram contents) is embedded into the ESP3 packet
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

/*
 * NB Questo pacchetto non � supportato dal TCM310
 * 
 */


public class RadioMessage extends ESP3Packet {
    /**
     * @param messageRorg   : RORG
     * @param mexData       : Message Data Content
     * @param destinationId : Destination ID Broadcast ID: FF FF FF FF
     * @param sourceId      : Receive case: Source ID of the sender Send case: 0x00000000
     * @param dBm           : Send case: 0xFF
     *                      Receive case: Best RSSI value of all received sub telegrams (value decimal without minus)
     */
    public RadioMessage(byte messageRorg, byte[] mexData, byte[] destinationId, byte[] sourceId, byte dBm) {
        packetType = RADIO_MESSAGE;
        data = new byte[1 + mexData.length];
        data[0] = messageRorg;
        System.arraycopy(mexData, 0, data, 1, mexData.length);
        optData = new byte[8];
        optData[0] = destinationId[0];
        optData[1] = destinationId[1];
        optData[2] = destinationId[2];
        optData[3] = destinationId[3];

        optData[4] = sourceId[0];
        optData[5] = sourceId[1];
        optData[6] = sourceId[2];
        optData[7] = sourceId[3];

        buildPacket();
    }
}