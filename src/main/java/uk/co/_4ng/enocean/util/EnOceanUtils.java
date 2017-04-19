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
package uk.co._4ng.enocean.util;

/**
 * A class offering static methods for easily handling operations on bytes and
 * byte arrays
 *
 * @author bonino
 */
public class EnOceanUtils {

    /**
     * Given a byte array provides back the corresponding string representation
     * as 0xHHHH. the byte order is assumed to be little endian, i.e., LSB
     * located at higher positions in the array.
     *
     * @param byteArray
     * @return The {@link String} representation of the given byte array.
     */
    public static String toHexString(byte byteArray[]) {
        if (byteArray == null || byteArray.length == 0) {
            return "<empty>";
        }

        // prepare the string buffer for holding the final byte representation
        StringBuilder hexBytes = new StringBuilder();

        // append the hexadecimal notation identifier
        hexBytes.append("0x");

        // convert each byte
        for (byte aByteArray : byteArray) {
            hexBytes.append(String.format("%02X", aByteArray));
        }
        // render the buffer as a string
        return hexBytes.toString();
    }

    /**
     * Given a byte provides back the corresponding string representation as
     * 0xHH.
     *
     * @param theByte to convert.
     * @return The {@link String} representation of the given byte.
     */
    public static String toHexString(byte theByte) {
        return String.format("0x%02X", theByte);
    }
}
