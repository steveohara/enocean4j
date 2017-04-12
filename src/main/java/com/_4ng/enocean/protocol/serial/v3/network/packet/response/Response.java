/**
 *
 */
package com._4ng.enocean.protocol.serial.v3.network.packet.response;

import com._4ng.enocean.enj.util.EnOceanUtils;
import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

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
     * @return
     */
    public boolean retOk() {
        return data[0] == RET_OK;
    }

    /**
     * @return
     */
    public boolean retError() {
        return data[0] == RET_ERROR;
    }

    /**
     * @return
     */
    public boolean retNotSupported() {
        return data[0] == RET_NOT_SUPPORTED;
    }

    /**
     * @return
     */
    public boolean retWrongParam() {
        return data[0] == RET_WRONG_PARAM;
    }

    /**
     * @return
     */
    public boolean retOperationDenied() {
        return data[0] == RET_OPERATION_DENIED;
    }

    /**
     * Provides a readable string representation of this object
     */
    @Override
    public String toString() {
        StringBuffer stringThis = new StringBuffer();
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