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
 * Read mailbox information at the postmaster device, about all learned Smart Ack clients
 *
 * @author andreabiasi
 */
public class SaRdLearnedclients extends ESP3Packet {
    public SaRdLearnedclients() {
        packetType = SMART_ACK_COMMAND;
        //Smart ack code
        data[0] = 0x06;
        buildPacket();
    }
}
