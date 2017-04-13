/*
 * EnJ - EnOcean Java API
 * 
 * Copyright 2014 Andrea Biasi, Dario Bonino 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com._4ng.enocean.enj.devices;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Hashmap implementation for EnOceanDevices
 */
public class PersistentDeviceSet implements Map<Integer, EnOceanDevice>,Serializable {

    private static final long serialVersionUID = 4702253423427228802L;
    private Map<Integer, EnOceanDevice> theSet = new HashMap<>();

    @Override
    public int size() {
        return theSet.size();
    }

    @Override
    public boolean isEmpty() {
        return theSet.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return theSet.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return theSet.containsValue(value);
    }

    @Override
    public EnOceanDevice get(Object key) {
        return theSet.get(key);
    }

    @Override
    public EnOceanDevice put(Integer key, EnOceanDevice value) {
        return theSet.put(key, value);
    }

    @Override
    public EnOceanDevice remove(Object key) {
        return theSet.remove(key);
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends EnOceanDevice> m) {
        theSet.putAll(m);
    }

    @Override
    public void clear() {
        theSet.clear();
    }

    @Override
    public Set<Integer> keySet() {
        return theSet.keySet();
    }

    @Override
    public Collection<EnOceanDevice> values() {
        return theSet.values();
    }

    @Override
    public Set<Entry<Integer, EnOceanDevice>> entrySet() {
        return theSet.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return theSet.equals(o);
    }

    @Override
    public int hashCode() {
        return theSet.hashCode();
    }

    /**
     * Gets a device given its high-level UID, equivalent to performing the get
     * method of the superclass.
     *
     * @param deviceUID The device high-level identifier
     * @return The corresponding {@link EnOceanDevice} instance.
     */
    EnOceanDevice getByUID(int deviceUID) {
        // return the device, if present
        return theSet.get(deviceUID);
    }

    /**
     * Gets a device given its low-level byte address. Uses the EnOceanDevice
     * utilities for converting the low-level address into an high-level UID and
     * then calls the getByUID method.
     *
     * @param address The low-level address of the device
     * @return The corresponding {@link EnOceanDevice} instance
     */
    EnOceanDevice getByAddress(byte[] address) {
        // get the corresponding EnOcean device
        return theSet.get(EnOceanDevice.byteAddressToUID(address));
    }

    /**
     * Adds the given {@link EnOceanDevice} to the set of devices.
     *
     * @param device The {@link EnOceanDevice} instance to add.
     */
    void add(EnOceanDevice device) {
        // add the given device
        theSet.put(device.getAddressInt(), device);
    }

    EnOceanDevice remove(EnOceanDevice device) {
        return theSet.remove(device.getAddressInt());
    }

    EnOceanDevice remove(int uid) {
        return theSet.remove(uid);
    }
}