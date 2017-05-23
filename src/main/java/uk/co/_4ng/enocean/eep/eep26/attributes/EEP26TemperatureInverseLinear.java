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

/**
 * A variant of the standard temperature attribute but the scale is inverted
 */
public class EEP26TemperatureInverseLinear extends EEP26TemperatureLinear {

    public EEP26TemperatureInverseLinear() {
    }

    public EEP26TemperatureInverseLinear(int maxRawValue, Double minT, Double maxT) {
        super(maxRawValue, minT, maxT);
    }

    public EEP26TemperatureInverseLinear(Double minT, Double maxT) {
        super(minT, maxT);
    }

    @Override
    public void setRawValue(int value) {
        // perform the scaling
        this.value = (max - min) * (maxRawValue - value) / maxRawValue + min;
    }


}
