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

package uk.co._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Order to enter the energy saving mode.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrSleep extends ESP3Packet {
    /**
     * @param deepSleepPeriod : Period in 10 ms units 00000000 = default max. value = max. data range 00 FF FF FF (~ 46h); After waking up, the module generate an internal hardware reset
     */
    public CoWrSleep(int deepSleepPeriod) {
        packetType = COMMON_COMMAND;
        data[0] = (byte) (deepSleepPeriod & 0xff); // Command code
        data[1] = (byte) ((deepSleepPeriod & 0xff00) >> 8);
        data[2] = (byte) ((deepSleepPeriod & 0xff0000) >> 16);
        data[3] = 0x00;
        buildPacket();
    }
}