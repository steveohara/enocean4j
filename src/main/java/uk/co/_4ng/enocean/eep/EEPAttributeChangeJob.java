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
 * @author bonino
 */
public class EEPAttributeChangeJob implements Runnable {

    private List<EEPAttribute<?>> changedAttributes;
    private int channelId;
    private EEP26Telegram telegram;
    private EnOceanDevice device;
    private DeviceManager deviceManager;

    /**
     * Creates a list of attributes that need to be notified for the specific device and channel
     *
     * @param deviceManager    Device manager to use
     * @param changedAttributes List of attributes that have changed
     * @param channelId         The channel
     * @param telegram          The originating telegram
     * @param device            The target device
     */
    public EEPAttributeChangeJob(DeviceManager deviceManager, List<EEPAttribute<?>> changedAttributes, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
        this.deviceManager = deviceManager;
        this.changedAttributes = new ArrayList<>(changedAttributes);
        this.channelId = channelId;
        this.telegram = telegram;
        this.device = device;
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
        changedAttributes = new ArrayList<>();
        changedAttributes.add(changedAttribute);
        this.channelId = channelId;
        this.telegram = telegram;
        this.device = device;
    }

    public List<EEPAttribute<?>> getChangedAttributes() {
        return changedAttributes;
    }

    public int getChannelId() {
        return channelId;
    }

    public EEP26Telegram getTelegram() {
        return telegram;
    }

    public EnOceanDevice getDevice() {
        return device;
    }

    @Override
    public void run() {
        for (EEPAttribute<?> attribute : changedAttributes) {
            attribute.notifyAttributeListeners(channelId, telegram, device);
        }
        deviceManager.notifyDeviceValueListeners(this);
    }
}
