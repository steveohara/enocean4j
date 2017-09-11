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
package uk.co._4ng.enocean.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co._4ng.enocean.communication.DeviceChangeType;
import uk.co._4ng.enocean.communication.DeviceListener;
import uk.co._4ng.enocean.communication.DeviceValueListener;
import uk.co._4ng.enocean.communication.tasks.DeviceChangeJob;
import uk.co._4ng.enocean.eep.EEP;
import uk.co._4ng.enocean.eep.EEPAttributeChangeJob;
import uk.co._4ng.enocean.eep.EEPIdentifier;
import uk.co._4ng.enocean.eep.eep26.EEPRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is the connection independent device management class.
 * A DeviceManager is registered with one or more Connection classes. It is possible to have more than
 * one DeviceManager but not a likely scenario. Most applications will want to register their devices
 * with a single manager.
 */
public class DeviceManager {

    private static final Logger logger = LoggerFactory.getLogger(DeviceManager.class);

    // the set of device listeners to keep updated about device events
    private Set<DeviceListener> deviceListeners = Collections.newSetFromMap(new ConcurrentHashMap<DeviceListener, Boolean>());

    // the set of device listeners to keep updated about device value changes
    private Set<DeviceValueListener> deviceValueListeners = Collections.newSetFromMap(new ConcurrentHashMap<DeviceValueListener, Boolean>());

    // Initialize the update delivery executor thread pool
    private ExecutorService deviceUpdateDeliveryExecutor = Executors.newCachedThreadPool();

    // The set of known devices
    private PersistentDeviceSet knownDevices;

    /**
     * Initialises the DeviceManager
     */
    public DeviceManager() {

        // Clear the listeners

        deviceListeners.clear();
        deviceValueListeners.clear();

        // Stop all jobs and clear the thread pool

        deviceUpdateDeliveryExecutor.shutdownNow();
        deviceUpdateDeliveryExecutor = Executors.newCachedThreadPool();

        // Initialise the device eepRegistry from the passed file if necessary

        knownDevices = new PersistentDeviceSet();
        logger.info("Initialised EnOcean device manager - {} EEP profiles", EEPRegistry.getProfiles().size());
    }

    /**
     * Adds a device listener to the set of listeners to be notified about
     * device events: creation, modification, deletion.
     *
     * @param listener The {@link DeviceListener} to notify to.
     */
    public void addDeviceListener(DeviceListener listener) {
        // Store the listener in the set of currently active listeners

        deviceListeners.add(listener);

        // Immediately notify this listener of all the existing devices

        for (EnOceanDevice device : knownDevices.values()) {
            notifyDeviceValueListeners(device, DeviceChangeType.CREATED);
        }
    }

    /**
     * Removes a device listener from the ste of listeners to be notified about
     * device events.
     *
     * @param listener The {@link DeviceListener} to remove.
     * @return true if removal was successful, false, otherwise.
     */
    public boolean removeDeviceListener(DeviceListener listener) {
        return deviceListeners.remove(listener);
    }

    /**
     * Adds a device listener to the set of listeners to be notified about
     * device value changes
     *
     * @param listener The {@link DeviceListener} to notify to.
     */
    public void addDeviceValueListener(DeviceValueListener listener) {
        deviceValueListeners.add(listener);
    }

    /**
     * Removes a device listener from the ste of listeners to be notified about
     * device value changes
     *
     * @param listener The {@link DeviceListener} to remove.
     * @return true if removal was successful, false, otherwise.
     */
    public boolean removeDeviceValueListener(DeviceValueListener listener) {
        return deviceValueListeners.remove(listener);
    }

    /**
     * Registers a device that can be communicated with
     *
     * @param hexDeviceAddress Hexadecimal address of the device
     * @param eep              The equipment profile for this device
     * @return EnOceanDevice
     */
    public EnOceanDevice registerDevice(String hexDeviceAddress, String eep) {
        byte deviceAddress[] = EnOceanDevice.parseAddress(hexDeviceAddress);
        EEPIdentifier eepId = EEPIdentifier.parse(eep);

        // check if the device is already known
        if (eepId != null) {
            if (knownDevices.getByAddress(deviceAddress) == null) {
                return registerDevice(deviceAddress, null, eepId);
            }
            else {
                return knownDevices.getByAddress(deviceAddress);
            }
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
    public EnOceanDevice registerDevice(byte[] address, byte[] manufacturerId, EEPIdentifier eep) {
        EnOceanDevice device = createDevice(address, manufacturerId, eep);
        if (device != null) {
            return registerDevice(device);
        }
        else {
            return null;
        }
    }

    /**
     * Creates a device that can be communicated with but doesn't register it
     *
     * @param address        Address expressed as a series of bytes as received in a telegram
     * @param manufacturerId The unique manufacturer ID if known
     * @param eep            The equipment profile for this device
     * @return EnOceanDevice
     */
    public static EnOceanDevice createDevice(byte address[], byte manufacturerId[], EEPIdentifier eep) {
        EnOceanDevice device = new EnOceanDevice(address, manufacturerId);
        EEP deviceEEP = EEPRegistry.getEEP(eep);
        if (deviceEEP != null) {
            device.setEEP(deviceEEP);
            return device;
        }
        return null;
    }

    /**
     * Registers a device that can be communicated with
     *
     * @param device Enocean device to register
     * @return EnOceanDevice
     */
    public EnOceanDevice registerDevice(EnOceanDevice device) {
        // notify listeners
        notifyDeviceValueListeners(device, DeviceChangeType.CREATED);

        // store the device
        knownDevices.add(device);
        return device;
    }

    /**
     * Clear all the registered devices
     */
    public void clearRegistry() {
        knownDevices.clear();
    }

    /**
     * Un-registers a device that can be communicated with
     *
     * @param device Enocean device to unregister
     */
    public void unRegisterDevice(EnOceanDevice device) {
        if (knownDevices.getByUID(device.getAddressInt()) != null) {

            // notify listeners
            notifyDeviceValueListeners(device, DeviceChangeType.CREATED);

            // store the device
            knownDevices.remove(device);
        }
    }

    /**
     * Returns the set of currently known devices
     *
     * @return the knownDevices
     */
    public Collection<EnOceanDevice> getDevices() {
        return knownDevices.values();
    }

    /**
     * adds sender address and status to the given payload, thus completing the
     * Returns the EnOcean device having the given UID
     *
     * @param deviceUID Return the device using its decimal ID
     * @return EnOceanDevice or null if the device is not registered
     */
    public EnOceanDevice getDevice(int deviceUID) {
        return knownDevices.getByUID(deviceUID);
    }

    /**
     * Returns the registered device with the given address
     *
     * @param address Address in byte array format
     * @return Device or null if not registered
     */
    public EnOceanDevice getDevice(byte[] address) {
        return knownDevices.getByAddress(address);
    }

    /**
     * Checks to see if the EEP is a supported format
     *
     * @param eep EEP identifier to check
     * @return True if this EEP is supported by enocean4j
     */
    public boolean isEEPSupported(EEPIdentifier eep) {
        return EEPRegistry.isEEPSupported(eep);
    }

    /**
     * Notify all the registered listeners that an event on a device has occurred
     *
     * @param device     Device being actioned
     * @param changeType Type of the change occurring
     */
    public void notifyDeviceValueListeners(EnOceanDevice device, DeviceChangeType changeType) {
        deviceUpdateDeliveryExecutor.execute(new DeviceChangeJob(device, changeType, deviceListeners));
    }

    /**
     * Notify all the registered listeners that an event on a device has occurred
     *
     * @param eepAttributeChangeJob Attribute change values
     */
    public void notifyDeviceValueListeners(EEPAttributeChangeJob eepAttributeChangeJob) {
        for (DeviceValueListener listener : deviceValueListeners) {
            listener.deviceAttributeChange(eepAttributeChangeJob);
        }
    }

    /**
     * Returns a reference to all the supported profiles
     *
     * @return Map of supported EEP profiles
     */
    public Map<EEPIdentifier, EEP> getProfiles() {
        return new HashMap<>(EEPRegistry.getProfiles());
    }

}
