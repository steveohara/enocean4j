/*
 * EnJ - EnOcean Java API
 * 
 * Copyright 2014 Andrea Biasi, Dario Bonino 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com._4ng.enocean.enj.eep.eep26.telegram;

import com._4ng.enocean.enj.eep.Rorg;
import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * @author bonino
 */
public class VLDTelegram extends EEP26Telegram {

    /**
     * @param pkt
     */
    public VLDTelegram(ESP3Packet pkt) {
        super(EEP26TelegramType.VLD);

        // store the raw packet wrapped by this VLDPacket instance
        rawPacket = pkt;

        // get the raw, un-interpreted data payload,
        // for VLD packets the payload may have a variable length
        // therefore data shall be accessed accordingly
        byte rawData[] = rawPacket.getData();

        // compute the payload length: data.length - 1byte RORG - 4 byte
        // senderId - 1 byte status
        int payloadLength = rawData.length - 6;

        // initialize the packet payload
        payload = new byte[payloadLength];

        // read the payload in order
        // TODO: check if reversing is needed, apparently it is not.
        // skip RORG (first byte)
        int startingOffset = 1;
        System.arraycopy(rawData, startingOffset, payload, 0, payloadLength);

        // intialize the packet address
        address = new byte[4];

        // get the actual address
        startingOffset = 1 + payload.length;
        System.arraycopy(rawData, startingOffset, address, 0, address.length);

        // build the actual Rorg
        rorg = new Rorg(rawData[0]);

        // no func nor type definitions are provided in these packets...
    }

    public static boolean isVLDPacket(ESP3Packet pkt) {
        // the packet should be a radio packet with a specific value in the
        // first byte of the data payload (RORG).
        return pkt.isRadio() && pkt.getData()[0] == Rorg.VLD;
    }

}
