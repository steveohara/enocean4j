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

    /**
     * Creates a list of attributes that need to be notified for the specific device and channel
     *
     * @param changedAttributes List of attributes that have changed
     * @param channelId         The channel
     * @param telegram          The originating telegram
     * @param device            The target device
     */
    public EEPAttributeChangeJob(List<EEPAttribute<?>> changedAttributes, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
        this.changedAttributes = new ArrayList<>(changedAttributes);
        this.channelId = channelId;
        this.telegram = telegram;
        this.device = device;
    }

    /**
     * Creates a list of attributes that need to be notified for the specific device and channel
     *
     * @param changedAttribute Attributes that has changed
     * @param channelId        The channel
     * @param telegram         The originating telegram
     * @param device           The target device
     */
    public EEPAttributeChangeJob(EEPAttribute<?> changedAttribute, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
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
        DeviceManager.notifyDeviceValueListeners(this);
    }
}
