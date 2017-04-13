/*
 * EnJ - EnOcean Java API
 * 
 * Copyright 2014 Andrea Biasi, Dario Bonino 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com._4ng.enocean.enj.eep.eep26.profiles.F6.F610;

import com._4ng.enocean.enj.devices.EnOceanDevice;
import com._4ng.enocean.enj.eep.EEPAttributeChangeJob;
import com._4ng.enocean.enj.eep.EEPIdentifier;
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26HandleRotation;
import com._4ng.enocean.enj.eep.eep26.telegram.EEP26Telegram;
import com._4ng.enocean.enj.eep.eep26.telegram.EEP26TelegramType;
import com._4ng.enocean.enj.eep.eep26.telegram.RPSTelegram;

/**
 * @author bonino
 */
public class F61000 extends F610 {

    // the type definition
    public static final byte TYPE = (byte) 0x00;

    public static final int CHANNEL = 0;

    /**
     *
     */
    public F61000() {
        // call the superclass constructor

        // define the "active" attributes
        addChannelAttribute(CHANNEL, new EEP26HandleRotation());
    }

    /*
     * (non-Javadoc)
     *
     * @see com._4ng.enocean.enj.eep.EEP#getEEPIdentifier()
     */
    @Override
    public EEPIdentifier getEEPIdentifier() {
        return new EEPIdentifier(RORG, FUNC, TYPE);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com._4ng.enocean.enj.eep.EEP#handleProfileUpdate(it.polito.elite
     * .enocean.enj.eep.eep26.telegram.EEP26Telegram)
     */
    @Override
    public boolean handleProfileUpdate(EEP26Telegram telegram, EnOceanDevice device) {
        // the success flag, if true the update has been succesful, otherwise it
        // is set at false.
        boolean success = false;

        // handle the telegram, as first cast it at the right type (or fail)
        if (telegram.getTelegramType() == EEP26TelegramType.RPS) {
            // cast the telegram to handle to its real type
            RPSTelegram profileUpdate = (RPSTelegram) telegram;

            // get the packet payload
            byte[] payload = profileUpdate.getPayload();

            // create a window handle message object
            F610WindowHandleMessage message = new F610WindowHandleMessage(payload);

            // check if the message is valid
            if (message.isValid()) {
                // get the attribute to update
                EEP26HandleRotation rotationAttribute = (EEP26HandleRotation) getChannelAttribute(CHANNEL, EEP26HandleRotation.NAME);

                // set the current rotation value
                rotationAttribute.setValue(message.getPosition());

                // build the dispatching task
                EEPAttributeChangeJob dispatcherTask = new EEPAttributeChangeJob(rotationAttribute, CHANNEL, telegram, device);

                // submit the task for execution
                attributeNotificationWorker.submit(dispatcherTask);

                // if comes here everything is fine
                success = true;
            }
        }
        return success;
    }
}
