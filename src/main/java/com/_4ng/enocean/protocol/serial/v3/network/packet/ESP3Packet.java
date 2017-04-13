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

package com._4ng.enocean.protocol.serial.v3.network.packet;

import com._4ng.enocean.protocol.serial.v3.network.crc8.Crc8;
import com._4ng.enocean.protocol.serial.v3.network.packet.event.Event;
import com._4ng.enocean.util.EnOceanUtils;

import java.util.Arrays;

/**
 * A class for representing EnOcean Serial Protocol version 3 packets
 *
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */

// ATTENZIONE, per i metodi setLength e getLength meglio usare tipo int?

public class ESP3Packet {
    /*
     * Attenzione ho tolto la classe Abstract la uso come una normale, perch�
	 * cosi posso impacchettare come generico Packet e successivamente
	 * discriminarne il tipo
	 */

    // --------------- Packet types -----------------

    // ---------------- The packet constants ---------
    public static final byte SYNC_BYTE = 0x55;
    public static final byte RADIO = 1;
    public static final byte RESPONSE = 2;
    public static final byte RADIO_SUB_TEL = 3;
    public static final byte EVENT = 4;
    public static final byte COMMON_COMMAND = 5;
    public static final byte SMART_ACK_COMMAND = 6;
    public static final byte REMOTE_MAN_COMMAND = 7;
    public static final byte RADIO_MESSAGE = 9;
    public static final byte RADIO_ADVANCED = 10;
    // serial synchronization byte
    protected byte syncByte; // Il problema e che byte e signed
    // identifies the packet type
    protected byte packetType;
    // data payload (DATA)
    protected byte[] data;
    // additional data extending the data payload (OPTIONAL_DATA)
    protected byte[] optData;
    // number of bytes in the data part of the packet (DATA_LENGTH)
    private byte dataLength[] = new byte[2]; // 2 byte
    // number of bytes of optional data (OPTIONAL_LENGTH)
    private byte optLength;
    // checksum for bytes DATA_LENGTH, OPTIONAL_LENGTH and TYPE
    private byte crc8h;
    // checksum for DATA and OPTIONAL_DATA
    private byte crc8d;

    // --------------- Constructors --------------------

    /**
     * Empty constructor (to support the bean instantiation pattern)
     */
    public ESP3Packet() {
        syncByte = SYNC_BYTE;
    }

    /**
     * @param packetType
     * @param data
     * @param optData
     */
    public ESP3Packet(byte packetType, byte[] data, byte[] optData) {
        syncByte = 0x55;
        this.packetType = packetType;
        this.data = data;
        this.optData = optData;
        buildPacket();
    }

    /**
     * Computes the length of an ESP3Packet packet given the first 4 bytes
     *
     * @param partialBuffer Buffer to interrogate
     * @return packet length
     */
    public static int getPacketLength(byte partialBuffer[]) {
        byte dataLength[] = new byte[2];
        dataLength[0] = partialBuffer[1];
        dataLength[1] = partialBuffer[2];
        byte optLength = partialBuffer[3];

        // byte array to unsigned int conversion
        int dLength = (dataLength[0] << 8 & 0xff00) + (dataLength[1] & 0xff);

        // byte to unsigned int conversion
        int oLength = optLength & 0xFF;

        return 7 + dLength + oLength;
    }

    public void buildPacket() {
        dataLength[1] = (byte) (data.length & 0x00ff); // Parte bassa dei 2
        // byte
        dataLength[0] = (byte) ((data.length & 0xff00) >> 8 & 0x00ff); // Parte alta
        // dei due
        // byte
        optLength = (byte) (optData.length & 0x00ff);
        byte header[] = new byte[4];
        header[0] = dataLength[0];
        header[1] = dataLength[1];
        header[2] = optLength;
        header[3] = packetType;

        crc8h = Crc8.calc(header);
        byte vectData[] = new byte[data.length + optData.length];
        System.arraycopy(data, 0, vectData, 0, data.length);
        // Se non ho dati opzionali non metto piu nulla nel vettore
        if (optLength != 0) {
            System.arraycopy(optData, 0, vectData, data.length, optData.length);
        }
        crc8d = Crc8.calc(vectData);
    }

    /**
     * @return the syncByte
     */
    public byte getSyncByte() {
        return syncByte;
    }

    /**
     * @param syncByte the syncByte to set
     */
    public void setSyncByte(byte syncByte) {
        this.syncByte = syncByte;
    }

    /**
     * @return the dataLength
     */
    public byte[] getDataLength() {
        return dataLength;
    }

    /**
     * @param dataLength the dataLength to set
     */
    public void setDataLength(byte[] dataLength) {
        this.dataLength = dataLength;
    }

    /**
     * @return the optLength
     */
    public byte getOptLength() {
        return optLength;
    }

    /**
     * @param optLength the optLength to set
     */
    public void setOptLength(byte optLength) {
        this.optLength = optLength;
    }

    /**
     * @return the packetType
     */
    public byte getPacketType() {
        return packetType;
    }

    /**
     * @param packetType the packetType to set
     */
    public void setPacketType(byte packetType) {
        this.packetType = packetType;
    }

    /**
     * @return the crc8h
     */
    public byte getCrc8h() {
        return crc8h;
    }

    /**
     * @param crc8h the crc8h to set
     */
    public void setCrc8h(byte crc8h) {
        this.crc8h = crc8h;
    }

    /**
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * @return the optData
     */
    public byte[] getOptData() {
        return optData;
    }

    /**
     * @param optData the optData to set
     */
    public void setOptData(byte[] optData) {
        this.optData = optData;
    }

    /**
     * @return the crc8d
     */
    public byte getCrc8d() {
        return crc8d;
    }

    /**
     * @param crc8d the crc8d to set
     */
    public void setCrc8d(byte crc8d) {
        this.crc8d = crc8d;
    }

    public byte[] getPacketAsBytes() {
        // byte to unsigned int conversion
        int optLength = this.optLength & 0x00FF;

        // byte array to unsigned int conversion
        int dataLength = (this.dataLength[0] << 8 & 0xff00) + (this.dataLength[1] & 0x00ff);

        // 1 syncByte + 2 dataLength + 1 optLength + 1 packetType + 1 crcr8h +
        // 1crc8d + dataLength + optDataLength
        int packetLengthInBytes = 7 + dataLength + optLength;

        byte[] packetAsBytes = new byte[packetLengthInBytes];

        // Header
        packetAsBytes[0] = syncByte;
        packetAsBytes[1] = this.dataLength[0]; // Attenzione mando prima la
        // parte alta
        packetAsBytes[2] = this.dataLength[1];
        packetAsBytes[3] = this.optLength;
        packetAsBytes[4] = packetType;
        packetAsBytes[5] = crc8h;

        System.arraycopy(data, 0, packetAsBytes, 6, dataLength);

        if (optLength > 0) {
            for (int i = 0; i < optLength; i++) {
                packetAsBytes[6 + dataLength + i] = optData[i];
            }
        }

        packetAsBytes[6 + dataLength + optLength] = crc8d;

        // return the packet as byte array
        return packetAsBytes;
    }

    public byte[] getDataPayload() {
        //copy the data array leaving space for optData, if needed
        byte[] dataPayload = Arrays.copyOf(data, data.length + optData.length);

        if (optData.length > 0) {
            System.arraycopy(optData, 0, dataPayload, data.length, optData.length);
        }

        return dataPayload;
    }

    public void parsePacket(byte[] buffer) {
        // "Inpacchetto" cio che arriva in ingresso

        syncByte = buffer[0];
        dataLength[0] = buffer[1];
        dataLength[1] = buffer[2];
        optLength = buffer[3];
        packetType = buffer[4];
        crc8h = buffer[5];

        // byte array to unsigned int conversion
        int dataLength = (this.dataLength[0] << 8 & 0xff00) + (this.dataLength[1] & 0xff);

        // byte to unsigned int conversion
        int optLength = this.optLength & 0xFF;

        // Inizializzo il vettore dei dati alla lunghezza effettiva
        data = new byte[dataLength];

        System.arraycopy(buffer, 6, data, 0, dataLength);

        // Inizializzo il vettore dati opzionali alla lunghezza effettiva
        optData = new byte[optLength];

        for (int i = 0; i < optLength; i++) {
            optData[i] = buffer[6 + dataLength + i];
        }
        crc8d = buffer[6 + dataLength + optLength];

    } // Fine parsePacket

    // Metodi per discriminare che tipo di pacchetto ho ricevuto
    public boolean isResponse() {
        return packetType == RESPONSE;
    }

    public boolean isEvent() {
        return packetType == EVENT;
    }

    public boolean isRadio() {
        return packetType == RADIO;
    }

    public boolean requiresResponse() {
        return isEvent() && (packetType == Event.SA_RECLAIM_NOT_SUCCESSFUL || packetType == Event.SA_CONFIRM_LEARN || packetType == Event.SA_LEARN_ACK);
    }

    @Override
    public String toString() {
        return "ESP3Packet{" + "syncByte=" + EnOceanUtils.toHexString(syncByte) + ", dataLength=" + EnOceanUtils.toHexString(dataLength) + ", optLength=" + optLength + ", packetType=" + EnOceanUtils.toHexString(packetType) + ", crc8h=" + crc8h + ", data=" + EnOceanUtils.toHexString(data) + ", optData=" + EnOceanUtils.toHexString(optData) + ", crc8d=" + EnOceanUtils.toHexString(crc8d) + '}';
    }
}