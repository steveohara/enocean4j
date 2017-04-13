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

/**
 * @author bonino
 */
public class EEP26OverCurrentSwitchOff extends EEPAttribute<Boolean> {
    // the EEPFunction name
    public static final String NAME = "EEP26OverCurrentSwitchOff";

    // the possible values
    public static final boolean READY = false;
    public static final boolean EXECUTED = true;

    /**
     * Over current switch
     */
    public EEP26OverCurrentSwitchOff() {

        // call the superclass constructor
        super(NAME);

        // set the default value at disabled
        value = READY;
    }

    public EEP26OverCurrentSwitchOff(Boolean value) {
        // call the superclass constructor
        super(NAME);

        // set the given value
        this.value = value;
    }

    @Override
    public byte[] byteValue() {
        // by default is not-used / not_detected
        byte value = 0x00;

        // if value is true than a power failure has been detected and the value
        // should be 0b1 == 0x01
        if (this.value == EXECUTED) {
            value = 0x01;
        }

        return new byte[]{value};
    }

}
