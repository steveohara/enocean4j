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

package com._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Write security information (level, keys). This functions does not support the actual security concept should not be used any more
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrSecurity extends ESP3Packet {
    /**
     * @param secLevel    : Type no. of encryption
     * @param key         : Security key
     * @param rollingCode : Reserved
     */
    public CoWrSecurity(byte secLevel, int key, int rollingCode) {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x16;
        data[1] = secLevel;
        data[2] = (byte) (key & 0xff);
        data[3] = (byte) ((key & 0xff00) >> 8);
        data[4] = (byte) ((key & 0xff0000) >> 16);
        data[5] = (byte) ((key & 0xff000000) >> 24);
        data[6] = (byte) (rollingCode & 0xff);
        data[7] = (byte) ((rollingCode & 0xff00) >> 8);
        data[8] = (byte) ((rollingCode & 0xff0000) >> 16);
        data[9] = (byte) ((rollingCode & 0xff000000) >> 24);
        buildPacket();
    }
}
