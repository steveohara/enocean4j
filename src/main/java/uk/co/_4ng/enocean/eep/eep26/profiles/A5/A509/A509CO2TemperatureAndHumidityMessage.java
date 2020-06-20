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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing the CO2, temperature and humidity measurement message sent by a sensor
 * belonging to the A509 profile set. Basically, every message set by devices
 * having a profile stemming from A509 use the same structure and the only
 * difference is given the temperature linear range.
 */
class A509CO2TemperatureAndHumidityMessage {

    private static final Logger logger = LoggerFactory.getLogger(A509CO2TemperatureAndHumidityMessage.class);

    private final int temperature;
    private final int humidity;
    private final int concentration;
    private final boolean teachIn;
    private final boolean  humidityAvailable;
    private final boolean temperatureAvailable;

    /**
     * Class constructor, builds a message instance given the raw byte payload
     * of the corresponding 4BS telegram.
     */
    A509CO2TemperatureAndHumidityMessage(byte[] data) {

        humidity = 0x00FF & data[0];
        concentration = 0x00FF & data[1];
        temperature = 0x00FF & data[2];

        // get the teach-in flag (offset 28, 4th bit of the 4th byte)

        teachIn =  (data[3] & 0x8) == 0;

        // check if humidity reading is available bit 29
        humidityAvailable = (data[3] & 0x4) > 0;

        // check if temperature reading is available bit 30
        temperatureAvailable = (data[3] & 0x2) > 0;
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
     * Get the humidity value "transferred" by means of this messageas an
     * integer between 0 and 255.
     *
     * @return the concentration as an integer, between 0 and 255
     */
    int getConcentration() {
        return concentration;
    }

    /**
     * Get the teach-in status
     *
     * @return the teachIn, true if teach-in is active, false otherwise.
     */
    boolean isTeachIn() {
        return teachIn;
    }

    /**
     * Retrns true if there is a humidity value available
     * @return True if humidity value is OK to use
     */
    public boolean isHumidityAvailable() {
        return humidityAvailable;
    }

    /**
     * Retrns true if there is a temperature value available
     * @return True if temperature value is OK to use
     */
    public boolean isTemperatureAvailable() {
        return temperatureAvailable;
    }
}
