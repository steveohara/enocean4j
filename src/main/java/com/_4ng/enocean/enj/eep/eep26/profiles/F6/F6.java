/*
 *
 * Copyright (c) 2017, 4ng and/or its affiliates. All rights reserved.
 * 4NG PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com._4ng.enocean.enj.eep.eep26.profiles.F6;

import com._4ng.enocean.enj.eep.EEP;
import com._4ng.enocean.enj.eep.Rorg;

/**
 * Top level EEP
 */
public abstract class F6 extends EEP {
    public static final Rorg RORG = new Rorg((byte) 0xd6);

    public F6(String version) {
        super(version);
    }
}
