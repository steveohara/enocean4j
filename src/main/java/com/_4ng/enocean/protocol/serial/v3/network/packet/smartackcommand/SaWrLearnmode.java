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

package com._4ng.enocean.protocol.serial.v3.network.packet.smartackcommand;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Enables or disables learn mode of Smart Ack Controller.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class SaWrLearnmode extends ESP3Packet {
    /**
     * @param enable   Start Learnmode = 1
     *                 End Learnmode = 0
     * @param extended Simple Learnmode = 0
     *                 Advance Learnmode = 1
     *                 Advance Learnmode select Rep. = 2
     * @param timeout  Time-Out for the learn mode in ms.
     *                 When time is 0 then default period of 60�000 ms is used
     */
    public SaWrLearnmode(byte enable, byte extended, int timeout) {
        packetType = SMART_ACK_COMMAND;
        //Smart ack code
        data[0] = 0x01;
        //Enable
        data[1] = enable;
        //Extended
        data[2] = extended;
        //Timeout
        data[3] = (byte) (timeout & 0xff);
        data[4] = (byte) ((timeout & 0xff00) >> 8);
        data[5] = (byte) ((timeout & 0xff0000) >> 16);
        data[6] = (byte) ((timeout & 0xff000000) >> 24);
        buildPacket();
    }
}