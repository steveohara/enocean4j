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

/**
 * A class representing the temperature measurement message sent by a sensor
 * belonging to the A502 profile set. Basically, every message set by devices
 * having a profile stemming from A502 use the same structure and the only
 * difference is given the temperature linear range.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
class A502TemperatureMessage {
    int temperature;
    boolean teachIn;

    A502TemperatureMessage() {
    }

    /**
     * Class constructor, builds a message instance given the raw byte payload
     * of the corresponding 4BS telegram.
     */
    A502TemperatureMessage(byte[] data) {
        // temperature data has offset 16 (3rd byte)
        temperature = 0x00FF & data[2];

        // teach-n is DB0.3
        teachIn = (data[3] & 0x8) == 0;
    }

    /**
     * Get the temperature value "transferred" by means of this messageas an integer between 0 and 255.
     *
     * @return the temperature as an integer, between 0 and 255
     */
    int getTemperature() {
        return temperature;
    }

    /**
     * Get the teach-in status
     *
     * @return the teachIn, true if teach-in is active, false otherwise.
     */
    boolean isTeachIn() {
        return teachIn;
    }

}
