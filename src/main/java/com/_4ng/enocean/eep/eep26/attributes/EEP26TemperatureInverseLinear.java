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
package com._4ng.enocean.eep.eep26.attributes;

import com._4ng.enocean.eep.EEPAttribute;

import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class EEP26TemperatureInverseLinear extends EEPAttribute<Double> {
    // the EEPFunction name
    public static final String NAME = "Temperature";
    public static final double MAX_VALID_RAW = 255.0;

    // the allowed range
    private double minT;
    private double maxT;

    /**
     * Inverse linear temperature sensor
     */
    public EEP26TemperatureInverseLinear() {
        super(NAME);

        // default value= -273 °C
        value = -273.0;
        unit = "Celsius";
        minT = -273.0;
        maxT = Double.MAX_VALUE;
    }

    public EEP26TemperatureInverseLinear(Double value, String unit) {
        super(NAME);

        if (unit != null && value != null && !unit.isEmpty() && (unit.equalsIgnoreCase("Celsius") || unit.equalsIgnoreCase("°C") || unit.equalsIgnoreCase("C"))) {
            // store the value
            this.value = value;

            // store the unit
            this.unit = unit;

            // set the maximum range
            minT = -273.0;
            maxT = Double.MAX_VALUE;
        }

        else {
            throw new NumberFormatException("Wrong unit or null value for temperature in Celsius degrees");
        }

    }

    public EEP26TemperatureInverseLinear(Double minT, Double maxT) {
        super(NAME);

        // default value= -273 °C
        value = -273.0;
        unit = "Celsius";
        this.minT = minT;
        this.maxT = maxT;
    }

    /**
     * @return the minT
     */
    public double getMinT() {
        return minT;
    }

    /**
     * @param minT the minT to set
     */
    public void setMinT(double minT) {
        this.minT = minT;
    }

    /**
     * @return the maxT
     */
    public double getMaxT() {
        return maxT;
    }

    /**
     * @param maxT the maxT to set
     */
    public void setMaxT(double maxT) {
        this.maxT = maxT;
    }

    @Override
    public void setValue(Double value) {
        if (value != null) {
            // store the current value
            this.value = value;
        }
    }

    @Override
    public void setUnit(String unit) {
        if (unit != null && !unit.isEmpty() && (unit.equalsIgnoreCase("Celsius") || unit.equalsIgnoreCase("°C") || unit.equalsIgnoreCase("C"))) {
            // store the unit
            this.unit = unit;
        }
    }

    @Override
    public byte[] byteValue() {
        // it is likely to never be used...

        // use byte buffers to ease double encoding / decoding

        // a byte buffer wrapping an array of 4 bytes
        ByteBuffer valueAsBytes = ByteBuffer.wrap(new byte[4]);

        // store the current value
        valueAsBytes.putDouble(value);

        // return the value as byte array
        return valueAsBytes.array();
    }

    public void setRawValue(int value) {
        // perform the scaling
        // TODO check conversion
        this.value = (maxT - minT) * (MAX_VALID_RAW - value) / MAX_VALID_RAW + minT;
    }

    /**
     * Checks if the current attribute represents a value in the declared valid
     * range or not.
     *
     * @return True if the valus is within range
     */
    public boolean isValid() {
        return value >= minT && value <= maxT;
    }

}
