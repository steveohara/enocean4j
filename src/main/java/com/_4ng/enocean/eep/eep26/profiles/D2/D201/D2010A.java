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
package com._4ng.enocean.eep.eep26.profiles.D2.D201;

import com._4ng.enocean.communication.Connection;
import com._4ng.enocean.eep.EEPAttribute;
import com._4ng.enocean.eep.EEPIdentifier;
import com._4ng.enocean.eep.eep26.attributes.*;

/**
 * @author bonino
 */
public class D2010A extends D201 {
    // the type definition
    public static final byte TYPE = (byte) 0x0A;
    // the ON command byte
    public static final byte ON_BYTE = (byte) 0x64;
    // the OFF command byte
    public static final byte OFF_BYTE = (byte) 0x00;
    // the byte identifier for all output channels
    public static final byte ALL_OUTPUT_CHANNEL = 0x1E;
    // the used channel
    public static final int CHANNEL = 0;
    // the ON state / command
    public static boolean ON = true;
    // the OFF state / command
    public static boolean OFF = false;

    // the "data" fields accessible through this eep (and updated upon network
    // data reception)

    /**
     * Builds a new EEPProfile instance of type D2.01.08 as specified in the
     * EEP2.6 specification.
     */
    public D2010A() {

        // add the supported functions
        addChannelAttribute(CHANNEL, new EEP26Switching());
        addChannelAttribute(CHANNEL, new EEP26LocalControl());
        addChannelAttribute(CHANNEL, new EEP26UserInterfaceMode());
        addChannelAttribute(CHANNEL, new EEP26DefaultState());
        addChannelAttribute(CHANNEL, new EEP26PowerFailure());
        addChannelAttribute(CHANNEL, new EEP26PowerFailureDetection());
        addChannelAttribute(CHANNEL, new EEP26OverCurrentSwitchOff());
    }

    // execution commands
    public void actuatorSetOuput(Connection connection, byte[] deviceAddress, boolean command) {
        // exec the command by using the D201 general purpose implementation
        actuatorSetOutput(connection, deviceAddress, D201DimMode.SWITCH_TO_NEW_OUTPUT_VALUE.getCode(), ALL_OUTPUT_CHANNEL, command ? ON_BYTE : OFF_BYTE);
    }

    public void actuatorSetOuput(Connection connection, byte[] deviceAddress, int dimValue, D201DimMode dimMode) {
        // check limits
        if (dimValue < 0) {
            dimValue = 0;
        }
        else if (dimValue > 100) {
            dimValue = 100;
        }

        actuatorSetOutput(connection, deviceAddress, dimMode.getCode(), ALL_OUTPUT_CHANNEL, (byte) dimValue);

    }

    /**
     * Updates the configuration of the physical actuator having this EEP
     * profile with values from the given set of attributes. Attributes not
     * being part of the acceptable configuration parameters will be simply
     * ignored.
     *
     * @param connection    The {@link Connection} object enabling physical layer
     *                      communication
     * @param deviceAddress The physical layer address of the device
     * @param attributes    The configuration attributes to set
     */
    public void actuatorSetLocal(Connection connection, byte[] deviceAddress, int channelId, EEPAttribute<?>[] attributes, D201DimTime dimTime1, D201DimTime dimTime2, D201DimTime dimTime3) {
        // the over current shutdown settings (enabled / disabled), disabled by
        // default
        byte overCurrentShutDown = 0x00;

        // the reset behavior in overcurrent shutdown cases
        byte resetOverCurrentShutDown = 0x00;

        // the local control enabling flag
        byte localControl = 0x00;

        // the user interface mode (either day or night)
        byte userInterfaceIndication = 0x00;

        // the power failure flag
        byte powerFailure = 0x00;

        // the default state to set when the actuator is powered
        byte defaultState = 0x00;

        // extract the attributes
        // TODO: find a better way to perform such operations, if possible
        for (EEPAttribute<?> attribute : attributes) {
            if (attribute instanceof EEP26LocalControl) {
                localControl = attribute.byteValue()[0];
            }
            else if (attribute instanceof EEP26OverCurrentShutdown) {
                overCurrentShutDown = attribute.byteValue()[0];
            }
            else if (attribute instanceof EEP26OverCurrentShutdownReset) {
                resetOverCurrentShutDown = attribute.byteValue()[0];
            }
            else if (attribute instanceof EEP26UserInterfaceMode) {
                userInterfaceIndication = attribute.byteValue()[0];
            }
            else if (attribute instanceof EEP26PowerFailure) {
                powerFailure = attribute.byteValue()[0];
            }
            else if (attribute instanceof EEP26DefaultState) {
                defaultState = attribute.byteValue()[0];
            }
        }

        // call the superclass method for setting the device configuration
        actuatorSetLocal(connection, deviceAddress, (byte) channelId, localControl, overCurrentShutDown, resetOverCurrentShutDown, userInterfaceIndication, powerFailure, defaultState, dimTime1, dimTime2, dimTime3);
    }

    @Override
    public EEPIdentifier getEEPIdentifier() {
        return new EEPIdentifier(RORG, FUNC, TYPE);
    }


}
