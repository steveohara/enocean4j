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
package com._4ng.enocean.devices;

import com._4ng.enocean.communication.DeviceChangeType;
import com._4ng.enocean.communication.DeviceListener;
import com._4ng.enocean.communication.timing.tasks.DeviceChangeJob;
import com._4ng.enocean.eep.EEP;
import com._4ng.enocean.eep.EEPAttributeChangeJob;
import com._4ng.enocean.eep.EEPIdentifier;
import com._4ng.enocean.eep.Rorg;
import com._4ng.enocean.eep.eep26.EEPRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the connection independent device management class.
 * This is static, there is only one of these per VM and it manages the communications
 * of attribute and device changes to the rest of the system, irrespective of the communications
 * type or channel in which the change was detected
 */
public class DeviceManager {

    private static final Logger logger = LoggerFactory.getLogger(DeviceManager.class);

    // the set of device listeners to keep updated about device events
    private static Set<DeviceListener> deviceListeners = Collections.newSetFromMap(new ConcurrentHashMap<DeviceListener, Boolean>());

    // The EEP eepRegistry
    private static EEPRegistry eepRegistry = new EEPRegistry();

    // Initialize the update delivery executor thread pool
    private static ExecutorService deviceUpdateDeliveryExecutor = Executors.newCachedThreadPool();

    // The set of known devices
    private static PersistentDeviceSet knownDevices;

    static {
        initialise();
    }

    /**
     * Initialises the DeviceManager
     */
    public static synchronized void initialise() {

        // Clear the listeners

        deviceListeners.clear();

        // Stop all jobs and clear the thread pool

        deviceUpdateDeliveryExecutor.shutdownNow();
        deviceUpdateDeliveryExecutor = Executors.newCachedThreadPool();

        // Initialise the device eepRegistry from the passed file if necessary

        knownDevices = new PersistentDeviceSet();
        logger.info("Initialised EnOcean device manager - {} EEP profiles", eepRegistry.getProfiles().size());
    }

    /**
     * Adds a device listener to the set of listeners to be notified about
     * device events: creation, modification, deletion.
     *
     * @param listener filename * The {@link DeviceListener} to notify to.
     */
    public static void addDeviceListener(DeviceListener listener) {
        // Store the listener in the set of currently active listeners

        deviceListeners.add(listener);

        // Immediately notify this listener of all the existing devices

        for (EnOceanDevice device : knownDevices.values()) {
            notifyDeviceListeners(device, DeviceChangeType.CREATED);
        }
    }

    /**
     * Removes a device listener from the ste of listeners to be notified about
     * device events.
     *
     * @param listener The {@link DeviceListener} to remove.
     * @return true if removal was successful, false, otherwise.
     */
    public static boolean removeDeviceListener(DeviceListener listener) {
        return deviceListeners.remove(listener);
    }

    /**
     * Registers a device that can be communicated with
     *
     * @param hexDeviceAddress Hexadecimal address of the device
     * @param eep              The equipment profile for this device
     * @return EnOceanDevice
     */
    public static EnOceanDevice registerDevice(String hexDeviceAddress, String eep) {
        byte deviceAddress[] = EnOceanDevice.parseAddress(hexDeviceAddress);
        EEPIdentifier eepId = EEPIdentifier.parse(eep);

        // check if the device is already known
        if (knownDevices.getByAddress(deviceAddress) == null && eepId != null) {
            return registerDevice(deviceAddress, null, eepId);
        }
        return null;
    }

    /**
     * Registers a device that can be communicated with
     *
     * @param address        Address expressed as a series of bytes as received in a telegram
     * @param manufacturerId The unique manufacturer ID if known
     * @param eep            The equipment profile for this device
     * @return EnOceanDevice
     */
    public static EnOceanDevice registerDevice(byte address[], byte manufacturerId[], EEPIdentifier eep) {
        EnOceanDevice device = new EnOceanDevice(address, manufacturerId);
        EEP deviceEEP = eepRegistry.getEEP(eep);
        if (deviceEEP != null) {
            device.setEEP(deviceEEP);
            return registerDevice(device);
        }
        return null;
    }

    /**
     * Registers a device that can be communicated with
     *
     * @param device Enocean device to register
     * @return EnOceanDevice
     */
    public static EnOceanDevice registerDevice(EnOceanDevice device) {
        // notify listeners
        notifyDeviceListeners(device, DeviceChangeType.CREATED);

        // store the device
        knownDevices.add(device);
        return device;
    }

    /**
     * Un-registers a device that can be communicated with
     *
     * @param device Enocean device to unregister
     */
    public static void unRegisterDevice(EnOceanDevice device) {
        if (knownDevices.getByUID(device.getAddressInt()) != null) {

            // notify listeners
            notifyDeviceListeners(device, DeviceChangeType.CREATED);

            // store the device
            knownDevices.remove(device);
        }
    }

    /**
     * Returns the set of currently known devices
     *
     * @return the knownDevices
     */
    public static Collection<EnOceanDevice> getDevices() {
        return knownDevices.values();
    }

    /**
     * adds sender address and status to the given payload, thus completing the
     * Returns the EnOcean device having the given UID
     *
     * @param deviceUID Return the device using its decimal ID
     * @return EnOceanDevice or null if the device is not registered
     */
    public static EnOceanDevice getDevice(int deviceUID) {
        return knownDevices.getByUID(deviceUID);
    }

    /**
     * Returns the registered device with the given address
     *
     * @param address Address in byte array format
     * @return Device or null if not registered
     */
    public static EnOceanDevice getDevice(byte[] address) {
        return knownDevices.getByAddress(address);
    }

    /**
     * Checks to see if the EEP is a supported format
     *
     * @param eep EEP identifier to check
     * @return True if this EEP is supported by enocean4j
     */
    public static boolean isEEPSupported(EEPIdentifier eep) {
        return eepRegistry.isEEPSupported(eep);
    }

    /**
     * Notify all the registered listeners that an event on a device has occurred
     *
     * @param device     Device being actioned
     * @param changeType Type of the change occurring
     */
    public static void notifyDeviceListeners(EnOceanDevice device, DeviceChangeType changeType) {
        deviceUpdateDeliveryExecutor.execute(new DeviceChangeJob(device, changeType, deviceListeners));
    }

    /**
     * Get the EEP profile for the given EEp identifier
     *
     * @param eep EEP identifier
     * @return EEP profile or null if not supported
     */
    public static EEP getEEP(EEPIdentifier eep) {
        return eepRegistry.getEEP(eep);
    }


    public static void notifyDeviceListeners(EEPAttributeChangeJob eepAttributeChangeJob) {
        for (DeviceListener listener : deviceListeners) {
            listener.deviceAttributeChange(eepAttributeChangeJob);
        }
    }

    /**
     * Returns a reference to all the supported profiles for the given RORG
     *
     * @param rorg Rorg to get profiles for
     * @return Map of supported EEP profiles
     */
    public Map<EEPIdentifier, EEP> getProfiles(Rorg rorg) {
        Map<EEPIdentifier, EEP> returnValue = new HashMap<>();
        for (EEP eep : eepRegistry.getProfiles().values()) {
            if (eep.getRorg().equals(rorg)) {
                returnValue.put(eep.getEEPIdentifier(), eep);
            }
        }
        return returnValue;
    }

    /**
     * Returns a reference to all the supported profiles for the given RORG and function
     *
     * @param rorg Rorg to get profiles for
     * @param function Function to match
     * @return Map of supported EEP profiles
     */
    public Map<EEPIdentifier, EEP> getProfiles(Rorg rorg, byte function) {
        Map<EEPIdentifier, EEP> returnValue = new HashMap<>();
        for (EEP eep : eepRegistry.getProfiles().values()) {
            if (eep.getRorg().equals(rorg) && eep.getFunction() == function) {
                returnValue.put(eep.getEEPIdentifier(), eep);
            }
        }
        return returnValue;
    }


}
