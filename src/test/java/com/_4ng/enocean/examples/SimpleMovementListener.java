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
package com._4ng.enocean.examples;


import com._4ng.enocean.enj.eep.EEPAttribute;
import com._4ng.enocean.enj.eep.EEPAttributeChangeListener;
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26PIRStatus;
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26SupplyVoltage;
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26SupplyVoltageAvailability;
import com._4ng.enocean.enj.eep.eep26.telegram.EEP26Telegram;
import com._4ng.enocean.enj.model.EnOceanDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bonino
 */
public class SimpleMovementListener implements EEPAttributeChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMovementListener.class);

    /**
     *
     */
    public SimpleMovementListener() {
    }

    /* (non-Javadoc)
     * @see com._4ng.enocean.enj.eep.EEPAttributeChangeListener#handleAttributeChange(int, com._4ng.enocean.enj.eep.EEPAttribute)
     */
    @Override
    public void handleAttributeChange(int channelId, EEP26Telegram telegram, EEPAttribute<?> attribute, EnOceanDevice device) {
        //log the received attributes
        if (attribute instanceof EEP26PIRStatus) {
            logger.info("Current PIR status: {}", attribute.getValue());
        }
        else if (attribute instanceof EEP26SupplyVoltageAvailability) {
            logger.info("Supply voltage information is available: {}", attribute.getValue());
        }
        else if (attribute instanceof EEP26SupplyVoltage) {
            logger.info("Supply voltage value: {} V", attribute.getValue());
        }
    }

}
