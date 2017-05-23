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

package uk.co._4ng.enocean.protocol.serial.v3.network.packet.commoncommand;

import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * Write x bytes of the Flash, RAM0, DATA, IDATA, XDATA.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

public class CoWrMem extends ESP3Packet {
    /**
     * @param memoryType    : 	Flash 0x00
     *                      RAM 0 0x01
     *                      data RAM 0x02
     *                      idata RAM 0x03
     *                      xdata RAM 0x04
     * @param memoryAddress : Start address to write
     * @param memoryData    : Data content to write
     */
    public CoWrMem(byte memoryType, int memoryAddress, byte[] memoryData) {
        packetType = COMMON_COMMAND;
        //Command code
        data[0] = 0x12;
        data[1] = memoryType;
        data[2] = (byte) (memoryAddress & 0xff);
        data[3] = (byte) ((memoryAddress & 0xff00) >> 8);
        data[4] = (byte) ((memoryAddress & 0xff0000) >> 16);
        data[5] = (byte) ((memoryAddress & 0xff000000) >> 24);
        System.arraycopy(memoryData, 6, data, 6, memoryData.length);
        buildPacket();
    }
}