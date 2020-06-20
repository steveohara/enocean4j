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
import uk.co._4ng.enocean.eep.EEP;
import uk.co._4ng.enocean.util.EnOceanUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * A class representing EnOcean devices, it allows easier composition of
 * device-related packets and easier deconding / encoding of device-specific
 * information. It supports typically one (but many are permitted) EnOcean
 * Equipment Profile which defines the device functions and capabilities.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * @author <a href="mailto:biasiandrea04@gmail.com">Andrea Biasi </a>
 */
public class EnOceanDevice implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(EnOceanDevice.class);

    /**
     * The class version, to be used in serialization / de-serialization
     */
    private static final long serialVersionUID = 1L;
    // the device address
    private byte[] address = new byte[4];
    // the device manufacturer
    private byte[] manufacturerId = new byte[3];
    // the set of supported profiles
    private transient EEP profile;

    /**
     * Creates a new {@link EnOceanDevice} instance representing a physical
     * device having one or more EnOcean EquipmentProfiles.
     *
     * @param address        The low level address of the device
     * @param manufacturerId The low level manufacturer id associated to the device
     */
    public EnOceanDevice(byte[] address, byte[] manufacturerId) {
        // store the device address
        this.address = address;

        // store the manufacturer id
        this.manufacturerId = manufacturerId;
    }

    /**
     * Given a device address expressed in the low level notation (array of
     * bytes) returns the device address in the high-level notation.
     *
     * @param address The low-level address to convert.
     * @return the corresponding high level identifier.
     */
    public static int byteAddressToUID(byte[] address) {
        return ByteBuffer.wrap(address).getInt();
    }

    /**
     * Parses a device address expressed as an hexadecimal string
     *
     * @param hexDeviceAddress Device address in hex format
     * @return Address in byte array
     */
    public static byte[] parseAddress(String hexDeviceAddress) {

        // allowed format for Device address is with or without dashes

        hexDeviceAddress = hexDeviceAddress.replaceAll("-|(0x)", "").trim().toLowerCase();
        hexDeviceAddress = "00000000".substring(hexDeviceAddress.length() > 8 ? 8 : hexDeviceAddress.length()) + hexDeviceAddress;

        // prepare the byte[] for hosting the address
        byte[] address = new byte[4];

        // parse the address
        if (hexDeviceAddress.length() == 8) {
            for (int i = 0; i < hexDeviceAddress.length(); i += 2) {
                address[i / 2] = (byte) Integer.parseInt(hexDeviceAddress.substring(i, i + 2), 16);
            }
        }
        return address;
    }

    /**
     * Provides back the low level address for this device.
     *
     * @return the address as a byte array.
     */
    public byte[] getAddress() {
        return address;
    }

    /**
     * Returns the Hex representation of the address
     *
     * @return Hex string
     */
    public String getAddressHex() {
        return EnOceanUtils.toHexString(address);
    }

    /**
     * Provides back the low level manufacturer id of this device
     *
     * @return the manufacturerId
     */
    public byte[] getManufacturerId() {
        return manufacturerId;
    }

    /**
     * Get the <code>&lt;T extends {@link EEP}&gt;</code> instance associated to
     * implemented bythis device.
     *
     * @return the <code>&lt;T extends {@link EEP}&gt;</code> instance, if
     * present, or null otherwise.
     */
    public EEP getEEP() {
        return profile;
    }

    /**
     * Associates this device instance with a newly created instance of EnOcean
     * Equipment Profile, of the given class.
     *
     * @param eep The corresponding EEP, which must extend {@link EEP}
     */
    public void setEEP(EEP eep) {
        profile = eep;
    }

    /**
     * Provides back the device address as an integer. The returned id is meant
     * to be used for "detecting" the same device at the application level.
     * Currently no backward translation to low level address is supported.
     *
     * @return The device address as an integer number.
     */
    public int getAddressInt() {
        return ByteBuffer.wrap(address).getInt();
    }

    /**
     * Provides back the device manufacturer id as an integer. The returned id
     * is meant to be used as manufacturer identifier. Currently, no back
     * translation is supported.
     *
     * @return The manufacturer id as an integer.
     */
    public int getManufacturerUID() {
        return ByteBuffer.wrap(manufacturerId).getInt();
    }

    @Override
    public String toString() {
        return "EnOceanDevice{" + "address=" + EnOceanUtils.toHexString(address) + ", manufacturerId=" + EnOceanUtils.toHexString(manufacturerId) + ", profile=" + profile + '}';
    }
}