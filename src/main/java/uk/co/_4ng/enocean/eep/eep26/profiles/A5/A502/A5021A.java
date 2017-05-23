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
package uk.co._4ng.enocean.eep.eep26.profiles.A5.A502;

import uk.co._4ng.enocean.eep.eep26.attributes.EEP26TemperatureInverseLinear;

/**
 * @author bonino
 */
public class A5021A extends A502 {
    // the used channel
    public static final int CHANNEL = 0;

    /**
     */
    public A5021A() {

        // add attributes A5021A has operative range between 40.0 and 120.0
        // Celsius
        addChannelAttribute(CHANNEL, new EEP26TemperatureInverseLinear(40.0, 120.0));
    }
}
