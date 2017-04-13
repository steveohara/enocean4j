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
package com._4ng.enocean.eep.eep26.telegram;

import com._4ng.enocean.eep.Rorg;
import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * A class representing a 4BS telegram as defined in the EEP2.6 specification.
 * It provides means for parsing / encoding packets and to extract relevant data
 * encoded as data-payload in packet.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class FourBSTelegram extends EEP26Telegram {

    /**
     * Class constructor, builds an instance of {@link FourBSTelegram} given the
     * {@link ESP3Packet} containing the telegram as payload.
     *
     * @param pkt The {@link ESP3Packet} containing the telegram as payload.
     */
    public FourBSTelegram(ESP3Packet pkt) {
        super(EEP26TelegramType.FourBS);

        // store the raw packet wrapped by this VLDPacket instance
        rawPacket = pkt;

        // get the raw, un-interpreted data payload,
        // for 4BS packets the payload length is 4 bytes
        byte rawData[] = rawPacket.getData();

        // 4 byte payload for all 4BS messages
        payload = new byte[4];

        // fill the payload
        int startingOffset = 1;
        System.arraycopy(rawData, startingOffset, payload, 0, payload.length);

        // intialize the packet address
        address = new byte[4];

        // get the actual address
        startingOffset += payload.length;
        System.arraycopy(rawData, startingOffset, address, 0, address.length);

        // build the actual Rorg
        rorg = new Rorg(rawData[0]);

        // store the status byte
        status = rawData[startingOffset + address.length]; // shall be
        // equal
        // to
        // rawData.length-
    }

    /**
     * Checks if the given {@link ESP3Packet} contains a 4BS packet as payload.
     *
     * @param pkt The {@link ESP3Packet} to check
     * @return true if it contains a 4BS telegram, false otherwise.
     */
    public static boolean is4BSPacket(ESP3Packet pkt) {
        // the packet should be a radio packet with a specific value in the
        // first byte of the data payload (RORG).
        return pkt.isRadio() && pkt.getData()[0] == Rorg.BS4;
    }

}
