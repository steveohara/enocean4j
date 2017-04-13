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
package com._4ng.enocean.communication;

import com._4ng.enocean.communication.timing.tasks.CancelTeachInTask;
import com._4ng.enocean.devices.DeviceManager;
import com._4ng.enocean.devices.EnOceanDevice;
import com._4ng.enocean.eep.EEP;
import com._4ng.enocean.eep.EEPIdentifier;
import com._4ng.enocean.eep.eep26.profiles.D5.D500.D50001;
import com._4ng.enocean.eep.eep26.profiles.F6.F602.F60201;
import com._4ng.enocean.eep.eep26.telegram.*;
import com._4ng.enocean.link.LinkLayer;
import com._4ng.enocean.link.PacketListener;
import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;
import com._4ng.enocean.protocol.serial.v3.network.packet.event.Event;
import com._4ng.enocean.protocol.serial.v3.network.packet.radio.Radio;
import com._4ng.enocean.protocol.serial.v3.network.packet.response.Response;
import com._4ng.enocean.util.EnOceanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    // the default teach-in timeout in milliseconds
    public static final int TEACH_IN_TIME = 20000;
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);
    // the wrapped link layer
    private LinkLayer linkLayer;
    // the set of device listeners to keep updated about device events
    private Set<TeachInListener> teachInListeners;

    // ------------- TEACH IN -----------------
    // the executor service to run device update tasks
    private ExecutorService deviceUpdateDeliveryExecutor;
    // the teach-in flag
    private boolean teachIn;

    // the teach in timer
    private Timer teachInTimer;

    // the device to teach-in
    private EnOceanDevice deviceToTeachIn;

    // a flag for enabling / disabling smart (non-standard) teach-in for a
    // selected subset of telegrams (RPS and 1BS)
    private boolean smartTeachIn;

    /**
     * Build a connection layer instance on top of the given link layer
     * instance.
     *
     * @param linkLayer The {@link LinkLayer} instance upon which basing the connection
     *                  layer.
     */
    public Connection(LinkLayer linkLayer) {

        //initialize the set of teach-in listeners
        teachInListeners = new HashSet<>();

        // initialize the update delivery executor
        deviceUpdateDeliveryExecutor = Executors.newCachedThreadPool();

        // initialize the teachIn flag at false
        teachIn = false;
        deviceToTeachIn = null;

        // set the smart teach-in at false by default
        smartTeachIn = false;

        // initialize the teach in timer
        teachInTimer = new Timer();

        // store a reference to the link layer
        this.linkLayer = linkLayer;

        // add this connection layer as listener for incoming events
        this.linkLayer.addPacketListener(this);
    }

    /**
     * Adds a teach in listener to the set of listeners to be notified about
     * teach-in status
     *
     * @param listener filename * The {@link DeviceListener} to notify to.
     */
    public void addTeachInListener(TeachInListener listener) {
        // store the listener in the set of currently active listeners
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
     * <p>
     * The teach in procedure lasts for a time equal to the default
     * <code>Connection.TEACH_IN_TIME</code>
     */
    public void enableTeachIn() {
        // start reset timer
        enableTeachIn(TEACH_IN_TIME);
    }

    public void enableTeachIn(String hexDeviceAddress, String eepIdentifierAsString) {
        enableTeachIn(hexDeviceAddress, eepIdentifierAsString, TEACH_IN_TIME);
    }

    public void enableTeachIn(String hexDeviceAddress, String eepIdentifierAsString, int time) {
        if (hexDeviceAddress != null && !hexDeviceAddress.isEmpty() && eepIdentifierAsString != null && !eepIdentifierAsString.isEmpty()) {
            // convert - parse strings to corresponding data

            // parse the identifier
            EEPIdentifier eep = EEPIdentifier.parse(eepIdentifierAsString);
            byte address[] = EnOceanDevice.parseAddress(hexDeviceAddress);
            EnOceanDevice device = new EnOceanDevice(address, null);
            device.setEEP(DeviceManager.getEEP(eep));

            // store the device to learn
            deviceToTeachIn = device;

            // start reset timer
            enableTeachIn(time);
        }
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
        if (!teachIn) {
            teachIn = true;

            // start the teach in reset timer
            teachInTimer.schedule(new CancelTeachInTask(this), teachInTime);
            logger.info("Teach-in enabled for {} milliseconds", teachInTime);

            //notify listeners
            for (TeachInListener listener : teachInListeners) {
                listener.teachInEnabled(isSmartTeachInEnabled());
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
        return teachIn;
    }

    /**
     * Disables the teach mode on the connection layer. Teach-in requests are
     * handlePacket * ignored.
     */
    public synchronized void disableTeachIn() {
        // stop any pending timer
        teachInTimer.purge();

        // disable the teach in procedure
        teachIn = false;
        deviceToTeachIn = null;
        logger.info("Teach-in disabled");

        //notify listeners
        for (TeachInListener listener : teachInListeners) {
            listener.teachInDisabled();
        }
    }

    /**
     * Sends the given payload encapsulated into a Radio message, automatically
     * adds sender address and status to the payload, thus completing the actual
     * payload being sent. By default the gateway address is fixed at 0x00ffffff
     * and the status byte at 0x00.
     *
     * @param address
     * @param payload
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

        // build the link-layer packet
        Radio enjLinkPacket = Radio.getRadio(address, actualPayload, true);

        // send the packet
        linkLayer.send(enjLinkPacket);
    }

    /**
     * @return the smartTeachIn
     */
    public boolean isSmartTeachInEnabled() {
        return smartTeachIn;
    }

    /**
     * @param smartTeachIn the smartTeachIn to set
     */
    public void setSmartTeachIn(boolean smartTeachIn) {
        this.smartTeachIn = smartTeachIn;
    }

    @Override
    /*
     * Handles packets received at the link layer
     */ public void handlePacket(ESP3Packet pkt) {
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

    private void handleRadioPacket(Radio pkt) {

        logger.debug("Radio packet received: {}", pkt);

        EEP26Telegram telegram = EEP26TelegramFactory.getEEP26Telegram(pkt);

        if (telegram != null) {

            if (teachIn && telegram.getTelegramType() == EEP26TelegramType.UTETeachIn) {
                handleUTETeachIn((UTETeachInTelegram) telegram);
            }
            else {
                // get the sender id, i.e., the address of the device
                // generating the packet
                byte address[] = telegram.getAddress();

                // get the corresponding device...
                EnOceanDevice device = DeviceManager.getDevice(address);

                // check null
                if (device == null) {

                    // the device has never been seen before,
                    // therefore the device must be learned, either
                    // implicitly or explicitly

                    // handle RPS teach-in, can either be done
                    // implicitly, an F60201 EEP will be used, or explicitly if teachIn
                    // is true and the device to teach in has been completely
                    // specified.
                    if (RPSTelegram.isRPSPacket(pkt)) {
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

                    // Unknown packet type
                    else {
                        logger.info("Unknown packet type: {}", pkt);
                    }
                }
                else {
                    // the device is already known therefore message handling
                    // can be delegated

                    // delegate to the device
                    EEP deviceEEP = device.getEEP();

                    // check not null
                    if (deviceEEP != null) {
                        if (!deviceEEP.handleUpdate(telegram, device)) {
                            logger.warn("Profile update for {} was not handled successfully", EnOceanUtils.toHexString(device.getAddress()));
                        }
                    }
                    else {
                        logger.warn("No suitable EEP found for the given device... {}", device);
                    }
                }
            }
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
            if (DeviceManager.isEEPSupported(pkt.getEEP())) {
                // build the response packet
                response = pkt.buildResponse(UTETeachInTelegram.BIDIRECTIONAL_TEACH_IN_SUCCESSFUL);

                // build the device
                DeviceManager.registerDevice(pkt.getAddress(), pkt.getManId(), pkt.getEEP());
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

    private EnOceanDevice handleRPSTeachIn(ESP3Packet pkt) {
        // parse the packet
        RPSTelegram rpsTelegram = new RPSTelegram(pkt);

        // initially null
        EnOceanDevice device = null;

        // actually everything shall be ignored unless teach-in is
        // enabled
        if (teachIn) {

            // the only teach-in procedure supported by the EEP2.6 specification
            // is direct and explicit teach-in, meaning that the device to
            // teach-in and the corresponding profile are known in advance.
            // Since this assumption may sometimes be a little bit limiting, a
            // mechanism for enabling implicit teach-in is also provided. This
            // latter option, however always matches 2 rocker switches as there
            // is no way to distinguish between the two EEPs by just looking at
            // the packet content.
            if (deviceToTeachIn != null) {
                device = explicitTeachIn(rpsTelegram);
            }
            else if (smartTeachIn) {
                // build a new RPS device
                device = DeviceManager.registerDevice(rpsTelegram.getAddress(), null, new EEPIdentifier(F60201.RORG, F60201.FUNC, F60201.TYPE));
            }
        }
        else {
            logger.debug("Teach-in is disabled so telegram ignored: {}", pkt);
        }
        return device;
    }

    private EnOceanDevice handle1BSTeachIn(ESP3Packet pkt) {
        // parse the packet
        OneBSTelegram oneBSTelegram = new OneBSTelegram(pkt);

        // initially null
        EnOceanDevice device = null;

        // actually everything shall be ignored unless teach-in is
        // enabled
        if (teachIn) {

            // the only teach-in procedure supported by the EEP2.6 specification
            // is direct and explicit tech-in, meaning that the device to
            // teach-in and the corresponding profile are known in advance.
            // Since this assumption may sometimes be a little bit limiting, a
            // mechanism for enabling implicit teach-in is also provided. This
            // latter option, matches the only device defined by the
            // specification i.e., the D5-00-01 Contact Switch
            if (deviceToTeachIn != null) {
                device = explicitTeachIn(oneBSTelegram);
            }
            else if (smartTeachIn) {

                // build a new RPS device,
                device = DeviceManager.registerDevice(oneBSTelegram.getAddress(), null, new EEPIdentifier(D50001.RORG, D50001.FUNC, D50001.TYPE));
            }
        }
        else {
            logger.debug("Teach-in is disabled so telegram ignored: {}", pkt);
        }
        return device;
    }

    private EnOceanDevice handle4BSTeachIn(ESP3Packet pkt) {
        // parse the packet
        FourBSTelegram bs4Telegram = new FourBSTelegram(pkt);

        // prepare the device to return
        EnOceanDevice device = null;

        // actually everything shall be ignored unless teach-in is
        // enabled
        if (teachIn) {
            // check if the received packet is teach in
            if (FourBSTeachInTelegram.isTeachIn(bs4Telegram)) {
                // wrap the telegram
                FourBSTeachInTelegram bs4TeachInTelegram = new FourBSTeachInTelegram(bs4Telegram);

                // --------- Teach-in variation 2 ------
                if (bs4TeachInTelegram.isWithEEP()) {
                    // build a new 4BS device,
                    device = DeviceManager.registerDevice(bs4TeachInTelegram.getAddress(), bs4TeachInTelegram.getManId(), new EEPIdentifier(bs4TeachInTelegram.getRorg(), bs4TeachInTelegram.getEEPFunc(), bs4TeachInTelegram.getEEPType()));
                }
                else if (deviceToTeachIn != null) {
                    device = explicitTeachIn(bs4TeachInTelegram);
                }
                else {
                    // log not supported
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
        else {
            logger.debug("Teach-in is disabled so telegram ignored: {}", pkt);
        }
        return device;
    }

    private EnOceanDevice explicitTeachIn(EEP26Telegram telegram) {
        // device initially null
        EnOceanDevice device = null;

        if (logger.isDebugEnabled()) {
            // dump the address of the device to be taught in
            StringBuilder msg = new StringBuilder("toTeachIn: ");
            for (int i = 0; i < 4; i++) {
                msg.append(String.format("%02x", deviceToTeachIn.getAddress()[i]));
            }
            logger.debug(msg.toString());

            // dump the address of the device sending the data message
            msg = new StringBuilder("received: ");
            for (int i = 0; i < 4; i++) {
                msg.append(String.format("%02x", telegram.getAddress()[i]));
            }

            // write the debug information in the log
            logger.debug(msg.toString());
        }

        // check if the address of the device and the address
        // of the telegram match
        if (Arrays.equals(deviceToTeachIn.getAddress(), telegram.getAddress())) {

            // store the device
            DeviceManager.registerDevice(deviceToTeachIn);

            // set the device learnt
            device = deviceToTeachIn;

            // reset the device to teach in
            deviceToTeachIn = null;
        }

        return device;
    }

    private void handleResponse(Response pkt) {
        logger.info("Received response: {}", pkt.toString());
    }

    private void handleEvent(Event pkt) {
        logger.debug("Received event: {}", pkt.toString());
    }

}