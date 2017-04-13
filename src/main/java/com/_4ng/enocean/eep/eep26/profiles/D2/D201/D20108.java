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
import com._4ng.enocean.eep.EEPIdentifier;
import com._4ng.enocean.eep.eep26.attributes.*;

/**
 * @author Andrea Biasi <biasiandrea04@gmail.com>
 */
public class D20108 extends D20109 {

    // the type definition
    public static final byte TYPE = (byte) 0x08;
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
    public D20108() {

        // add the supported functions
        addChannelAttribute(CHANNEL, new EEP26Switching());
        addChannelAttribute(CHANNEL, new EEP26LocalControl());
        addChannelAttribute(CHANNEL, new EEP26UserInterfaceMode());
        addChannelAttribute(CHANNEL, new EEP26OverCurrentShutdown());
        addChannelAttribute(CHANNEL, new EEP26OverCurrentShutdownReset());
        addChannelAttribute(CHANNEL, new EEP26OverCurrentSwitchOff());
        addChannelAttribute(CHANNEL, new EEP26EnergyMeasurement());
        addChannelAttribute(CHANNEL, new EEP26PowerMeasurement());
        addChannelAttribute(CHANNEL, new EEP26DefaultState());
        addChannelAttribute(CHANNEL, new EEP26ErrorLevel());
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
     * Asks for the current power or energy measurement on a given channel Id of
     * a given EnOcean actuator
     *
     * @param connection
     * @param deviceAddress
     * @param powerMode
     * @param channelId
     */
    public void actuatorMeasurementQuery(Connection connection, byte[] deviceAddress, boolean powerMode, int channelId) {
        // get the measurement mode as a byte value
        byte powerModeAsByte = powerMode ? (byte) 0x01 : (byte) 0x00;

        // call the superclass method
        actuatorMeasurementQuery(connection, deviceAddress, powerModeAsByte, (byte) channelId);
    }

    @Override
    public EEPIdentifier getEEPIdentifier() {
        return new EEPIdentifier(RORG, FUNC, TYPE);
    }
}