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

package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Delete filter from filter list
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrFilterDel extends ESP3Packet {
    /**
     * @param filterType  : Device ID = 0, R-ORG = 1, dBm = 2
     * @param filterValue : Value of filter function �compare�: - device ID - R-ORG - RSSI of radio telegram in dBm
     */
    public CoWrFilterDel(byte filterType, int filterValue) {
        packetType = COMMON_COMMAND;
        //Command code
        data[0] = 0x0C;
        data[1] = filterType;
        data[2] = (byte) (filterValue & 0xff);
        data[3] = (byte) ((filterValue & 0xff00) >> 8);
        data[4] = (byte) ((filterValue & 0xff0000) >> 16);
        data[5] = (byte) ((filterValue & 0xff000000) >> 24);
    }
}