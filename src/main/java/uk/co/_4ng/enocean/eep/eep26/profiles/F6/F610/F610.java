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
package uk.co._4ng.enocean.eep.eep26.profiles.F6.F610;

import uk.co._4ng.enocean.eep.eep26.profiles.InternalEEP;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author bonino
 */
public abstract class F610 extends InternalEEP {

    // func must be defined by extending classes

    // Executor Thread Pool for handling attribute updates
    protected transient ExecutorService attributeNotificationWorker;

    // -------------------------------------------------
    // Parameters defined by this EEP, which
    // might change depending on the network
    // activity.
    // --------------------------------------------------

    // --------------------------------------------------

    /**
     */
    public F610() {
        // build the attribute dispatching worker
        attributeNotificationWorker = Executors.newFixedThreadPool(1);
    }
}
