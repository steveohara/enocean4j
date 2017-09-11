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
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26Current;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26Divisor;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26PowerFailure;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;

/**
 * Implementation of a single phase current clamp
 * @author sohara
 */
public class D23200 extends D232 {

    /**
     * Single channel CT Clamp
     */
    public D23200() {

        // add attributes, basically a switching one
        addChannelAttribute(0, new EEP26PowerFailure(false));
        addChannelAttribute(0, new EEP26Divisor(EEP26Divisor.X1));
        addChannelAttribute(0, new EEP26Current(4095));
    }

    @Override
    public boolean handleProfileUpdate(DeviceManager deviceManager, EEP26Telegram telegram, EnOceanDevice device) {
        return handleProfileUpdate(deviceManager, telegram, device, 1);
    }
}
