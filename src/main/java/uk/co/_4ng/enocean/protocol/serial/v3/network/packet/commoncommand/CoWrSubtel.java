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
 * Enable/Disable transmitting additional subtelegram info.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrSubtel extends ESP3Packet {
    /**
     * @param enable : Enable = 1 Disable = 0
     */
    public CoWrSubtel(byte enable) {
        packetType = COMMON_COMMAND;
        // Command code
        data[0] = 0x11;
        data[1] = enable;
        buildPacket();
    }
}