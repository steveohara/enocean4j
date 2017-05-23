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
 * Gas concentration e.g. CO2
 */
public class EEP26ConcentrationLinear extends EEPAttribute<Double> {
    // the EEPFunction name
    public static final String NAME = "Concentration";
    public static final double MAX_VALID_RAW = 255.0;
    // the allowed range
    private double minC;
    private double maxC;

    /**
     * A linear temperature probe
     */
    public EEP26ConcentrationLinear() {
        super(NAME);

        value = 0.0;
        unit = "ppm";
        minC = 0.0;
        maxC = 2550;
    }

    public EEP26ConcentrationLinear(Double value, String unit) {
        super(NAME);

        if (unit != null && value != null && !unit.isEmpty() && (unit.equalsIgnoreCase("ppm"))) {
            // store the value
            this.value = value;

            // store the unit
            this.unit = unit;

            // set the maximum range
            minC = 0.0;
            maxC = 2550;
        }

        else {
            throw new NumberFormatException("Wrong unit or null value for concentration");
        }

    }

    public EEP26ConcentrationLinear(Double minC, Double maxC) {
        this();
        this.minC = minC;
        this.maxC = maxC;
    }

    /**
     * Returns the minimum concentration
     *
     * @return the minC
     */
    public double getMinC() {
        return minC;
    }

    /**
     * Sets the minimum concentration
     *
     * @param minC the minC to set
     */
    public void setMinT(double minC) {
        this.minC = minC;
    }

    /**
     * Gets the maximum concentration
     *
     * @return the maxC
     */
    public double getMaxT() {
        return maxC;
    }

    /**
     * @param maxC the maxC to set
     */
    public void setMaxT(double maxC) {
        this.maxC = maxC;
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
        if (unit != null && !unit.isEmpty() && (unit.equalsIgnoreCase("ppm"))) {
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
        this.value = (maxC - minC) * (double) value / MAX_VALID_RAW + minC;
    }

    /**
     * Checks if the current attribute represents a value in the declared valid
     * range or not.
     *
     * @return True if the value is valid
     */
    public boolean isValid() {
        return value >= minC && value <= maxC;
    }

}
