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
package uk.co._4ng.enocean.eep.eep26.profiles.F6.F602;

import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEPAttribute;
import uk.co._4ng.enocean.eep.EEPAttributeChangeJob;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26RockerSwitch2RockerAction;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26RockerSwitch2RockerButtonCount;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26RockerSwitch2RockerEnergyBow;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26TelegramType;
import uk.co._4ng.enocean.eep.eep26.telegram.RPSTelegram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class F60201 extends F602 {

    private static final int CHANNEL_1 = 0;
    private static final int CHANNEL_2 = 1;

    /**
     *
     */
    public F60201() {

        // add attributes
        addChannelAttribute(CHANNEL_1, new EEP26RockerSwitch2RockerAction());
        addChannelAttribute(CHANNEL_2, new EEP26RockerSwitch2RockerAction());
        addChannelAttribute(CHANNEL_1, new EEP26RockerSwitch2RockerButtonCount());
        addChannelAttribute(CHANNEL_1, new EEP26RockerSwitch2RockerEnergyBow());
    }

    @Override
    public boolean handleProfileUpdate(DeviceManager deviceManager, EEP26Telegram telegram, EnOceanDevice device) {
        boolean success = false;

        // handle the telegram, as first cast it at the right type (or fail)
        if (telegram.getTelegramType() == EEP26TelegramType.RPS) {

            // cast the telegram to handle to its real type
            RPSTelegram profileUpdate = (RPSTelegram) telegram;

            // get the packet payload
            byte[] payload = profileUpdate.getPayload();

            // parse the F602 data payload
            F6020102RockerSwitchMessage message = new F6020102RockerSwitchMessage(payload, profileUpdate.getStatus());
            if (message.isValid()) {

                Map<Integer, List<EEPAttribute<?>>> attrs = new HashMap<>();
                attrs.put(CHANNEL_1, new ArrayList<EEPAttribute<?>>());
                attrs.put(CHANNEL_2, new ArrayList<EEPAttribute<?>>());

                // Action message
                if (message.isActionMessage()) {

                    // update the rocker switch attribute
                    // shortcut used here: channel0 = action1, channel1 =
                    // action2
                    EEP26RockerSwitch2RockerAction rockerSwitchAttribute1 = (EEP26RockerSwitch2RockerAction) getChannelAttribute(CHANNEL_1, EEP26RockerSwitch2RockerAction.NAME);
                    if (rockerSwitchAttribute1 != null) {
                        addAttributes(attrs, rockerSwitchAttribute1, message.getButtonActions1(), CHANNEL_1);
                    }

                    // if action2 is enabled
                    EEP26RockerSwitch2RockerAction rockerSwitchAttribute2 = (EEP26RockerSwitch2RockerAction) getChannelAttribute(CHANNEL_2, EEP26RockerSwitch2RockerAction.NAME);
                    if (message.isAction2Enabled() && rockerSwitchAttribute2 != null) {
                        addAttributes(attrs, rockerSwitchAttribute2, message.getButtonActions2(), CHANNEL_2);
                    }

                }
                else {
                    // get the number of buttons attribute
                    EEP26RockerSwitch2RockerButtonCount btnCountAttribute = (EEP26RockerSwitch2RockerButtonCount) getChannelAttribute(CHANNEL_1, EEP26RockerSwitch2RockerButtonCount.NAME);
                    if (btnCountAttribute != null) {
                        btnCountAttribute.setValue(message.getnButtonsPressed());
                        attrs.get(CHANNEL_1).add(btnCountAttribute);
                    }
                }

                // handle energy bow (common to all messages)
                EEP26RockerSwitch2RockerEnergyBow energyBowAttribute = (EEP26RockerSwitch2RockerEnergyBow) getChannelAttribute(CHANNEL_1, EEP26RockerSwitch2RockerEnergyBow.NAME);
                if (energyBowAttribute != null) {
                    energyBowAttribute.setValue(message.isEnergyBowPressed());
                    attrs.get(CHANNEL_1).add(energyBowAttribute);
                }

                // Send the data to the listeners
                diapatchJobs(deviceManager, attrs, telegram, device);

                //if comes here everything is fine
                success = true;
            }
        }
        return success;
    }

    /**
     * Dispatches a change event on a specific channel
     *
     * @param attrs                 Map of channels attribute lists
     * @param rockerSwitchAttribute Rocket switch attributes
     * @param buttonActions         Button actions
     * @param channel               Channel
     */
    private void addAttributes(Map<Integer, List<EEPAttribute<?>>> attrs, EEP26RockerSwitch2RockerAction rockerSwitchAttribute, boolean[] buttonActions, int channel) {
        rockerSwitchAttribute.setButtonValue(EEP26RockerSwitch2RockerAction.AO, buttonActions[EEP26RockerSwitch2RockerAction.AO]);
        rockerSwitchAttribute.setButtonValue(EEP26RockerSwitch2RockerAction.AI, buttonActions[EEP26RockerSwitch2RockerAction.AI]);
        rockerSwitchAttribute.setButtonValue(EEP26RockerSwitch2RockerAction.BO, buttonActions[EEP26RockerSwitch2RockerAction.BO]);
        rockerSwitchAttribute.setButtonValue(EEP26RockerSwitch2RockerAction.BI, buttonActions[EEP26RockerSwitch2RockerAction.BI]);
        attrs.get(channel).add(rockerSwitchAttribute);
    }

    /**
     * Dispatches a change event on a specific channel
     *
     * @param telegram Telegram
     * @param device   Device
     */
    private void diapatchJobs(DeviceManager deviceManager, Map<Integer, List<EEPAttribute<?>>> attrs, EEP26Telegram telegram, EnOceanDevice device) {

        EEPAttributeChangeJob dispatcherTask = new EEPAttributeChangeJob(deviceManager);
        for (Entry<Integer, List<EEPAttribute<?>>> channel : attrs.entrySet()) {

            // build the dispatching task
            for (EEPAttribute<?> attr : channel.getValue()) {
                dispatcherTask.addChangedAttribute(attr, channel.getKey(), telegram, device);
            }
        }

        // submit the task for execution
        attributeNotificationWorker.submit(dispatcherTask);
    }
}
