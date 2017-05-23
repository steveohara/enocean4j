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
package uk.co._4ng.enocean.eep.eep26.profiles.A5.A509;

import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEPAttribute;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26ConcentrationLinear;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26HumidityLinear;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26TemperatureLinear;
import uk.co._4ng.enocean.eep.eep26.profiles.AbstractEEP;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26TelegramType;
import uk.co._4ng.enocean.eep.eep26.telegram.FourBSTelegram;

/**
 * A class representing the A5-04 family of EnOcean Equipment Profiles
 * (Temperature and Humidity sensors).
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public abstract class A509 extends AbstractEEP {

    @Override
    public boolean handleProfileUpdate(DeviceManager deviceManager, EEP26Telegram telegram, EnOceanDevice device) {
        // success flag, initially false
        boolean success = false;

        // handle the telegram, as first cast it at the right type (or fail)
        if (telegram.getTelegramType() == EEP26TelegramType.FourBS) {

            // cast the telegram to handle to its real type
            FourBSTelegram profileUpdate = (FourBSTelegram) telegram;

            // get the packet payload
            byte[] payload = profileUpdate.getPayload();

            // wrap the payload as a temperature and humidity message
            A509CO2TemperatureAndHumidityMessage msg = new A509CO2TemperatureAndHumidityMessage(payload);

            // Figure out if we can use the temperature and humidity values

            EEPAttribute attrHumidity = getChannelAttribute(0, EEP26HumidityLinear.NAME);
            Integer humidity = msg.isHumidityAvailable() ? msg.getHumidity() : null;

            EEPAttribute attrTemp = getChannelAttribute(0, EEP26TemperatureLinear.NAME);
            Integer temp = msg.isHumidityAvailable() ? msg.getTemperature() : null;

            if (fireAttributeEvent(deviceManager, attrTemp, 0, telegram, device, temp)) {
                if (fireAttributeEvent(deviceManager, attrHumidity, 0, telegram, device, humidity)) {
                    if (fireAttributeEvent(deviceManager, getChannelAttribute(0, EEP26ConcentrationLinear.NAME), 0, telegram, device, msg.getConcentration())) {
                        success = true;
                    }
                }
            }
        }
        return success;
    }

}
