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

package com._4ng.enocean.protocol.serial.v3.network.packet.event;

import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;
import com._4ng.enocean.util.EnOceanUtils;

/**
 * PACKET TYPE 4 : EVENT
 * <p>
 * An EVENT is primarily a confirmation for processes and procedures, incl. specific data content. Events are currently used only by Smart Ack.
 * In the current version of ESP3 the type EVENT carries no optional data.
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */
public class Event extends ESP3Packet {

	/*
     * ATTENZIONE IL PACCHETTO EVENT PUO SOLO ESSERE RICEVUTO PER CUI CREDO NON SERVA A NULLA IL COSTRUTTORE
	 */

    // --------- CONSTANT FIELD ----------
    public static final int SA_RECLAIM_NOT_SUCCESSFUL = 1;
    public static final int SA_CONFIRM_LEARN = 2;
    public static final int SA_LEARN_ACK = 3;
    public static final int CO_READY = 4;
    public static final int CO_EVENT_SECUREDEVICE = 5;

    public Event(byte respCode) {
        packetType = 0x04;
        data[0] = respCode;
        buildPacket();
    }

    public Event(ESP3Packet pkt) throws Exception {
        if (pkt.isEvent()) {
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

    /**
     * @return TRUE if the event is SA_RECLAIM_NOT_SUCCESSFUL
     */
    public boolean retSaReclaimNotSuccessful() {
        return data[0] == 0x01;
    }

    /**
     * @return TRUE if the event is SA_CONFIRM_LEARN
     */
    public boolean retSaConfirmLearn() {
        return data[0] == 0x02;
    }

    /**
     * @return TRUE if the event is SA_LEARN_ACK
     */
    public boolean retSaLearnAck() {
        return data[0] == 0x03;
    }

    /**
     * @return TRUE if the event is CO_READY
     */
    public boolean retCoReady() {
        return data[0] == 0x04;
    }

    /**
     * @return TRUE if the event is CO_EVENT_SECUREDEVICES
     */
    public boolean retCoEventSecuredevice() {
        return data[0] == 0x05;
    }

    @Override
    public String toString() {
        StringBuilder stringThis = new StringBuilder();
        stringThis.append("Event[");
        stringThis.append(" 'value' :");
        if (retSaReclaimNotSuccessful()) {
            stringThis.append("SA_RECLAIM_NOT_SUCCESSFUL");
        }
        else if (retSaConfirmLearn()) {
            stringThis.append("SA_CONFIRM_LEARN");
        }
        else if (retSaLearnAck()) {
            stringThis.append("SA_LEARN_ACK");
        }
        else if (retCoReady()) {
            stringThis.append("CO_READY");
        }
        else if (retCoEventSecuredevice()) {
            stringThis.append("CO_EVENT_SECUREDEVICE");
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