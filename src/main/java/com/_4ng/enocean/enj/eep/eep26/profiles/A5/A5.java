/*
 *
 * Copyright (c) 2017, 4ng and/or its affiliates. All rights reserved.
 * 4NG PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com._4ng.enocean.enj.eep.eep26.profiles.A5;

import com._4ng.enocean.enj.eep.EEP;
import com._4ng.enocean.enj.eep.Rorg;

/**
 * Top level EEP
 */
public abstract class A5 extends EEP {
    public static final Rorg RORG = new Rorg((byte) 0xa5);

    public A5(String version) {
        super(version);
    }
}
