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
package com._4ng.enocean.enj.eep.eep26.profiles.A5.A502;

import com._4ng.enocean.enj.eep.EEPIdentifier;
import com._4ng.enocean.enj.eep.eep26.attributes.EEP26TemperatureInverseLinear;

/**
 * @author bonino
 */
public class A50201 extends A502 {

    // the type definition
    public static final byte TYPE = (byte) 0x01;

    // the used channel
    public static final int CHANNEL = 0;

    /**
     *
     */
    public A50201() {

        // add attributes A50201 has operative range between -40.0 and 0 Celsius
        addChannelAttribute(CHANNEL, new EEP26TemperatureInverseLinear(-40.0, 0.0));
    }

    /*
     * (non-Javadoc)
     *
     * @see com._4ng.enocean.enj.eep.EEP#getEEPIdentifier()
     */
    @Override
    public EEPIdentifier getEEPIdentifier() {
        // return the EEPIdentifier for this profile
        return new EEPIdentifier(RORG, FUNC, TYPE);

    }
}
