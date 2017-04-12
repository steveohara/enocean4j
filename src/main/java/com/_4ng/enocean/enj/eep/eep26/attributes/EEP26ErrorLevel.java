package com._4ng.enocean.enj.eep.eep26.attributes;

import com._4ng.enocean.enj.eep.EEPAttribute;
import com._4ng.enocean.enj.eep.eep26.D2.D201.D201ErrorLevel;

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
