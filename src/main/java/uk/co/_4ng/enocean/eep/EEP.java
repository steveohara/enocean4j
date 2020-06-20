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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.eep26.telegram.EEP26Telegram;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author bonino
 */
public abstract class EEP implements EEPAttributeChangePublisher {

    private static final Logger logger = LoggerFactory.getLogger(EEP.class);

    // the set of attributes associated to single channels defined by this
    // profile: the key is the channel id. If the EEP has only one channel id
    // the implementing classes might encode all attributes as device-wide
    private final Map<Integer, Map<String, EEPAttribute<?>>> channelAttributes;

    // the set of EEPWide attributes
    private final Map<String, EEPAttribute<?>> eepAttributes;

    // the EnOcean Equipment Profile version
    private final String version;

    // Convenience storage for the EEP identifier components
    protected Rorg rorg;
    protected byte type;
    protected byte function;

    public EEP() {
        this("2.6");
    }

    /**
     *
     */
    public EEP(String version) {
        // store the version number
        this.version = version;

        // initialize the channel specific attributes
        channelAttributes = new HashMap<>();

        // initialise the eep wide attributes
        eepAttributes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        // Assuming we came from a local instantiation, lets work out the signature from the class name

        if (getClass().getSimpleName().matches("[A-Z][0-9][A-Z0-9]+")) {
            rorg = new Rorg((byte) (Integer.parseInt(getClass().getSimpleName().substring(0, 2), 16) & 0xff));
        }
        function = (byte) (Integer.parseInt(getClass().getSimpleName().substring(2, 4), 16) & 0xff);
        type = (byte) (Integer.parseInt(getClass().getSimpleName().substring(4, 6), 16) & 0xff);
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Adds an EEPWide attribute to this {@link EEP} instance
     *
     * @param attribute the attribute to add
     * @return true if successful, false otherwise.
     */
    public boolean addAttribute(EEPAttribute<?> attribute) {
        // the operation result, initially false
        boolean stored = false;

        // check if the insertion was successful
        if (eepAttributes.put(attribute.getClass().getSimpleName(), attribute).equals(attribute)) {
            stored = true;
        }

        // return the insertion result
        return stored;
    }

    /**
     * Returns the eep-wide attribute having the given name, or null if no
     * attribute with the given name is available.
     *
     * @param attributeName The name of the attribute to retrieve.
     * @return The corresponding {@link EEPAttribute} instance
     */
    public EEPAttribute<?> getAttribute(String attributeName) {
        return eepAttributes.get(attributeName);
    }

    /**
     * Returns the names of the eep-wide attributes defined for this {@link EEP}
     * instance.
     *
     * @return The {@link Set}<{@link String}> containing all the names of the
     * currently available {@link EEP}s.
     */
    public Set<String> getAttributes() {
        return eepAttributes.keySet();
    }

    /**
     * Returns the names of all the eep-wide and channel attributes defined for this {@link EEP}
     * instance.
     *
     * @return The {@link Set}<{@link String}> containing all the names of the
     * currently available {@link EEP}s.
     */
    public Set<String> getAllAttributes() {
        Set<String> returnValue = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        returnValue.addAll(eepAttributes.keySet());
        if (channelAttributes.size() < 2) {
            for (Map<String, EEPAttribute<?>> set : channelAttributes.values()) {
                returnValue.addAll(set.keySet());
            }
        }
        else {
            for (Entry<Integer, Map<String, EEPAttribute<?>>> channel : channelAttributes.entrySet()) {
                for (String key : channel.getValue().keySet()) {
                    returnValue.add(String.format("%s (channel %d)", key, channel.getKey()));
                }
            }
        }
        return returnValue;
    }

    /**
     * Adds an channel-specific attribute to this {@link EEP} instance
     *
     * @param channelId The id of the channel to which the attribute must be
     *                  associated.
     * @param attribute the attribute to add
     */
    public void addChannelAttribute(Integer channelId, EEPAttribute<?> attribute) {

        // the channel-specific attribute map
        Map<String, EEPAttribute<?>> channelAttrs = this.channelAttributes.get(channelId);

        // check not null, if null the channel does not exist and must be
        // created
        if (channelAttrs == null) {
            // create the channel attributes map
            channelAttrs = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

            // store the map
            this.channelAttributes.put(channelId, channelAttrs);
        }

        // check if the insertion was successful
        channelAttrs.put(attribute.getName(), attribute);
    }

    /**
     * Returns the channel specific attribute having the given name, or null
     * if no attribute with the given name is available.
     *
     * @param channelId     The channel id.
     * @param attributeName The name of the attribute to retrieve.
     * @return The corresponding {@link EEPAttribute} instance
     */
    public EEPAttribute<?> getChannelAttribute(Integer channelId, String attributeName) {
        // prepare the attribute holder
        EEPAttribute<?> attribute = null;

        // get the channel-specific attributes
        Map<String, EEPAttribute<?>> channelAttrs = this.channelAttributes.get(channelId);

        // if channel-specific attributes are available
        if (channelAttrs != null)
        // store the requested attribute, if present
        {
            attribute = channelAttrs.get(attributeName);
        }

        // return the extracted attribute, will be null if the attribute does
        // not exist.
        return attribute;
    }

    /**
     * Returns the names of the channel-specific attributes defined for this
     * {@link EEP} instance, on the given channel.
     *
     * @param channelId The id of the channel.
     * @return The {@link Set}<{@link String}> containing all the names of the
     * currently available {@link EEP}s.
     */
    public Set<String> getChannelAttributes(Integer channelId) {
        // get the channel specific attributes
        Map<String, EEPAttribute<?>> attributes = channelAttributes.get(channelId);

        // return the channel attribute names if the channel exist and there are
        // attributes associated to the channel, false otherwise.
        return attributes != null ? attributes.keySet() : null;
    }

    /**
     * Returns the number of channels supported by this {@link EEP} instance.
     *
     * @return The number of channels as an Integer.
     */
    public int getNumberOfChannels() {
        return channelAttributes.size();
    }

    @Override
    public boolean addAttributeListener(int channelId, String attributeName, EEPAttributeChangeListener listener) {
        // the success flag, initially false
        boolean success = false;

        // the map of attributes associated to the given channel
        Map<String, EEPAttribute<?>> attributes = channelAttributes.get(channelId);

        // get the required attribute name
        EEPAttribute<?> attribute = attributes.get(attributeName);

        // if not null, register the listener and store the result of the
        // process
        if (attribute != null) {
            success = attribute.addAttributeChangeListener(listener);
        }

        // return the final status of the registration (either true or false)
        return success;
    }

    @Override
    public boolean removeAttributeListener(int channelId, String attributeName, EEPAttributeChangeListener listener) {
        // the success flag, initially false
        boolean success = false;

        // the map of attributes associated to the given channel
        Map<String, EEPAttribute<?>> attributes = channelAttributes.get(channelId);

        // get the required attribute name
        EEPAttribute<?> attribute = attributes.get(attributeName);

        // if not null, remove the listener and store the operation result
        if (attribute != null) {
            success = attribute.removeAttributeChangeListener(listener);
        }

        // return the final status of the registration (either true or false)
        return success;
    }

    @Override
    public String toString() {
        return "EEP{" + "channelAttributes=" + channelAttributes.size() +
                ", eepAttributes=" + eepAttributes.size() +
                ", version='" + version + '\'' +
                ", rorg=" + rorg +
                ", type=" + type +
                ", function=" + function +
                '}';
    }

    /**
     * Handles the profile data update, must be specifically implemented by each
     * profile class
     */
    public boolean handleUpdate(DeviceManager deviceManager, EEP26Telegram telegram, EnOceanDevice device) {
        logger.debug("Handling telegram: {} for device: {}", getIdentifier(), device);
        return handleProfileUpdate(deviceManager, telegram, device);
    }

    /**
     * Return the eep identifier associated to this EEP
     *
     * @return Returns an EEP identifier object
     */
    public EEPIdentifier getIdentifier() {
        // return the EEPIdentifier for this profile
        return new EEPIdentifier(rorg, function, type);
    }

    public Rorg getRorg() {
        return rorg;
    }

    public byte getType() {
        return type;
    }

    public byte getFunction() {
        return function;
    }

    /**
     * Handles the profile data update, must be specifically implemented by each
     * profile class
     */
    protected abstract boolean handleProfileUpdate(DeviceManager deviceManager, EEP26Telegram telegram, EnOceanDevice device);
}
