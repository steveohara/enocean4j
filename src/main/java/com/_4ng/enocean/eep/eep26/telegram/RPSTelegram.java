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
package com._4ng.enocean.eep.eep26.telegram;

import com._4ng.enocean.eep.Rorg;
import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * @author bonino
 */
public class RPSTelegram extends EEP26Telegram {

    public RPSTelegram(ESP3Packet pkt) {
        super(EEP26TelegramType.RPS);

        // store the raw packet wrapped by this VLDPacket instance
        rawPacket = pkt;

        // get the raw, un-interpreted data payload,
        // for VLD packets the payload may have a variable length
        // therefore data shall be accessed accordingly
        byte rawData[] = rawPacket.getData();

        // one byte payload for all RPS messages
        payload = new byte[]{rawData[1]};

        // intialize the packet address
        address = new byte[4];

        // get the actual address
        int startingOffset = 1 + payload.length;
        System.arraycopy(rawData, startingOffset, address, 0, address.length);

        // build the actual Rorg
        rorg = new Rorg(rawData[0]);

        // store the status byte
        status = rawData[startingOffset + address.length]; //shall be equal to rawData.length-1

    }

    /**
     * Checks if the given ESP3 packet wraps an RPS telegram or not
     *
     * @param pkt The packet to check
     * @return true if the given packet wraps an RPS telegram, false otherwise
     */
    public static boolean isRPSPacket(ESP3Packet pkt) {
        // the packet should be a radio packet with a specific value in the
        // first byte of the data payload (RORG).
        return pkt.isRadio() && pkt.getData()[0] == Rorg.RPS;
    }

}
