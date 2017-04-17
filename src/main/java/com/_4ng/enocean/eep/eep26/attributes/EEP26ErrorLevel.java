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

package com._4ng.enocean.eep.eep26.attributes;

import com._4ng.enocean.eep.EEPAttribute;
import com._4ng.enocean.eep.eep26.profiles.D2.D201.D201ErrorLevel;

public class EEP26ErrorLevel extends EEPAttribute<D201ErrorLevel> {
    // the EEPFunction name
    public static final String NAME = "ErrorLevel";

    public EEP26ErrorLevel() {
        super(NAME);

        // default no error
        value = D201ErrorLevel.HARDWARE_OK;
    }

    public EEP26ErrorLevel(D201ErrorLevel errorLevel) {
        // call the super class method
        super(NAME);

        // store the given error level
        value = errorLevel;
    }

    @Override
    public byte[] byteValue() {
        // return the byte representation of the current error level
        return new byte[]{value.getCode()};
    }

}
