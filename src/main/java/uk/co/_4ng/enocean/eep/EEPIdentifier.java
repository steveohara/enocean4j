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

package uk.co._4ng.enocean.eep;

import uk.co._4ng.enocean.util.EnOceanUtils;

import java.io.Serializable;

public class EEPIdentifier implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // Identify the Radio-Telegram organization
    private Rorg rorg;
    // Function of the device
    private byte function;
    // Type of device
    private byte type;

    /**
     * Constructor
     *
     * @param rorg     RORG value
     * @param function Function code
     * @param type     Type coe
     */
    public EEPIdentifier(int rorg, int function, int type) {
        this(new Rorg((byte) rorg), (byte) (function & 0xff), (byte) (type & 0xff));
    }

    /**
     * Constructor
     *
     * @param rorg     RORG value
     * @param function Function code
     * @param type     Type coe
     */
    public EEPIdentifier(Rorg rorg, byte function, byte type) {
        this.rorg = rorg;
        this.function = function;
        this.type = type;
    }

    public static EEPIdentifier parse(String eepIdentifierAsString) {
        EEPIdentifier identifier = null;

        // allowed format for EEPIdentifier is with or without dashes
        if (eepIdentifierAsString.contains("-")) {
            eepIdentifierAsString = eepIdentifierAsString.replace("-", "");
        }

        // trim leading and trailing spaces
        eepIdentifierAsString = eepIdentifierAsString.trim();

        // check the right length
        if (eepIdentifierAsString.length() == 6 || eepIdentifierAsString.length() == 4) {
            // parses the eep identifier expressed according to the EEP
            // specification, i.e., rrfftt
            byte rorg = (byte) Integer.parseInt(eepIdentifierAsString.substring(0, 2), 16);
            byte func = (byte) Integer.parseInt(eepIdentifierAsString.substring(2, 4), 16);

            // TODO handle higher EEP e.g. D201
            byte type = (byte) 0xff;
            if (eepIdentifierAsString.length() == 6) {
                type = (byte) Integer.parseInt(eepIdentifierAsString.substring(4, 6), 16);
            }

            identifier = new EEPIdentifier(new Rorg(rorg), func, type);
        }

        return identifier;
    }

    public static boolean isPartOf(EEPIdentifier part, EEPIdentifier whole) {
        // checks if the part and the whole have the same rorg and function
        return part.rorg.getRorgValue() == whole.rorg.getRorgValue() && part.function == whole.function;
    }

    /**
     * @return the rorg
     */
    public Rorg getRorg() {
        return rorg;
    }

    /**
     * @param rorg the rorg to set
     */
    public void setRorg(Rorg rorg) {
        this.rorg = rorg;
    }

    /**
     * @return the function
     */
    public byte getFunction() {
        return function;
    }

    /**
     * @param function the function to set
     */
    public void setFunction(byte function) {
        this.function = function;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * Returns the EEPIdentifier in form of an EEP identifier string with the dash notation
     *
     * @return Returns the EEP as the '-' notation
     */
    @Override
    public String toString() {
        return EnOceanUtils.toHexString(rorg.getRorgValue()).substring(2) + "-" + EnOceanUtils.toHexString(function).substring(2) + "-" + EnOceanUtils.toHexString(type).substring(2);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + function;
        result = prime * result + (rorg == null ? 0 : rorg.hashCode());
        result = prime * result + type;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EEPIdentifier)) {
            return false;
        }
        EEPIdentifier other = (EEPIdentifier) obj;
        if (function != other.function) {
            return false;
        }
        if (rorg == null) {
            if (other.rorg != null) {
                return false;
            }
        }
        else if (!rorg.equals(other.rorg)) {
            return false;
        }
        return type == other.type;
    }

}
