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
package uk.co._4ng.enocean.eep.eep26.telegram;

import uk.co._4ng.enocean.eep.EEPIdentifier;
import uk.co._4ng.enocean.eep.Rorg;
import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;

/**
 * A class representing an UTE Teach-In telegram as defined by the EEP2.6
 * specification.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class UTETeachInTelegram extends EEP26Telegram {
    // packet identifiers according to the EnOcean EEP2.6 specification
    public static final byte TEACHIN_REQUEST = (byte) 0x00;
    public static final byte TEACHIN_DELECTION_REQUEST = (byte) 0x01;
    public static final byte TEACHIN_NOT_SPECIFIED = (byte) 0x02;

    // RESPONSE MASK as according to the EnOcean EEP2.6 specification
    public static final byte IS_RESPONSE_MASK = (byte) 0x40;

    // TEACH-IN RESULTS

    // 0x91 = 10010001 ->
    // DB6.BIT_7== 1 ->bidirectional communication
    // DB6.BIT_5..4 == 01 -> request accepted, teach-in successful
    // DB6.BIT3..0 == 001 -> command identifier: teach-in response
    public static final byte BIDIRECTIONAL_TEACH_IN_SUCCESSFUL = (byte) 0x91;

    // 0xb1 = 10110001 ->
    // DB6.BIT_7== 1 ->bidirectional communication
    // DB6.BIT_5..4 == 11 -> request refused, eep not supported
    // DB6.BIT3..0 == 001 -> command identifier: teach-in response
    public static final byte BIDIRECTIONAL_TEACH_IN_REFUSED = (byte) 0xb1;

    // 0xa1 = 10100001 ->
    // DB6.BIT_7== 1 ->bidirectional communication
    // DB6.BIT_5..4 == 10 -> request accepted, teach in canceled
    // DB6.BIT3..0 == 001 -> command identifier: teach-in response
    public static final byte BIDIRECTIONAL_TEACH_IN_DELETION_ACCEPTED = (byte) 0xa1;
    // the response flag
    protected boolean response;
    // the manufacturer id
    private final byte[] manId;
    // the packet EEP
    private final EEPIdentifier eep;

    /**
     * Class constructor, builds an instance of {@link UTETeachInTelegram} given
     * the {@link ESP3Packet} containing the telegram as payload.
     *
     * @param pkt {@link ESP3Packet} containing the telegram as payload.
     */
    public UTETeachInTelegram(ESP3Packet pkt) {
        // call the superclass constructor
        super(EEP26TelegramType.UTETeachIn);

        // by default the packet is not a response
        response = false;

        // store the packet reference
        rawPacket = pkt;

        // initialize the packet payload
        payload = new byte[7];

        // fill the payload

        // get the raw, un-interpreted data payload
        byte[] rawData = rawPacket.getData();

        // get the actual payload
        int startingOffset = 1;
        for (int i = startingOffset; i < startingOffset + payload.length; i++) {
            // reverse fill
            payload[startingOffset + payload.length - (i + 1)] = rawData[i];
        }

        // intialize the packet address
        startingOffset = 8;
        address = new byte[4];

        // get the actual address
        System.arraycopy(rawData, startingOffset, address, 0, address.length);

        // get the manufacturer id
        manId = new byte[2];

        // Consider only DB_6.BIT2 , DB_6.BIT1 , DB_6.BIT0 by performing a
        // bitwise AND with the 00000111 mask.
        manId[0] = (byte) (payload[3] & 0x07);
        manId[1] = payload[4];

        isTeachIn = true;

        // build the rorg
        rorg = new Rorg(payload[0]);

        // build the equipment profile
        eep = new EEPIdentifier(rorg, payload[1], payload[2]);
    }

    /**
     * Checks if the given packet is a UTE TeachIn packet
     *
     * @param pkt the packet to check
     * @return true if it is a UTE Teach In, false otherwise.
     */
    public static boolean isUTETeachIn(ESP3Packet pkt) {
        // the packet should be a radio packet with a specific value in the
        // first byte of the data payload (RORG).
        return pkt.isRadio() && pkt.getData()[0] == Rorg.UTE;
    }

    /**
     * Gets the EnOcean Equipment Profile of the device who has sent this teach
     * in packet.
     *
     * @return The associated EEP identifier
     */
    public EEPIdentifier getEEP() {
        return eep;
    }

    /**
     * Checks if this {@link UTETeachInTelegram} instance is a teach-in request
     *
     * @return true if it is a teach-in request, false otherwise
     */
    public boolean isTeachInRequest() {
        return (payload[6] & 0x30) == TEACHIN_REQUEST || (payload[6] & 0x30) == (byte) 0x20;
    }

    /**
     * Checks if this {@link UTETeachInTelegram} instance is a teach-in deletion
     * request
     *
     * @return true if it is a teach-in deletion request, false otherwise
     */
    public boolean isTeachInDeletionRequest() {
        return ((payload[6] & 0xff) & 1 << TEACHIN_DELECTION_REQUEST) > 0;
    }

    /**
     * Checks if this {@link UTETeachInTelegram} instance is a not specified
     * teach-in
     *
     * @return true if it is a not-specified teach-in packet, false otherwise.
     */
    public boolean isNotSpecifiedTeachIn() {
        return ((payload[6] & 0xff) & 1 << TEACHIN_NOT_SPECIFIED) > 0;
    }

    /**
     * Checks if this packet requires a response
     *
     * @return true if the packet requires a response within 500ms, false
     * otherwise
     */
    public boolean isResponseRequired() {
        return (payload[6] & IS_RESPONSE_MASK) == 0x00;
    }

    /**
     * Checks if the packet represents a teach-in response or not
     *
     * @return the response true if it is a response, false otherwise
     */
    public boolean isResponse() {
        return response;
    }

    /**
     * Sets the response flag, can only be called by extending classes
     *
     * @param response the response flag
     */
    protected void setResponse(boolean response) {
        this.response = response;
    }

    /**
     * Gets the raw {@link ESP3Packet} instance corresponding to this teach-in
     * high-level packet
     *
     * @return the rawPacket
     */
    public ESP3Packet getRawPacket() {
        return rawPacket;
    }


    @Override
    public byte[] getAddress() {
        return address;
    }

    /**
     * Gets the EnOcean RORG of this teach-in packet
     *
     * @return The associated RORG
     */
    public Rorg getRorg() {
        return rorg;
    }

    /**
     * Get the manufacturer id of the device who sent this packet
     *
     * @return the manId as a byte array
     * <p>
     * TODO: integer would be better?
     */
    public byte[] getManId() {
        return manId;
    }

    /**
     * Build the response to this packet instance, response could be one of
     * <ul>
     * <li><code>UTETeachInPacket.BIDIRECTIONAL_TEACH_IN_SUCCESSFUL</code></li>
     * <li><code>UTETeachInPacket.BIDIRECTIONAL_TEACH_IN_REFUSED</code></li>
     * <li>
     * <code>UTETeachInPacket.BIDIRECTIONAL_TEACH_IN_DELETION_ACCEPTED</code></li>
     * </ul>
     * <p>
     * the typical usage is as follows:<br/>
     * <p>
     * <pre>
     * //The teach-in request
     * UTETeachInPacket uteRequest = new UTETeachInPacket(rawPacket);
     *
     * //check if a response is required
     * if(uteRequest.isResponseRequired())
     * {
     * 		//teach-in request
     * 		if(uteRequest.isTeachInRequest())
     *        {
     * 			//check if the eep is available
     * 		if(this.isEEPAvailable(uteRequest.getEEP())
     *        {
     * 			//build the accept response
     * 			UTETeachInPacket response = uteRequest.buildResponse(UTETeachInPacket.BIDIRECTIONAL_TEACH_IN_SUCCESSFUL);
     *
     * 			//send the response
     *
     *        }
     *        }
     * }
     * </pre>
     *
     * @param response The response to send
     * @return the {@link UTETeachInTelegram} response packet.
     */
    public UTETeachInTelegram buildResponse(byte response) {

        // build the response packet
        byte[] payloadResp = new byte[13];

        // Rorg UTE
        payloadResp[0] = (byte) 0xD4;

        // Inverted order of bytes in the transmission
        payloadResp[1] = response;

        payloadResp[2] = payload[5];
        payloadResp[3] = payload[4];
        payloadResp[4] = payload[3];
        payloadResp[5] = payload[2];
        payloadResp[6] = payload[1];
        payloadResp[7] = payload[0];

        // address
        payloadResp[8] = (byte) 0x00;
        payloadResp[9] = (byte) 0xFF;
        payloadResp[10] = (byte) 0xFF;
        payloadResp[11] = (byte) 0xFF;

        // status
        payloadResp[12] = (byte) 0x00;

        // optional data
        byte[] opt = new byte[7];
        opt[0] = (byte) 0x03;
        opt[1] = address[0];//(byte) 0x00
        opt[2] = address[1];//(byte) 0x81
        opt[3] = address[2];//(byte) 0x2A
        opt[4] = address[3];//(byte) 0x90
        opt[5] = (byte) 0xFF;
        opt[6] = (byte) 0x00;

        // the low level packet
        ESP3Packet uteTeachInresponse = new ESP3Packet(ESP3Packet.RADIO, payloadResp, opt);
        // the UTE teach in packet
        UTETeachInTelegram responsePacket = new UTETeachInTelegram(uteTeachInresponse);

        // set the response flag
        responsePacket.setResponse(true);

        // the UTETeachinPacket
        return responsePacket;
    }

}
