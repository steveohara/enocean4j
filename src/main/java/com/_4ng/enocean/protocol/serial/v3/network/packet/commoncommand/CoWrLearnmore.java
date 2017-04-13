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

/*
 * Enables or disables learn mode of controller
 */
public class CoWrLearnmore extends ESP3Packet {

    public static final byte START_LEARNMODE = 0x01;
    public static final byte END_LEARNMODE = 0x00;

    /**
     * @param enable  : Start Learn mode = 1 End Learn mode = 0
     * @param timeout : Time-Out for the learn mode in ms. When time is 0 then default period of 60�000 ms is used
     * @param channel : 0..0xFD = Channel No. absolute
     *                0xFE = Previous channel relative
     *                0xFF = Next channel relative
     */
    public CoWrLearnmore(byte enable, int timeout, byte channel) {
        packetType = COMMON_COMMAND;
        data = new byte[6];
        //Command code
        data[0] = 0x17;
        data[1] = enable;
        data[2] = (byte) (timeout & 0xff);
        data[3] = (byte) ((timeout & 0xff00) >> 8);
        data[4] = (byte) ((timeout & 0xff0000) >> 16);
        data[5] = (byte) ((timeout & 0xff000000) >> 24);
        optData = new byte[1];
        optData[0] = channel;
        buildPacket();
    }
}