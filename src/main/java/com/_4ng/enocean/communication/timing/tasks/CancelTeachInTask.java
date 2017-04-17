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
package com._4ng.enocean.communication.timing.tasks;

import com._4ng.enocean.communication.Connection;

import java.util.TimerTask;

/**
 * A TimerTask subclass which disables the teach in procedure on the given
 * Connection instance.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class CancelTeachInTask extends TimerTask {

    // the Connection layer reference
    private Connection theConnection;

    /**
     * Builds a task which disables the teach in procedure on the given
     * Connection instance.
     *
     * @param theConnection
     */
    public CancelTeachInTask(Connection theConnection) {
        // store a reference to the connection layer for which teach in must be
        // disabled
        this.theConnection = theConnection;
    }

    @Override
    public void run() {
        theConnection.disableTeachIn();
    }

}
