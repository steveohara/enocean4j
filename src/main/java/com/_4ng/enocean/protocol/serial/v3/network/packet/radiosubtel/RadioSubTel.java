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

package com._4ng.enocean.protocol.serial.v3.network.packet.radiosubtel;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Packet type 3 : Radio Sub Tel
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */
public class RadioSubTel extends ESP3Packet {
    /**
     * @param data          : radio telegram without checksum
     * @param subTelNum     : actual sequence number of subtelegrams
     * @param destinationId : Broadcast radio FF FF FF FF
     * @param dBm           : Send case: FF
     *                      Receive case : best RSSI value of all received subtelegrams (value decimal without minus)
     * @param securityLevel : 0 = telegram unencrypted
     *                      n = type of encryption (not supported any more)
     * @param timeStamp     : Timestamp of 1st subtelegram is the system timer tick [ms] (2 bytes lower address)
     * @param tickSubTel    : Relative time [ms] of each subtelegram
     * @param dBmSubTel     : RSSI value of each subtelegram
     * @param statusSubTel  : Telegram control bits of each subtelegram - used in case of repeating, switch telegram encapsulation, checksum type identification
     */
    public RadioSubTel(byte data[], byte subTelNum, int destinationId, byte dBm, byte securityLevel, byte timeStamp, byte tickSubTel, byte dBmSubTel, byte statusSubTel) {
        packetType = RADIO_SUB_TEL;
        optData[0] = subTelNum;
        optData[1] = (byte) (destinationId & 0xff);
        optData[2] = (byte) ((destinationId & 0xff00) >> 8);
        optData[3] = (byte) ((destinationId & 0xff0000) >> 16);
        optData[4] = (byte) ((destinationId & 0xff000000) >> 24);
        optData[5] = dBm;
        optData[6] = securityLevel;
        optData[7] = (byte) (timeStamp & 0xff);
        optData[8] = (byte) ((timeStamp & 0xff00) >> 8);
        optData[9] = tickSubTel;
        optData[10] = dBmSubTel;
        optData[11] = statusSubTel;
        buildPacket();
    }
}