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
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D201DefaultStateValue;

/**
 * A class representing the default state of a switching actuator.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino<
 */
public class EEP26DefaultState extends EEPAttribute<D201DefaultStateValue> {
    // the EEPFunction name
    public static final String NAME = "DefaultState";

    public EEP26DefaultState() {
        // call the superclass constructor
        super(NAME);

        // set the default value at previous state
        value = D201DefaultStateValue.PREVIOUS_STATE;
    }

    public EEP26DefaultState(D201DefaultStateValue value) {
        // call the superclass constructor
        super(NAME);

        // store the value
        this.value = value;
    }

    public EEP26DefaultState(byte value) {
        // call the super class constructor
        super(NAME);

        //store the value
        if (!setValue(value))
        //set the value at the default value
        {
            this.value = D201DefaultStateValue.PREVIOUS_STATE;
        }

    }

    public boolean setValue(byte value) {
        // ----- map the byte value to the right enum entry

        // get all supported value
        D201DefaultStateValue supportedValues[] = D201DefaultStateValue.values();

        // the value found flag
        boolean found = false;

        // iterate until a supported value is found
        for (int i = 0; i < supportedValues.length && !found; i++) {
            // if the given value corresponds to a supported value, store it
            if (supportedValues[i].getCode() == value) {
                // store the value
                this.value = supportedValues[i];

                // set the flag at true
                found = true;
            }
        }

        return found;
    }

    @Override
    public byte[] byteValue() {
        return new byte[]{value.getCode()};
    }


}
