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
package com._4ng.enocean.eep.eep26.profiles.D5.D500;

import com._4ng.enocean.eep.EEP;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class representing the D5-00 Family of EnOcean profiles. In EEP26 only one
 * profile belongs to this family: the D5-00-01 which is used for single contact
 * inputs (true/false).
 *
 * @author bonino
 */
public abstract class D500 extends EEP {

    // Executor Thread Pool for handling attribute updates
    volatile ExecutorService attributeNotificationWorker;

    // -------------------------------------------------
    // Parameters defined by this EEP, which
    // might change depending on the network
    // activity.
    // --------------------------------------------------

    // --------------------------------------------------

    /**
     */
    public D500() {
        // build the attribute dispatching worker
        attributeNotificationWorker = Executors.newFixedThreadPool(1);
    }

}
