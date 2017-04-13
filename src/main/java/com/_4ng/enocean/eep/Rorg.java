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

package com._4ng.enocean.eep;

import java.io.Serializable;

public class Rorg implements Serializable {

    public static final byte RPS = (byte) 0xF6;
    public static final byte BS1 = (byte) 0xD5;
    public static final byte BS4 = (byte) 0xA5;
    public static final byte VLD = (byte) 0xD2;
    public static final byte MSC = (byte) 0xF1;
    public static final byte ADT = (byte) 0xA6;
    public static final byte SM_LRN_REQ = (byte) 0xC6;
    public static final byte SM_LRN_ANS = (byte) 0xC7;
    public static final byte SM_LRN_REC = (byte) 0xA7;
    public static final byte SYS_EX = (byte) 0xA6;
    public static final byte SEC = (byte) 0x30;
    public static final byte SEC_ENCAPS = (byte) 0x31;
    public static final byte UTE = (byte) 0xD4;
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    byte rorgValue;

    /**
     * @param rorgValue
     */
    public Rorg(byte rorgValue) {
        this.rorgValue = rorgValue;
    }

    /**
     * @return the rorgValue
     */
    public byte getRorgValue() {
        return rorgValue;
    }

    /**
     * @param rorg the rorgValue to set
     */
    public void setRorgValue(byte rorg) {
        rorgValue = rorg;
    }

    public boolean isRps() {
        return rorgValue == RPS;
    }

    public boolean isBS1() {
        return rorgValue == BS1;
    }

    public boolean isBS4() {
        return rorgValue == BS4;
    }

    public boolean isVld() {
        return rorgValue == VLD;
    }

    public boolean isMsc() {
        return rorgValue == MSC;
    }

    public boolean isAdt() {
        return rorgValue == ADT;
    }

    public boolean isSmLrnReq() {
        return rorgValue == SM_LRN_REQ;
    }

    public boolean isSmLrnAns() {
        return rorgValue == SM_LRN_ANS;
    }

    public boolean isSmLrnRec() {
        return rorgValue == SM_LRN_REC;
    }

    public boolean isSysEx() {
        return rorgValue == SYS_EX;
    }

    public boolean isSec() {
        return rorgValue == SEC;
    }

    public boolean isSecEncaps() {
        return rorgValue == SEC_ENCAPS;
    }

    public boolean isUTE() {
        return rorgValue == UTE;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + rorgValue;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        Rorg other = (Rorg) obj;
        return rorgValue == other.rorgValue;
    }

    @Override
    public String toString() {
        return "Rorg{" + "rorgValue=" + String.format("0x%02X", rorgValue) + '}';
    }
}
