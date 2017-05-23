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
import uk.co._4ng.enocean.eep.EEPIdentifier;
import uk.co._4ng.enocean.eep.eep26.EEPRegistry;
import uk.co._4ng.enocean.eep.eep26.telegram.*;
import uk.co._4ng.enocean.link.LinkLayer;
import uk.co._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;
import uk.co._4ng.enocean.protocol.serial.v3.network.packet.radio.Radio;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A class to delegate the teach-in handling to- stops the Connection class getting crowded
 */
public class TeachInHandler {

    private static final Logger logger = LoggerFactory.getLogger(TeachInHandler.class);

    private LinkLayer linkLayer;

    // the set of device listeners to keep updated about device events
    private Set<TeachInListener> teachInListeners;

    // the teach-in flag
    private boolean teachInEnabled;

    // the teach in timer
    private Timer teachInTimer;

    /**
     * Build a connection layer instance on top of the given link layer
     * instance.
     *
     * @param linkLayer The {@link LinkLayer} instance upon which basing the connection
     *                  layer.
     */
    TeachInHandler(LinkLayer linkLayer) {

        // store a reference to the link layer
        this.linkLayer = linkLayer;

        //initialize the set of teach-in listeners
        teachInListeners = new HashSet<>();

        // initialize the teachIn flag at false
        teachInEnabled = false;

        // initialize the teach in timer
        teachInTimer = new Timer();

    }

    /**
     * Adds a teach in listener to the set of listeners to be notified about
     * teach-in status
     *
     * @param listener filename * The {@link DeviceListener} to notify to.
     */
    public void addTeachInListener(TeachInListener listener) {
        teachInListeners.add(listener);
    }

    /**
     * Removes a teach-in listener from the set of listeners to be notified about
     * teach-in events.
     *
     * @param listener The {@link DeviceListener} to remove.
     * @return true if removal was successful, false, otherwise.
     */
    public boolean removeTeachInListener(TeachInListener listener) {
        return teachInListeners.remove(listener);
    }

    /**
     * Enables the teach in procedure, it forces the connection layer to listen
     * for teach-in requests coming from the physical network. whenever a new
     * teach-in request is detected, a device recognition process is started
     * enabling access to the newly discovered device.
     * <p>
     * New devices are transfered to the next layer by means of a listener
     * mechanism.
     *
     * @param teachInTime the maximum time for which the connection layer will accept
     *                    teach in requests.
     */
    public void enableTeachIn(int teachInTime) {
        if (!teachInEnabled) {
            teachInEnabled = true;

            // start the teach in reset timer
            teachInTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    disableTeachIn();
                }
            }, teachInTime);
            logger.debug("Teach-in enabled for {} milliseconds", teachInTime);

            //notify listeners
            for (TeachInListener listener : teachInListeners) {
                listener.teachInEnabled();
            }
        }
    }

    /**
     * Checks if the connection layer is currently accepting teach-in requests
     * or not
     *
     * @return the teachIn true if the connection layer is accepting teach-in
     * requests, false otherwise.
     */
    public boolean isTeachInEnabled() {
        return teachInEnabled;
    }

    /**
     * Disables the teach mode on the connection layer. Teach-in requests are
     * handlePacket * ignored.
     */
    public synchronized void disableTeachIn() {
        // stop any pending timer
        teachInTimer.purge();

        // disable the teach in procedure
        teachInEnabled = false;
        logger.debug("Teach-in disabled");

        //notify listeners
        for (TeachInListener listener : teachInListeners) {
            listener.teachInDisabled();
        }
    }

    /**
     * Informs the listeners we got a new device
     * @param device Found device
     */
    private void informNewDeviceFound(EnOceanDevice device) {
        //notify listeners
        for (TeachInListener listener : teachInListeners) {
            listener.foundNewDevice(device);
        }
    }

    /**
     * Informs the listeners we got a registered device
     * @param device Found device
     */
    private void informRegisteredDeviceFound(EnOceanDevice device) {
        //notify listeners
        for (TeachInListener listener : teachInListeners) {
            listener.foundRegisteredDevice(device);
        }
    }

    /**
     * Handles the UTE teach-in process, it can either result in a new device
     * being added, in a teach-in procedure disabling or it could just refuse
     * the physical teach-in request if not supported.
     *
     * @param pkt The teach-in request packet
     */
    private void handleUTETeachIn(UTETeachInTelegram pkt) {
        // the possible response to send back to the transceiver
        UTETeachInTelegram response = null;

        // check the packet type
        if (pkt.isTeachInRequest()) {
            // check the eep
            if (EEPRegistry.isEEPSupported(pkt.getEEP())) {

                // build the response packet
                response = pkt.buildResponse(UTETeachInTelegram.BIDIRECTIONAL_TEACH_IN_SUCCESSFUL);

                // build the device
                informNewDeviceFound(DeviceManager.createDevice(pkt.getAddress(), pkt.getManId(), pkt.getEEP()));
            }
            // build the response packet
            else {
                response = pkt.buildResponse(UTETeachInTelegram.BIDIRECTIONAL_TEACH_IN_REFUSED);
            }
        }
        else if (pkt.isTeachInDeletionRequest()) {
            // stop the learning process
            disableTeachIn();

            // check if response is required
            // build the response packet
            response = pkt.buildResponse(UTETeachInTelegram.BIDIRECTIONAL_TEACH_IN_DELETION_ACCEPTED);
        }
        else if (pkt.isNotSpecifiedTeachIn()) {
            // currently not supported
            response = pkt.buildResponse(UTETeachInTelegram.BIDIRECTIONAL_TEACH_IN_REFUSED);
        }

        if (pkt.isResponseRequired() && response != null && response.isResponse()) {
            // send the packet back to the transceiver, with high
            // priority as a maximum 500ms latency is allowed.
            linkLayer.send(response.getRawPacket(), true);
        }
    }

    /**
     * Handles the teach-in process for UTE type telegrams
     *
     * @param pkt Packet to use
     */
    private void handleRPSTeachIn(ESP3Packet pkt) {

        // parse the packet
        RPSTelegram rpsTelegram = new RPSTelegram(pkt);

        // the only teach-in procedure supported by the EEP2.6 specification
        // is direct and explicit teach-in, meaning that the device to
        // teach-in and the corresponding profile are known in advance.
        // Since this assumption may sometimes be a little bit limiting, a
        // mechanism for enabling implicit teach-in is also provided. This
        // latter option, however always matches 2 rocker switches as there
        // is no way to distinguish between the two EEPs by just looking at
        // the packet content.

        informNewDeviceFound(DeviceManager.createDevice(rpsTelegram.getAddress(), null, new EEPIdentifier(0xf6, 2, 1)));
    }

    /**
     * Handles the teach-in process for a single byte 1BS telegram
     *
     * @param pkt Packet to use
     */
    private void handle1BSTeachIn(ESP3Packet pkt) {

        // parse the packet
        OneBSTelegram oneBSTelegram = new OneBSTelegram(pkt);

        // the only teach-in procedure supported by the EEP2.6 specification
        // is direct and explicit teach-in, meaning that the device to
        // teach-in and the corresponding profile are known in advance.
        // Since this assumption may sometimes be a little bit limiting, a
        // mechanism for enabling implicit teach-in is also provided. This
        // latter option, matches the only device defined by the
        // specification i.e., the D5-00-01 Contact Switch

        informNewDeviceFound(DeviceManager.createDevice(oneBSTelegram.getAddress(), null, new EEPIdentifier(0xd5, 0, 1)));
    }

    /**
     * Handles the teach-in process for a single byte 1BS telegram
     *
     * @param pkt Packet to use
     */
    private void handle4BSTeachIn(ESP3Packet pkt) {
        // parse the packet
        FourBSTelegram bs4Telegram = new FourBSTelegram(pkt);

        // check if the received packet is teach in
        if (FourBSTeachInTelegram.isTeachIn(bs4Telegram)) {

            // wrap the telegram
            FourBSTeachInTelegram bs4TeachInTelegram = new FourBSTeachInTelegram(bs4Telegram);

            // --------- Teach-in variation 2 ------
            if (bs4TeachInTelegram.isWithEEP()) {

                // build a new 4BS device,
                informNewDeviceFound(DeviceManager.createDevice(bs4TeachInTelegram.getAddress(), bs4TeachInTelegram.getManId(), new EEPIdentifier(bs4TeachInTelegram.getRorg(), bs4TeachInTelegram.getEEPFunc(), bs4TeachInTelegram.getEEPType())));
            }
            else {
                logger.warn("Neither implicit or explicit learn succeeded; bi-directional teach-in currently not supported for 4BS telegrams.");

                // log the address
                StringBuilder msg = new StringBuilder();
                for (int i = 0; i < 4; i++) {
                    msg.append(String.format("%02x", bs4TeachInTelegram.getAddress()[i]));
                }
                logger.debug("Device address:" + msg);
            }
        }
    }

    /**
     * Handles all the possible teach requests
     * @param telegram Telegram forming the request
     * @param pkt Packet from the telegram
     */
    void handle(EEP26Telegram telegram, Radio pkt, EnOceanDevice device) {

        // If don't have a device
        if (device == null) {

            // Are we enabled?
            if (isTeachInEnabled()) {

                // Check th type of teach-in
                if (telegram.getTelegramType() == EEP26TelegramType.UTETeachIn) {
                    handleUTETeachIn((UTETeachInTelegram) telegram);
                }
                else if (RPSTelegram.isRPSPacket(pkt)) {
                    handleRPSTeachIn(pkt);
                }

                // handle 1BS telegrams (much similar to RPS)
                else if (OneBSTelegram.is1BSPacket(pkt)) {
                    handle1BSTeachIn(pkt);
                }

                // handle 3 variations of 4BS teach in: explicit
                // with application-specified EEP, explicit with
                // device-specified EEP or bi-directional.
                else if (FourBSTelegram.is4BSPacket(pkt)) {
                    handle4BSTeachIn(pkt);
                }
                else {
                    logger.error("Unknown packet type: {}", pkt);
                }
            }
            else {
                logger.warn("Ignoring teach-in requests whilst not in teach-in mode");
            }
        }
        else {
            logger.debug("Ignoring teach-in request for device {} already registered", device.toString());
            informRegisteredDeviceFound(device);
        }
    }
}