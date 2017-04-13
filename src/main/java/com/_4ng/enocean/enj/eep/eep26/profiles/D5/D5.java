/*
 *
 * Copyright (c) 2017, 4ng and/or its affiliates. All rights reserved.
 * 4NG PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com._4ng.enocean.enj.eep.eep26.profiles.D5;

import com._4ng.enocean.enj.eep.EEP;
import com._4ng.enocean.enj.eep.Rorg;

/**
 * Top level EEP
 */
public abstract class D5 extends EEP {
    public static final Rorg RORG = new Rorg((byte) 0xd5);

    public D5(String version) {
        super(version);
    }
}
