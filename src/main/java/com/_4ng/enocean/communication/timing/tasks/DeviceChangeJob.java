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

import com._4ng.enocean.communication.DeviceChangeType;
import com._4ng.enocean.communication.DeviceListener;
import com._4ng.enocean.devices.EnOceanDevice;

import java.util.Set;

/**
 * @author bonino
 */
public class DeviceChangeJob implements Runnable {
    private EnOceanDevice changedDevice;
    private DeviceChangeType typeOfChange;
    private Set<DeviceListener> listeners;

    /**
     * Builds a device update delivery task having as subject the given
     * {@link EnOceanDevice} instance and as type of change the given
     * {@link DeviceChangeType} value.
     *
     * @param changedDevice The changed device.
     * @param typeOfChange  The type of change (CREATED, MODIFIED or DELETED)
     * @param listeners     The {@link DeviceListener}s to be notified.
     */
    public DeviceChangeJob(EnOceanDevice changedDevice, DeviceChangeType typeOfChange, Set<DeviceListener> listeners) {
        this.changedDevice = changedDevice;
        this.typeOfChange = typeOfChange;
        this.listeners = listeners;
    }

    @Override
    public void run() {
        // deliver to all listeners
        for (DeviceListener listener : listeners) {
            // deliver the device update notification according to the change type
            switch (typeOfChange) {
                case CREATED: {
                    listener.addedEnOceanDevice(changedDevice);
                    break;
                }
                case MODIFIED: {
                    listener.modifiedEnOceanDevice(changedDevice);
                    break;
                }
                case DELETED: {
                    listener.removedEnOceanDevice(changedDevice);
                    break;
                }
            }
        }
    }
}
