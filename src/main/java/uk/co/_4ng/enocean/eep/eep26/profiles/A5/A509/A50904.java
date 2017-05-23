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

import uk.co._4ng.enocean.eep.eep26.attributes.EEP26ConcentrationLinear;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26HumidityLinear;
import uk.co._4ng.enocean.eep.eep26.attributes.EEP26TemperatureLinear;

/**
 * Implements the CO2/Temperature/Humidity profile
 */
public class A50904 extends A509 {

    // The used channel

    public static final int CHANNEL = 0;

    /**
     * Declare the supported attributes with their scales and ranges
     */
    public A50904() {

        // add attributes A50904 has operative range between 0.0 and 51 Celsius
        addChannelAttribute(CHANNEL, new EEP26TemperatureLinear(255, 0.0, 51.0));

        // and between 0 and 100% humidity
        addChannelAttribute(CHANNEL, new EEP26HumidityLinear(200, 0.0, 100.0));

        // and between 0 and 100% humidity
        addChannelAttribute(CHANNEL, new EEP26ConcentrationLinear(0.0, 2550.0));
    }
}
