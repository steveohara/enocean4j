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
package uk.co._4ng.enocean.eep.eep26.profiles.A5.A502;

import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEPAttributeChangeJob;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26TemperatureInverseLinear;
import uk.co._4ng.enocean.eep.eep26.profiles.InternalEEP;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26TelegramType;
import uk.co._4ng.enocean.eep.eep26.telegram.FourBSTelegram;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class representing the A5-02 family of EnOcean Equipment Profiles
 * (Temperature sensors).
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public abstract class A502 extends InternalEEP {

    // Executor Thread Pool for handling attribute updates
    private ExecutorService attributeNotificationWorker;

    /**
     * The class constructor
     */
    public A502() {
        // build the attribute dispatching worker
        attributeNotificationWorker = Executors.newFixedThreadPool(1);
    }

    @Override
    public boolean handleProfileUpdate(EEP26Telegram telegram, EnOceanDevice device) {
        boolean success = false;
        // handle the telegram, as first cast it at the right type (or fail)
        if (telegram.getTelegramType() == EEP26TelegramType.FourBS) {
            // cast the telegram to handle to its real type
            FourBSTelegram profileUpdate = (FourBSTelegram) telegram;

            // get the packet payload
            byte[] payload = profileUpdate.getPayload();


            //wrap the payload as a temperature message
            A502TemperatureMessage msg = new A502TemperatureMessage(payload);

            //update the value of the attribute
            EEP26TemperatureInverseLinear tLinear = (EEP26TemperatureInverseLinear) getChannelAttribute(0, EEP26TemperatureInverseLinear.NAME);
            success = dispatchUpdateTask(telegram, device, msg, tLinear);
        }

        return success;
    }

    /**
     * Convenience routine for dispatching the value change to the lusteners
     *
     * @param telegram Telegram that this change came in on
     * @param device The device associated with the value
     * @param msg The temperature message
     * @param tLinear The temperature value
     * @return True if task updated OK
     */
    boolean dispatchUpdateTask(EEP26Telegram telegram, EnOceanDevice device, A502TemperatureMessage msg, EEP26TemperatureInverseLinear tLinear) {
        //check not null
        boolean success = false;
        if (tLinear != null) {
            int rawT = msg.getTemperature();

            //check range
            if (rawT >= 0 && rawT <= 255) {
                //update the attribute value
                tLinear.setRawValue(rawT);

                // build the dispatching task
                EEPAttributeChangeJob dispatcherTask = new EEPAttributeChangeJob(tLinear, 1, telegram, device);

                // submit the task for execution
                attributeNotificationWorker.submit(dispatcherTask);

                //update the success flag
                success = true;
            }
        }
        return success;
    }

}
