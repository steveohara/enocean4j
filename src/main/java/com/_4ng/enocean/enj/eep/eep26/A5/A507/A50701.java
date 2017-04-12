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
package com._4ng.enocean.enj.eep.eep26.A5.A507;

import com._4ng.enocean.enj.eep.EEPAttribute;
import com._4ng.enocean.enj.eep.EEPAttributeChangeDispatcher;
import com._4ng.enocean.enj.eep.EEPIdentifier;
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26PIRStatus;
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26SupplyVoltage;
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26SupplyVoltageAvailability;
import com._4ng.enocean.enj.eep.eep26.telegram.EEP26Telegram;
import com._4ng.enocean.enj.eep.eep26.telegram.EEP26TelegramType;
import com._4ng.enocean.enj.eep.eep26.telegram.FourBSTelegram;
import com._4ng.enocean.enj.model.EnOceanDevice;

import java.util.ArrayList;

/**
 * @author bonino
 */
public class A50701 extends A507 {

    // the type definition
    public static final byte type = (byte) 0x01;
    public static final int CHANNEL = 0;

    /**
     * @param version
     */
    public A50701() {

        // add attributes,
        addChannelAttribute(CHANNEL, new EEP26SupplyVoltage(0.0, 5.0));
        addChannelAttribute(CHANNEL, new EEP26SupplyVoltageAvailability());
        addChannelAttribute(CHANNEL, new EEP26PIRStatus());
    }

    /*
     * (non-Javadoc)
     *
     * @see com._4ng.enocean.enj.eep.EEP#getEEPIdentifier()
     */
    @Override
    public EEPIdentifier getEEPIdentifier() {
        return new EEPIdentifier(A507.rorg, A507.func, type);
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
        boolean success = false;
        // handle the telegram, as first cast it at the right type (or fail)
        if (telegram.getTelegramType() == EEP26TelegramType.FourBS) {
            // cast the telegram to handle to its real type
            FourBSTelegram profileUpdate = (FourBSTelegram) telegram;

            // get the packet payload
            byte[] payload = profileUpdate.getPayload();

            // parse the telegram as an A50701 message
            A50701OccupancySensingMessage message = new A50701OccupancySensingMessage(payload);

            // check if its valid
            if (message.isValid()) {
                // prepare the list of changed attributes (only one)
                ArrayList<EEPAttribute<?>> changedAttributes = new ArrayList<>();

                // ------- get the attributes

                // supply voltage
                EEP26SupplyVoltage supplyVoltage = (EEP26SupplyVoltage) getChannelAttribute(CHANNEL, EEP26SupplyVoltage.NAME);

                // supply voltage availability
                EEP26SupplyVoltageAvailability supplyVoltageAvailability = (EEP26SupplyVoltageAvailability) getChannelAttribute(CHANNEL, EEP26SupplyVoltageAvailability.NAME);

                // occupancy status
                EEP26PIRStatus pirStatus = (EEP26PIRStatus) getChannelAttribute(CHANNEL, EEP26PIRStatus.NAME);

                // set the attribute values
                if (supplyVoltageAvailability != null) {
                    // set the availability value
                    supplyVoltageAvailability.setValue(message.isSupplyVoltageAvailable());

                    // update the list of changed attributes
                    changedAttributes.add(supplyVoltageAvailability);

                    // if the supply voltage attribute exists and a valid value
                    // had been specified in the message
                    if (message.isSupplyVoltageAvailable() && supplyVoltage != null) {
                        // store the voltage value
                        supplyVoltage.setRawValue(message.getSupplyVoltage());

                        // update the list of changed attributes
                        changedAttributes.add(supplyVoltage);
                    }
                }

                // set the pir status if the corresponding attribute is
                // available
                if (pirStatus != null) {
                    // set the pir status value
                    pirStatus.setValue(message.isMotionDetected());

                    // update the list of changed attributes
                    changedAttributes.add(pirStatus);
                }

                // if some attribute changed, notify it to listeners
                if (!changedAttributes.isEmpty()) {
                    // build the dispatching task
                    EEPAttributeChangeDispatcher dispatcherTask = new EEPAttributeChangeDispatcher(changedAttributes, CHANNEL, telegram, device);

                    // submit the task for execution
                    attributeNotificationWorker.submit(dispatcherTask);

                    // set success at true
                    // TODO check what to do if nothing changes, i.e., with
                    // success
                    // equal to false.
                    success = true;
                }
            }
        }

        return success;
    }

}
