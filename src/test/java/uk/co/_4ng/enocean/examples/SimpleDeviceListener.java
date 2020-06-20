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
package uk.co._4ng.enocean.examples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co._4ng.enocean.communication.DeviceListener;
import uk.co._4ng.enocean.communication.DeviceValueListener;
import uk.co._4ng.enocean.communication.TeachInListener;
import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.EEPAttributeChangeJob;

/**
 * @author bonino
 */
public class SimpleDeviceListener implements DeviceListener,DeviceValueListener,TeachInListener {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDeviceListener.class);
    private final DeviceManager deviceManager;

    public SimpleDeviceListener(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @Override
    public void addedEnOceanDevice(EnOceanDevice device) {
        logger.info("Added device: {} ({})", device.getAddressHex(), device.getEEP().getIdentifier());
    }

    @Override
    public void modifiedEnOceanDevice(EnOceanDevice device) {
        logger.info("Modified device: {} ({})", device.getAddressHex(), device.getEEP().getIdentifier());
    }

    @Override
    public void removedEnOceanDevice(EnOceanDevice device) {
        logger.info("Removed device: {} ({})", device.getAddressHex(), device.getEEP().getIdentifier());
    }

    @Override
    public void deviceAttributeChange(EEPAttributeChangeJob eepAttributeChangeJob) {
        for (EEPAttributeChangeJob.EEPAttributeChange attr : eepAttributeChangeJob.getChanges()) {
            logger.info("Device: {} Channel: {} Attribute: {} Value: {}", attr.getDevice().getAddressHex(), attr.getChannelId(), attr.getAttribute().getName(), attr.getAttribute().getValue());
        }
    }

    @Override
    public void teachInEnabled() {
        logger.info("Teach-in enabled");
    }

    @Override
    public void teachInDisabled() {
        logger.info("Teach-in disabled");
    }

    @Override
    public void foundNewDevice(EnOceanDevice device) {
        logger.info("Teach-in found: {} registering", device);
        deviceManager.registerDevice(device);
    }

    @Override
    public void foundRegisteredDevice(EnOceanDevice device) {
        logger.info("Teach-in found: {} already registered", device);
    }
}
