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
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class EEP26Current extends EEPAttribute<Double> {
    // the EEPFunction name
    public static final String NAME = "Current";

    private int maxRawValue = 4095;

    /**
     * A linear temperature probe
     */
    EEP26Current() {
        super(NAME);
        value = 0.0;
        unit = "A";
    }

    /**
     * Creates an attribute with the max/min and range
     *
     * @param maxRawValue Maximum unscaled value
     */
    public EEP26Current(int maxRawValue) {
        this();
        this.maxRawValue = maxRawValue;
    }

    /**
     * Returns the maximum raw value possible for this attribute
     *
     * @return Maximum unscaled value
     */
    public int getMaxRawValue() {
        return maxRawValue;
    }

    @Override
    public void setValue(Double value) {
        if (value != null) {
            this.value = value;
        }
    }

    @Override
    public byte[] byteValue() {
        ByteBuffer valueAsBytes = ByteBuffer.wrap(new byte[4]);
        valueAsBytes.putDouble(value);
        return valueAsBytes.array();
    }
}
