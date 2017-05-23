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
package uk.co._4ng.enocean.eep.eep26.profiles;

import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEP;
import uk.co._4ng.enocean.eep.EEPAttribute;
import uk.co._4ng.enocean.eep.EEPAttributeChangeJob;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A holder for all EEP profiles
 */
public abstract class AbstractEEP extends EEP {

    // Executor Thread Pool for handling attribute updates
    protected static ExecutorService attributeNotificationWorker = Executors.newFixedThreadPool(5);

    /**
     * Convenience routine for firing an attribute change value
     *
     * @param attr     Attribute with new value
     * @param channel  Channel
     * @param telegram The telegram it came in on
     * @param device   The device associated with the telegram
     * @param rawValue The unscaled value from the device - if null, it isn't stored
     * @return True if the update was OK
     */
    protected boolean fireAttributeEvent(DeviceManager deviceManager, EEPAttribute attr, int channel, EEP26Telegram telegram, EnOceanDevice device, Integer rawValue) {

        boolean success = false;

        // check not null
        if (attr != null) {

            // update the attribute value if it isn't null
            if (rawValue != null) {
                attr.setRawValue(rawValue);
            }

            // build the dispatching task
            EEPAttributeChangeJob dispatcherTask = new EEPAttributeChangeJob(deviceManager, attr, channel, telegram, device);

            // submit the task for execution
            attributeNotificationWorker.submit(dispatcherTask);

            // update the success flag
            success = true;
        }
        return success;

    }

}
