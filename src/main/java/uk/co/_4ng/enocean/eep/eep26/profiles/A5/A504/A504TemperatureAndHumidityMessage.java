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
package uk.co._4ng.enocean.eep.eep26.profiles.A5.A504;

/**
 * A class representing the temperature and humidity measurement message sent by a sensor
 * belonging to the A504 profile set. Basically, every message set by devices
 * having a profile stemming from A504 use the same structure and the only
 * difference is given the temperature linear range.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
class A504TemperatureAndHumidityMessage {
    private final int temperature;
    private final int humidity;
    private final boolean teachIn;

    /**
     * Class constructor, builds a message instance given the raw byte payload
     * of the corresponding 4BS telegram.
     */
    A504TemperatureAndHumidityMessage(byte[] data, Class clazz) {

        if (clazz.equals(A50403.class)) {
            // temperature data has offset 14 and is 10 bits
            int temperatureCalc = ((data[1] & 0xc0) << 2) + (data[2] & 0xff);
            temperature = temperatureCalc & 0x3ff;

            // humidity data has offset 0
            humidity = 0x00FF & data[0];
        }
        else {
            // humidity data has offset 1
            humidity = 0x00FF & data[1];

            // transform into a positive integer
            temperature = 0x00FF & data[2];
        }
        teachIn = (data[3] & 0x8) == 0;
    }

    /**
     * Get the temperature value "transferred" by means of this messageas an
     * integer between 0 and 255.
     *
     * @return the temperature as an integer, between 0 and 255e
     */
    int getTemperature() {
        return temperature;
    }

    /**
     * Get the humidity value "transferred" by means of this messageas an
     * integer between 0 and 255.
     *
     * @return the humidity as an integer, between 0 and 255
     */
    int getHumidity() {
        return humidity;
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
