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
package uk.co._4ng.enocean.eep.eep26.attributes;

import uk.co._4ng.enocean.eep.EEPAttribute;

import java.nio.ByteBuffer;

/**
 * @author bonino
 */
public class EEP26HumidityLinear extends EEPAttribute<Double> {
    // the EEPFunction name
    public static final String NAME = "RelativeHumidity";
    public static final double MAX_VALID_RAW = 250.0;

    // the allowed range
    private double minH;
    private double maxH;

    /**
     * Linear humidity sensor
     */
    public EEP26HumidityLinear() {
        super(NAME);

        // default value= -273 °C
        value = 0.0;
        unit = "%";
        minH = 0.0;
        maxH = Double.MAX_VALUE;
    }

    public EEP26HumidityLinear(Double value, String unit) {
        super(NAME);

        if (unit != null && value != null && !unit.isEmpty() && unit.equalsIgnoreCase("%")) {
            // store the value
            this.value = value;

            // store the unit
            this.unit = unit;

            // set the maximum range
            minH = 0.0;
            maxH = Double.MAX_VALUE;
        }

        else {
            throw new NumberFormatException("Wrong unit or null value for relative humidity expressed in %");
        }

    }

    public EEP26HumidityLinear(Double minH, Double maxH) {
        super(NAME);

        // default value= -273 °C
        value = 0.0;
        unit = "%";
        this.minH = minH;
        this.maxH = maxH;
    }

    /**
     * @return the minH
     */
    public double getMinH() {
        return minH;
    }

    /**
     * @param minH the minH to set
     */
    public void setMinH(double minH) {
        this.minH = minH;
    }

    /**
     * @return the maxH
     */
    public double getMaxH() {
        return maxH;
    }

    /**
     * @param maxH the maxH to set
     */
    public void setMaxH(double maxH) {
        this.maxH = maxH;
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

    public void setRawValue(int value) {
        // perform the scaling
        // TODO check conversion
        this.value = (maxH - minH) * (double) value / MAX_VALID_RAW + minH;
    }

    /**
     * Checks if the current attribute represents a value in the declared valid
     * range or not.
     *
     * @return True if the value is valid
     */
    public boolean isValid() {
        return value >= minH && value <= maxH;
    }

}
