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
package uk.co._4ng.enocean.eep.eep26.attributes;

import uk.co._4ng.enocean.eep.EEPAttribute;

import java.nio.ByteBuffer;

/**
 * @author bonino
 */
public class EEP26HumidityLinear extends EEPAttribute<Double> {
    // the EEPFunction name
    public static final String NAME = "RelativeHumidity";

    int maxRawValue = 200;
    double min = 0.0;
    double max = 100.0;

    /**
     * A linear temperature probe
     */
    EEP26HumidityLinear() {
        super(NAME);
        value = min;
        unit = "Celsius";
    }

    /**
     * Creates an attribute with the max/min and range
     *
     * @param maxRawValue Maximum unscaled value
     * @param min        Minimum scaled temperature
     * @param max        Mximum scaled temperature
     */
    public EEP26HumidityLinear(int maxRawValue, Double min, Double max) {
        this();
        this.maxRawValue = maxRawValue;
        this.min = min;
        this.max = max;
    }

    /**
     * Returns the minimum scaled value
     *
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * Sets the minimum scaled value
     *
     * @param min the min to set
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * Gets the maximum scaled value
     *
     * @return the max
     */
    public double getMax() {
        return max;
    }

    /**
     * Sets the maximum scaled value
     *
     * @param max the max to set
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * Returns the maximum raw value possible for this attribute
     *
     * @return Maximum unscaled value
     */
    public int getMaxRawValue() {
        return maxRawValue;
    }

    /**
     * Sets the maximum unscaled value of this attribute
     *
     * @param maxRawValue Maximum unscaled value
     */
    public void setMaxRawValue(int maxRawValue) {
        this.maxRawValue = maxRawValue;
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
        if (unit != null && !unit.isEmpty() && unit.equalsIgnoreCase("%")) {
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

    @Override
    public void setRawValue(int value) {
        this.value = (max - min) * (double) value / maxRawValue + min;
    }

    /**
     * Checks if the current attribute represents a value in the declared valid
     * range or not.
     *
     * @return True if the value is valid
     */
    public boolean isValid() {
        return value >= min && value <= max;
    }


}
