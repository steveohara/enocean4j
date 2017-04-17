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

package com._4ng.enocean.protocol.serial.v3.network.packet.response;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;
import com._4ng.enocean.util.EnOceanUtils;

/**
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */
public class Response extends ESP3Packet {
    public static final byte RET_OK = 0;
    public static final byte RET_ERROR = 1;

    // ---------------- Response type -----------------
    public static final byte RET_NOT_SUPPORTED = 2;
    public static final byte RET_WRONG_PARAM = 3;
    public static final byte RET_OPERATION_DENIED = 4;

    public Response(byte respCode) {
        packetType = RESPONSE;
        data[0] = respCode;
        buildPacket();
    }

    public Response(ESP3Packet pkt) throws Exception {
        if (pkt.isResponse()) {
            syncByte = pkt.getSyncByte();
            packetType = pkt.getPacketType();
            data = pkt.getData();
            optData = pkt.getOptData();
            buildPacket();
        }
        else {
            throw new Exception("Uncompatible packet type");
        }
    }

    public Response() {
    }

    /**
     * Returns true if the status byte is OK
     *
     * @return True if OK
     */
    public boolean retOk() {
        return data[0] == RET_OK;
    }

    /**
     * Returns true if the status byte indicates an error
     *
     * @return True if an error
     */
    public boolean retError() {
        return data[0] == RET_ERROR;
    }

    /**
     * Returns true if the status byte indicates non-support
     *
     * @return True if not supported
     */
    public boolean retNotSupported() {
        return data[0] == RET_NOT_SUPPORTED;
    }

    /**
     * Returns true if status byte indicates an incorrect parameter
     *
     * @return True if wrong parameter
     */
    public boolean retWrongParam() {
        return data[0] == RET_WRONG_PARAM;
    }

    /**
     * Returns true if the status byte indicates operation is denied
     *
     * @return True if operation is denied
     */
    public boolean retOperationDenied() {
        return data[0] == RET_OPERATION_DENIED;
    }

    @Override
    public String toString() {
        StringBuilder stringThis = new StringBuilder();
        stringThis.append("Response[");
        stringThis.append(" 'value' :");
        if (retOk()) {
            stringThis.append("RET_OK");
        }
        else if (retError()) {
            stringThis.append("RET_ERROR");
        }
        else if (retNotSupported()) {
            stringThis.append("RET_NOT_SUPPORTED");
        }
        else if (retWrongParam()) {
            stringThis.append("RET_WRONG_PARAM");
        }
        else if (retOperationDenied()) {
            stringThis.append("RET_OPERATION_DENIED");
        }
        else {
            stringThis.append("RET_UNKNOWN_FORMAT");
        }
        stringThis.append(" , rawPacket: ");
        stringThis.append(EnOceanUtils.toHexString(getPacketAsBytes()));
        stringThis.append(" ]");
        return stringThis.toString();
    }
}