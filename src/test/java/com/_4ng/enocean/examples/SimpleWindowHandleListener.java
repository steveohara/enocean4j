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
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26HandleRotation;
import com._4ng.enocean.enj.eep.eep26.telegram.EEP26Telegram;
import com._4ng.enocean.enj.model.EnOceanDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bonino
 */
public class SimpleWindowHandleListener implements EEPAttributeChangeListener {

    private static final Logger logger = LoggerFactory.getLogger(SimpleWindowHandleListener.class);

    /**
     *
     */
    public SimpleWindowHandleListener() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com._4ng.enocean.enj.eep.EEPAttributeChangeListener#
     * handleAttributeChange(int, com._4ng.enocean.enj.eep.EEPAttribute)
     */
    @Override
    public void handleAttributeChange(int channelId, EEP26Telegram telegram, EEPAttribute<?> attribute, EnOceanDevice device) {
        // cast
        if (attribute instanceof EEP26HandleRotation) {
            //get the rotation attribute
            EEP26HandleRotation rotation = (EEP26HandleRotation) attribute;

            //log the value
            logger.info("Handle in position: {}", rotation.getValue().name());
        }

    }

}
