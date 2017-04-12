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
package com._4ng.enocean.enj.eep.eep26.attributes;

import com._4ng.enocean.enj.eep.EEPAttribute;

import java.nio.ByteBuffer;

/**
 * @author bonino
 */
public class EEP26SupplyVoltage extends EEPAttribute<Double> {
    // the EEPFunction name
    public static final String NAME = "SupplyVoltage";

    // the allowed range
    private double minV;
    private double maxV;

    /**
     * @param name
     */
    public EEP26SupplyVoltage() {
        super(NAME);

        // set the default value
        value = 0.0;
        unit = "V";
        minV = 0;
        maxV = 5;
    }

    public EEP26SupplyVoltage(Double value, String unit) {
        super(NAME);

        if (unit != null

                && !unit.isEmpty() && (unit.equalsIgnoreCase("Volt") || unit.equalsIgnoreCase("V"))) {
            // store the value
            this.value = value;

            // store the unit
            this.unit = unit;

            // set the maximum range
            minV = 0.0;
            maxV = 5.0;
        }

        else {
            throw new NumberFormatException("Wrong unit or null value for supply voltage in Volt (V)");
        }

    }

    public EEP26SupplyVoltage(Double minV, Double maxV) {
        super(NAME);

        // default value 0V
        value = 0.0;
        unit = "V";
        this.minV = minV;
        this.maxV = maxV;
    }


    /**
     * @return the minV
     */
    public double getMinV() {
        return minV;
    }

    /**
     * @param minV the minV to set
     */
    public void setMinV(double minV) {
        this.minV = minV;
    }

    /**
     * @return the maxV
     */
    public double getMaxV() {
        return maxV;
    }

    /**
     * @param maxV the maxV to set
     */
    public void setMaxV(double maxV) {
        this.maxV = maxV;
    }

    /*
     * @see com._4ng.enocean.enj.eep.EEPAttribute#setValue
     */
    @Override
    public boolean setValue(Double value) {
        boolean stored = false;

        if (value != null) {
            // store the current value
            this.value = value;

            // updated the operation result
            stored = true;
        }

        return stored;
    }

    /*
     * @see com._4ng.enocean.enj.eep.EEPAttribute#setUnit
     */
    @Override
    public boolean setUnit(String unit) {
        boolean stored = false;

        if (unit != null

                && !unit.isEmpty() && (unit.equalsIgnoreCase("Volt") || unit.equalsIgnoreCase("V"))) {

            // store the unit
            this.unit = unit;

            // set the stored flag at true
            stored = true;
        }

        return stored;
    }

    /*
     * (non-Javadoc)
     *
     * @see com._4ng.enocean.enj.eep.EEPAttribute#byteValue()
     */
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
        // perform scaling (value should be between 0 and 250 included
        if (value >= 0 && value <= 250) {
            this.value = (maxV - minV) * (double) value / 250.0 + minV;
        }
    }

    /**
     * Checks if the current attribute represents a value in the declared valid
     * range or not.
     *
     * @return
     */
    public boolean isValid() {
        return value >= minV && value <= maxV;
    }

}
