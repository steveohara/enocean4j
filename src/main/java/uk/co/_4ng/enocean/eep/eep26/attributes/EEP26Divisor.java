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

/**
 * A class representing the divisor value (see D2-32-xx devices)
 *
 * @author sohara
 */
public class EEP26Divisor extends EEPAttribute<Boolean> {
    // the EEPFunction name
    public static final String NAME = "Divisor";

    // the possible values
    public static final boolean X1 = false;
    public static final boolean X10 = true;

    public EEP26Divisor() {
        //call the superclass constructor
        super(NAME);

        //set the default value at disabled
        value = X10;
    }

    public EEP26Divisor(Boolean value) {
        //call the superclass constructor
        super(NAME);

        //set the given value
        this.value = value;
    }

    @Override
    public byte[] byteValue() {
        return new byte[]{value ? (byte)1 : 0};
    }
}
