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
package uk.co._4ng.enocean.eep.eep26.profiles.D2.D232;

import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEPAttributeChangeJob;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26Current;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26Divisor;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26PowerFailure;
import uk.co._4ng.enocean.eep.eep26.profiles.AbstractEEP;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26TelegramType;
import uk.co._4ng.enocean.eep.eep26.telegram.VLDTelegram;

/**
 * A class representing the D2-32 Family of EnOcean profiles. In EEP26 specifies
 * only 3 profiles for this EEP type that are all Current clamps.
 *
 * @author sohara
 */
abstract class D232 extends AbstractEEP {

    /**
     * Handles updates received from a telegram
     *
     * @param deviceManager Device manager
     * @param telegram      Originating telegram
     * @param device        Device associated with the change
     * @param channels      Number of phases
     * @return True if the telegram was handled successfully
     */
    boolean handleProfileUpdate(DeviceManager deviceManager, EEP26Telegram telegram, EnOceanDevice device, int channels) {
        boolean success = false;

        // handle the telegram, as first cast it at the right type (or fail)
        if (telegram.getTelegramType() == EEP26TelegramType.VLD) {

            // cast the telegram to handle to its real type
            VLDTelegram profileUpdate = (VLDTelegram) telegram;

            D232Message msg = new D232Message(profileUpdate.getPayload(), channels);
            if (msg.isValid()) {

                EEPAttributeChangeJob changes = new EEPAttributeChangeJob(deviceManager);

                EEP26Divisor divisor = (EEP26Divisor) getChannelAttribute(0, EEP26Divisor.NAME);
                divisor.setValue(msg.getDivisor());
                changes.addChangedAttribute(divisor, 0, telegram, device);

                EEP26PowerFailure powerFailed = (EEP26PowerFailure) getChannelAttribute(0, EEP26PowerFailure.NAME);
                powerFailed.setValue(msg.hasPowerFailed());
                changes.addChangedAttribute(powerFailed, 0, telegram, device);

                // Work out the phases
                for (int channel = 0; channel < channels; channel++) {
                    EEP26Current phase = (EEP26Current) getChannelAttribute(channel, EEP26Current.NAME);
                    phase.setValue(msg.getScaledValue(channel));
                    changes.addChangedAttribute(phase, channel, telegram, device);
                }

                // submit the task for execution
                attributeNotificationWorker.submit(changes);
                success = true;
            }
        }

        return success;
    }

}
