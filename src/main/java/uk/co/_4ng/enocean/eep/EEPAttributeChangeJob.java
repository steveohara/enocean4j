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
package uk.co._4ng.enocean.eep;

import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple way of launching asynchronous notifications
 *
 * @author sohara
 */
public class EEPAttributeChangeJob implements Runnable {

    private DeviceManager deviceManager;
    private List<EEPAttributeChange> changes = new ArrayList<>();

    /**
     * Creates an empty list of attributes that need to be notified for the specific device and channel
     *
     * @param deviceManager Device manager to use
     */
    public EEPAttributeChangeJob(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    /**
     * Creates a list of attributes that need to be notified for the specific device and channel
     *
     * @param deviceManager    Device manager to use
     * @param changedAttribute Attributes that has changed
     * @param channelId        The channel
     * @param telegram         The originating telegram
     * @param device           The target device
     */
    public EEPAttributeChangeJob(DeviceManager deviceManager, EEPAttribute<?> changedAttribute, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
        this.deviceManager = deviceManager;
        changes.add(new EEPAttributeChange(changedAttribute, channelId, telegram, device));
    }

    /**
     * Creates a list of attributes that need to be notified for the specific device and channel
     *
     * @param deviceManager     Device manager to use
     * @param changedAttributes List of attributes that has changed for this channel
     * @param channelId         The channel
     * @param telegram          The originating telegram
     * @param device            The target device
     */
    public EEPAttributeChangeJob(DeviceManager deviceManager, List<EEPAttribute<?>> changedAttributes, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
        this.deviceManager = deviceManager;
        for (EEPAttribute<?> changedAttribute : changedAttributes) {
            changes.add(new EEPAttributeChange(changedAttribute, channelId, telegram, device));
        }
    }

    /**
     * Adds a changed attribute to the list of notifications
     *
     * @param changedAttribute Attributes that has changed
     * @param channelId        The channel
     * @param telegram         The originating telegram
     * @param device           The target device
     */
    public void addChangedAttribute(EEPAttribute<?> changedAttribute, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
        changes.add(new EEPAttributeChange(changedAttribute, channelId, telegram, device));
    }

    /**
     * Returns the list of changes
     * @return List of changes
     */
    public List<EEPAttributeChange> getChanges() {
        return new ArrayList<>(changes);
    }

    @Override
    public void run() {
        for (EEPAttributeChange attribute : changes) {
            attribute.notifyAttributeListeners();
        }
        deviceManager.notifyDeviceValueListeners(this);
    }

    /**
     * A class that encapsulates the attribute change
     */
    public class EEPAttributeChange {
        private EEPAttribute<?> changedAttribute;
        private int channelId;
        private EEP26Telegram telegram;
        private EnOceanDevice device;

        /**
         * Constructs an attribute change with all the required bits
         *
         * @param changedAttribute Attribute that has changed
         * @param channelId        The channel ID
         * @param telegram         The originating telegram
         * @param device           The device associated with the telegram
         */
        private EEPAttributeChange(EEPAttribute<?> changedAttribute, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
            this.changedAttribute = changedAttribute;
            this.channelId = channelId;
            this.telegram = telegram;
            this.device = device;
        }

        /**
         * Notifies any attribute listeners
         */
        private void notifyAttributeListeners() {
            changedAttribute.notifyAttributeListeners(channelId, telegram, device);
        }

        /**
         * Returns the changed attribute
         *
         * @return Changed attribute
         */
        public EEPAttribute<?> getAttribute() {
            return changedAttribute;
        }

        /**
         * Returns the associated channel
         *
         * @return Channel
         */
        public int getChannelId() {
            return channelId;
        }

        /**
         * Returns the originating telegram
         *
         * @return Telegram
         */
        public EEP26Telegram getTelegram() {
            return telegram;
        }

        /**
         * Returns the registered device
         *
         * @return Device
         */
        public EnOceanDevice getDevice() {
            return device;
        }
    }
}
