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
package com._4ng.enocean.eep.eep26.profiles.A5.A502;

import com._4ng.enocean.eep.EEPIdentifier;
import com._4ng.enocean.eep.eep26.attributes.EEP26TemperatureInverseLinear;

/**
 * @author bonino
 */
public class A50220 extends A50230 {

    // the type definition
    public static final byte TYPE = (byte) 0x20;
    // the used channel
    public static final int CHANNEL = 0;

    /**
     */
    public A50220() {

        // add attributes A50220 has operative range between -10.0 and 41.2
        // Celsius
        addChannelAttribute(CHANNEL, new EEP26TemperatureInverseLinear(-10.0, 41.2));
    }

    @Override
    public EEPIdentifier getEEPIdentifier() {
        // return the EEPIdentifier for this profile
        return new EEPIdentifier(RORG, FUNC, TYPE);

    }

}
