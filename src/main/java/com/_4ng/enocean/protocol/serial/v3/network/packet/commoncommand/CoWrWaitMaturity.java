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
 * Waiting till end of maturity time before received radio telegrams will transit
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrWaitMaturity extends ESP3Packet {
    /**
     * @param waitEndMaturity : 0: Radio telegrams are send immediately 1: Radio telegrams are send after the maturity time is elapsed
     */
    public CoWrWaitMaturity(byte waitEndMaturity) {
        packetType = COMMON_COMMAND;
        data[0] = 0x10;
        data[1] = waitEndMaturity;
        buildPacket();
    }
}