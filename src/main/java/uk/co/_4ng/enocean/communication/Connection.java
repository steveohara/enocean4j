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
package uk.co._4ng.enocean.communication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEP;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26TelegramFactory;
import uk.co._4ng.enocean.link.LinkLayer;
import uk.co._4ng.enocean.link.PacketListener;
import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;
import uk.co._4ng.enocean.protocol.serial.v3.network.packet.event.Event;
import uk.co._4ng.enocean.protocol.serial.v3.network.packet.radio.Radio;
import uk.co._4ng.enocean.protocol.serial.v3.network.packet.response.Response;
import uk.co._4ng.enocean.util.EnOceanUtils;

/**
 * The EnOcean for Java (EnJ) connection layer. It decouples link-level
 * communication and protocol management issues from the application logic.
 * Defines standard and "easy to use" methods for writing / reading data from an
 * EnOcean network.
 * <p>
 * It is typically built on top of an LinkLayer instance, e.g.:
 * <p>
 * <pre>
 * String serialId = &quot;/dev/tty0&quot;;
 *
 * LinkLayer link = new LinkLayer(serialId);
 *
 * Connection connection = new Connection(link);
 * </pre>
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * @author <a href="mailto:biasiandrea04@gmail.com">Andrea Biasi</a>
 */
public class Connection implements PacketListener {

    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

    // the wrapped link layer
    private LinkLayer linkLayer;
    private TeachInHandler teachIn;
    private DeviceManager deviceManager;

    /**
     * Build a connection layer instance on top of the given link layer
     * instance.
     *
     * @param linkLayer The {@link LinkLayer} instance upon which basing the connection
     *                  layer.
     */
    public Connection(LinkLayer linkLayer, DeviceManager deviceManager) {

        // store a reference to the link layer
        this.linkLayer = linkLayer;

        // store the device manager to use
        this.deviceManager = deviceManager;

        // add this connection layer as listener for incoming events
        this.linkLayer.addPacketListener(this);

        // Create the teach-in manager
        teachIn = new TeachInHandler(linkLayer);
    }

    /**
     * Sends the given payload encapsulated into a Radio message, automatically
     * adds sender address and status to the payload, thus completing the actual
     * payload being sent. By default the gateway address is fixed at 0x00ffffff
     * and the status byte at 0x00.
     *
     * @param address Address to send to
     * @param payload Payload to send
     */
    public void sendRadioCommand(byte[] address, byte[] payload) {
        // add sender address and status
        byte actualPayload[] = new byte[payload.length + 5];

        // copy the payload
        System.arraycopy(payload, 0, actualPayload, 0, payload.length);

        // sender address
        actualPayload[payload.length] = (byte) 0x00;
        actualPayload[payload.length + 1] = (byte) 0xFF;
        actualPayload[payload.length + 2] = (byte) 0xFF;
        actualPayload[payload.length + 3] = (byte) 0xFF;

        // status
        actualPayload[payload.length + 4] = (byte) 0x00;

        // build the link-layer packet and it
        linkLayer.send(Radio.getRadio(address, actualPayload, true));
    }

    /*
     * Handles packets received at the link layer
     */
    @Override
    public void handlePacket(ESP3Packet pkt) {
        // check if the packet is an asynchronous information coming from the
        // network or a response
        try {
            if (pkt.isRadio()) {
                handleRadioPacket(new Radio(pkt));
            }
            else if (pkt.isResponse()) {
                handleResponse(new Response(pkt));
            }
            else if (pkt.isEvent()) {
                handleEvent(new Event(pkt));
            }
        }
        catch (Exception e) {
            logger.warn("Error while handling received packet" + e);
        }
    }

    /**
     * Handles the packet and any notifications
     *
     * @param pkt Packet to interrogate
     */
    private void handleRadioPacket(Radio pkt) {

        logger.debug("Radio packet received: {}", pkt);
        EEP26Telegram telegram = EEP26TelegramFactory.getEEP26Telegram(pkt);
        if (telegram != null) {

            // We should only handle teach-in requests when in teach-in mode and if we don't
            // already have the device registered
            byte address[] = telegram.getAddress();
            EnOceanDevice device = deviceManager.getDevice(address);

            // Is this a teach-in request
            if (telegram.isTeachIn()) {
                teachIn.handle(telegram, pkt, device);
            }

            // Normal request so check if we have a device registered
            else if (device != null) {

                // delegate to the device
                EEP deviceEEP = device.getEEP();

                // check not null
                if (deviceEEP != null) {
                    if (!deviceEEP.handleUpdate(deviceManager, telegram, device)) {
                        logger.warn("Profile update for {} was not handled successfully", EnOceanUtils.toHexString(device.getAddress()));
                    }
                }
                else {
                    logger.warn("No suitable EEP found for the given device {}", device);
                }
            }
        }
    }

    /**
     * Default response handler
     *
     * @param pkt Response packet
     */
    private void handleResponse(Response pkt) {
        logger.debug("Received response: {}", pkt.toString());
    }

    /**
     * Default event handler
     *
     * @param pkt Event packet
     */
    private void handleEvent(Event pkt) {
        logger.debug("Received event: {}", pkt.toString());
    }

    /**
     * Returns the teach-in manager used by the connection
     * @return Teach in manager
     */
    public TeachInHandler getTeachIn() {
        return teachIn;
    }

    /**
     * Returns the device manager for the connection
     * @return Device manager
     */
    public DeviceManager getDeviceManager() {
        return deviceManager;
    }
}