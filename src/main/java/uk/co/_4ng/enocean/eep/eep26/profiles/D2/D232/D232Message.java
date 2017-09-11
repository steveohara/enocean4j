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
package uk.co._4ng.enocean.eep.eep26.profiles.D2.D232;

import uk.co._4ng.enocean.eep.eep26.attributes.EEP26Divisor;

/**
 * A handy place to parse the message bytes into useful features
 */
class D232Message {

    private boolean powerfailed;
    private boolean divisor;
    private boolean valid;
    private boolean teachIn;
    private double[] phases;

    /**
     * Parse the message for the common stuff
     */
    D232Message(byte data[], int channels) {

        valid = false;

        // Check for teachin
        teachIn = data.length == 1;

        // If we're not a teachin, then we must have at least 3 bytes
        if (!teachIn) {

            //decode the power failure status
            powerfailed = (data[0] & 0x80) > 0;

            //decode the divisor
            divisor = (data[0] & 0x40) > 0;

            // Check we have enough bytes
            int bytesRequired = channels == 3 ? 6 : channels == 2 ? 4 : 3;
            if (data.length == bytesRequired) {
                phases = new double[channels];

                // Create a bit stream using
                String bitvalue = "";
                for (int i=1; i<bytesRequired; i++) {
                    bitvalue = String.format("%s%8s", bitvalue, Integer.toBinaryString(data[i] & 0xFF)).replace(' ', '0');
                }

                // Now get the channel values
                int start = 0;
                for (int channel = 0; channel < channels; channel++) {
                    phases[channel] = Integer.parseInt(bitvalue.substring(start, start + 12), 2) / (divisor ==  EEP26Divisor.X10 ? 10.0 : 1.0);
                    start += 12;
                }

                //everything fine, the message is valid
                valid = true;
            }
        }
        else {
            valid = true;
        }
    }

    /**
     * @return the teachIn
     */
    boolean isTeachIn() {
        return teachIn;
    }

    /**
     * @return the powerfailed
     */
    boolean hasPowerFailed() {
        return powerfailed;
    }

    /**
     * @return the divisor
     */
    boolean getDivisor() {
        return divisor;
    }

    /**
     * @return the valid
     */
    boolean isValid() {
        return valid;
    }

    /**
     * Gets the real value for the given channel
     * Will set the invalid flag if the byte count doesn't match the channel count
     *
     * @param channel The phase to get the value for
     * @return double value scaled using the divisor
     */
    double getScaledValue(int channel) {
        double returnValue = 0.0;
        if (phases != null && channel > -1 && channel < phases.length) {
            returnValue = phases[channel];
        }
        return returnValue;
    }


}
