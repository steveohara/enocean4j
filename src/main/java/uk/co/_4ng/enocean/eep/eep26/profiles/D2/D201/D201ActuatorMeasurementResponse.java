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
package uk.co._4ng.enocean.eep.eep26.profiles.D2.D201;

import java.nio.ByteBuffer;

/**
 * @author bonino
 */
class D201ActuatorMeasurementResponse {
    // the command id, always 7
    private int commandId;

    // the channel id
    private int channelId;

    // the input / output flag
    private boolean output;

    // the measure
    private byte measure[];

    // the measure unit
    private D201UnitOfMeasure unit;

    D201ActuatorMeasurementResponse(byte commandId, byte channelId, byte[] measureValue, byte unit) {
        // store the command id
        this.commandId = (int) commandId;

        // store the channel id
        this.channelId = channelId;

        // set the input/output flag
        output = this.channelId < 126; // leaves space for
        // "not used values" to be
        // classified as input
        // store the measure as a byte and the offer means to retrieve his value
        // as int, double or whatever. Performs an array deep copy.
        measure = new byte[measureValue.length];
        System.arraycopy(measureValue, 0, measure, 0, measureValue.length);

        // store the unit of measure
        this.unit = D201UnitOfMeasure.valueOf(unit);
    }

    /**
     * @return the commandId
     */
    int getCommandId() {
        return commandId;
    }

    /**
     * @return the channelId
     */
    int getChannelId() {
        return channelId;
    }

    /**
     * @return the output
     */
    boolean isOutput() {
        return output;
    }

    /**
     * @return the measure
     */
    byte[] getMeasure() {
        return measure;
    }

    /**
     * @return the unit
     */
    D201UnitOfMeasure getUnit() {
        return unit;
    }

    /**
     * Returns the measure value as a double TODO: check measure encoding as it
     * is not specified in the EEP documentation
     *
     * @return Returns the value as a Double
     */
    double getMeasureAsDouble() {
        ByteBuffer buffer = ByteBuffer.wrap(measure);
        return (double) buffer.getInt();
    }

    /**
     * Returns the measure value as an integer.
     *
     * @return Returns the value as an int
     */
    int getMeasureAsInt() {
        ByteBuffer buffer = ByteBuffer.wrap(measure);
        return buffer.getInt();
    }

}
