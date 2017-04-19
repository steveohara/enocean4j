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

package uk.co._4ng.enocean.protocol.serial.v3.network.packet.radio;

import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;
import uk.co._4ng.enocean.util.EnOceanUtils;

/**
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */
public class Radio extends ESP3Packet {
    /**
     * Optional data: Byte vector which contain all optional data passed with
     * the constructor
     */

    /**
     * Constructor to initialize data and optional data in the radio packet
     *
     * @param data          : data to sent, in a byte vector
     * @param subTelNum     : number of subtelegram; Send: 3 / receive: 1 ... y
     * @param destinationId : Broadcast radio: FF FF FF FF ADT radio: Destination ID
     *                      (=address)
     * @param dBm           : Send case: FF Receive case: best RSSI value of all received
     *                      subtelegrams (value decimal without minus)
     * @param securityLevel : 0 = telegram unencrypted n = type of encryption (not
     *                      supported any more)
     */
    // public Radio(byte data[], byte subTelNum, int destinationId , byte dBm,
    // byte securityLevel){
    public Radio(byte data[], byte subTelNum, byte[] destinationId, byte dBm, byte securityLevel) {

        packetType = RADIO;
        //this.data = new byte[data.length];
        this.data = data;
        optData = new byte[7];
        optData[0] = subTelNum;

		/*
         * Destination ID intero this.optData[1] = (byte) (destinationId &
		 * 0xff); this.optData[2] = (byte) ((destinationId & 0xff00)>>8);
		 * this.optData[3] = (byte) ((destinationId & 0xff0000)>>16);
		 * this.optData[4] = (byte) ((destinationId & 0xff000000)>>32);
		 */

        // Indirizzo del dispositivo di destinazione, dal piu significativo al
        // meno significativo
        optData[1] = destinationId[0];
        optData[2] = destinationId[1];
        optData[3] = destinationId[2];
        optData[4] = destinationId[3];

        // ----------------------------------------------------------

        optData[5] = dBm;
        optData[6] = securityLevel;
        buildPacket();

    }

    public Radio(byte data[]) {
        packetType = RADIO;
        this.data = data;
        optData = new byte[0];
        buildPacket();
    }

    public Radio(ESP3Packet pkt) throws Exception {
        if (pkt.isRadio()) {
            syncByte = pkt.getSyncByte();
            packetType = pkt.getPacketType();
            data = pkt.getData();
            optData = pkt.getOptData();
            buildPacket();
        }
        else {
            throw new Exception("Incompatible packet type");
        }
    }

    /**
     * Gets a Radio packet to be sent to the transceiver, and then to the device
     * with the given address
     *
     * @param address Address to send to
     * @param payload Payload to send
     * @return A radio packet
     */
    public static Radio getRadio(byte[] address, byte[] payload, boolean send) {
        // prepare the radio packet
        Radio pkt = null;

        // public Radio(byte data[], byte subTelNum, byte[] destinationId , byte
        // dBm, byte securityLevel)
        // subtel number is set to 0x3 when sending
        // dBm is set to (0xFF) in communications between the EnJApi and the
        // physical device. TODO: check if it works, was 0x00
        // the security level (CRC8) is ignored in the advanced radio
        // composition 0 (disabled)
        if (send) {
            // fill the packet
            pkt = new Radio(payload, (byte) 0x03, address, (byte) 0xFF, (byte) 0x00);

        }
        else {
            // TODO: to be supported in future releases
        }
        return pkt;
    }

    @Override
    public String toString() {
        return EnOceanUtils.toHexString(getPacketAsBytes());
    }
}