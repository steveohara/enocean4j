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
package com._4ng.enocean.examples;

import com._4ng.enocean.enj.communication.EnJDeviceListener;
import com._4ng.enocean.enj.eep.EEPAttributeChangeListener;
import com._4ng.enocean.enj.eep.eep26.attributes.*;
import com._4ng.enocean.enj.model.EnOceanDevice;
import com._4ng.enocean.enj.util.EnOceanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author bonino
 */
public class SimpleDeviceListener implements EnJDeviceListener {

    private static final Logger logger = LoggerFactory.getLogger(SimpleDeviceListener.class);

    public SimpleDeviceListener() {
        // do nothing
    }

    /*
     * (non-Javadoc)
     *
     * @see com._4ng.enocean.enj.communication.EnJDeviceListener#
     * addedEnOceanDevice(com._4ng.enocean.enj.model.EnOceanDevice)
     */
    @Override
    public void addedEnOceanDevice(EnOceanDevice device) {
        logger.info("Added device: {} ({})", device.getDeviceUID(), EnOceanUtils.toHexString(device.getAddress()));

        SimpleMovementListener movementListener = new SimpleMovementListener();

        // handle device types
        if (device.getEEP().getChannelAttribute(0, EEP26RockerSwitch2RockerAction.NAME) != null) {
            device.getEEP().addEEP26AttributeListener(0, EEP26RockerSwitch2RockerAction.NAME, new SimpleRockerSwitchListener());
        }
        if (device.getEEP().getChannelAttribute(0, EEP26TemperatureInverseLinear.NAME) != null) {
            device.getEEP().addEEP26AttributeListener(0, EEP26TemperatureInverseLinear.NAME, new SimpleTemperatureListener());
        }
        if (device.getEEP().getChannelAttribute(0, EEP26Switching.NAME) != null) {
            device.getEEP().addEEP26AttributeListener(0, EEP26Switching.NAME, new SimpleContactSwitchListener());
        }
        if (device.getEEP().getChannelAttribute(0, EEP26PIRStatus.NAME) != null) {
            device.getEEP().addEEP26AttributeListener(0, EEP26PIRStatus.NAME, movementListener);
        }
        if (device.getEEP().getChannelAttribute(0, EEP26SupplyVoltage.NAME) != null) {
            device.getEEP().addEEP26AttributeListener(0, EEP26SupplyVoltage.NAME, movementListener);
        }
        if (device.getEEP().getChannelAttribute(0, EEP26SupplyVoltageAvailability.NAME) != null) {
            device.getEEP().addEEP26AttributeListener(0, EEP26SupplyVoltageAvailability.NAME, movementListener);
        }
        if (device.getEEP().getChannelAttribute(0, EEP26PowerMeasurement.NAME) != null) {
            device.getEEP().addEEP26AttributeListener(0, EEP26PowerMeasurement.NAME, new SimplePowerListener());
        }
        if (device.getEEP().getChannelAttribute(0, EEP26TemperatureLinear.NAME) != null && device.getEEP().getChannelAttribute(0, EEP26HumidityLinear.NAME) != null) {
            EEPAttributeChangeListener listener = new SimpleTemperatureAndHumidityListener();
            device.getEEP().addEEP26AttributeListener(0, EEP26TemperatureLinear.NAME, listener);
            device.getEEP().addEEP26AttributeListener(0, EEP26HumidityLinear.NAME, listener);
        }
        if (device.getEEP().getChannelAttribute(0, EEP26HandleRotation.NAME) != null) {
            device.getEEP().addEEP26AttributeListener(0, EEP26HandleRotation.NAME, new SimpleWindowHandleListener());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com._4ng.enocean.enj.communication.EnJDeviceListener#
     * modifiedEnOceanDevice(com._4ng.enocean.enj.model.EnOceanDevice)
     */
    @Override
    public void modifiedEnOceanDevice(EnOceanDevice device) {
        logger.info("Modified device: {} ({})", device.getDeviceUID(), EnOceanUtils.toHexString(device.getAddress()));
    }

    /*
     * (non-Javadoc)
     *
     * @see com._4ng.enocean.enj.communication.EnJDeviceListener#
     * removedEnOceanDevice(com._4ng.enocean.enj.model.EnOceanDevice)
     */
    @Override
    public void removedEnOceanDevice(EnOceanDevice device) {
        logger.info("Removed device: {} ({})", device.getDeviceUID(), EnOceanUtils.toHexString(device.getAddress()));
    }

}
