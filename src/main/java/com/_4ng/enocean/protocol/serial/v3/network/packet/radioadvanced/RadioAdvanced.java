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

package com._4ng.enocean.protocol.serial.v3.network.packet.radioadvanced;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * The advanced radio protocol telegram (raw data without LEN and CRC) is embedded into the ESP3 packet
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class RadioAdvanced extends ESP3Packet {
    /**
     * @param rawData   : Advanced radio protocol telegram without the first Length byte. For sending the advanced protocol CRC8 byte can be set to any value. x = Data Length
     * @param subTelNum : Number of sub telegram; Send: 3 / receive: 1 ... y
     * @param dBm       : Send case: FF Receive case: best RSSI value of all received sub telegrams (value decimal without minus)
     */
    public RadioAdvanced(byte rawData[], byte subTelNum, byte dBm) {
        packetType = RADIO_ADVANCED;
        data = rawData;
        optData[0] = subTelNum;
        optData[1] = dBm;
        buildPacket();
    }
}