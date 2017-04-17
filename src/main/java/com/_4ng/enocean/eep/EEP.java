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
package com._4ng.enocean.eep;

import com._4ng.enocean.devices.EnOceanDevice;
import com._4ng.enocean.eep.eep26.telegram.EEP26Telegram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Set;

/**
 * @author bonino
 */
public abstract class EEP implements EEPAttributeChangePublisher {

    private static final Logger logger = LoggerFactory.getLogger(EEP.class);

    // the set of attributes associated to single channels defined by this
    // profile: the key is the channel id. If the EEP has only one channel id
    // the implementing classes might encode all attributes as device-wide
    private HashMap<Integer, HashMap<String, EEPAttribute<?>>> channelAttributes;
    // the set of EEPWide attributes
    private HashMap<String, EEPAttribute<?>> eepAttributes;
    // the EnOcean Equipment Profile version
    private String version;

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
        eepAttributes = new HashMap<>();

        // Assuming we came from a local instantiation, lets work out the signature from the class name

        if (getClass().getSimpleName().matches("[A-Z][0-9][A-Z0-9]+"))
        rorg = new Rorg((byte) (Integer.parseInt(getClass().getSimpleName().substring(0, 2), 16) & 0xff));
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
    public boolean addEEPAttribute(EEPAttribute<?> attribute) {
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
    public EEPAttribute<?> getEEPAttribute(String attributeName) {
        return eepAttributes.get(attributeName);
    }

    /**
     * Returns the names of the eep-wide attributes defined for this {@link EEP}
     * instance.
     *
     * @return The {@link Set}<{@link String}> containing all the names of the
     * currently available {@link EEP}s.
     */
    public Set<String> listEEPAttributes() {
        return eepAttributes.keySet();
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
        HashMap<String, EEPAttribute<?>> channelAttributes = this.channelAttributes.get(channelId);

        // check not null, if null the channel does not exist and must be
        // created
        if (channelAttributes == null) {
            // create the channel attributes map
            channelAttributes = new HashMap<>();

            // store the map
            this.channelAttributes.put(channelId, channelAttributes);
        }

        // check if the insertion was successful
        channelAttributes.put(attribute.getName(), attribute);
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
        HashMap<String, EEPAttribute<?>> channelAttributes = this.channelAttributes.get(channelId);

        // if channel-specific attributes are available
        if (channelAttributes != null)
        // store the requested attribute, if present
        {
            attribute = channelAttributes.get(attributeName);
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
    public Set<String> listChannelAttributes(Integer channelId) {
        // get the channel specific attributes
        HashMap<String, EEPAttribute<?>> attributes = channelAttributes.get(channelId);

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
    public boolean addEEP26AttributeListener(int channelId, String attributeName, EEPAttributeChangeListener listener) {
        // the success flag, initially false
        boolean success = false;

        // the map of attributes associated to the given channel
        HashMap<String, EEPAttribute<?>> attributes = channelAttributes.get(channelId);

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
    public boolean removeEEP26AttributeListener(int channelId, String attributeName, EEPAttributeChangeListener listener) {
        // the success flag, initially false
        boolean success = false;

        // the map of attributes associated to the given channel
        HashMap<String, EEPAttribute<?>> attributes = channelAttributes.get(channelId);

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
        return "EEP{" + "channelAttributes=" + channelAttributes.size() + ", eepAttributes=" + eepAttributes.size() + ", version='" + version + '\'' + '}';
    }

    /**
     * Handles the profile data update, must be specifically implemented by each
     * profile class
     */
    public boolean handleUpdate(EEP26Telegram telegram, EnOceanDevice device) {
        logger.debug("Handling telegram: {} for device: {}", getEEPIdentifier(), device);
        return handleProfileUpdate(telegram, device);
    }

    /**
     * Return the eep identifier associated to this EEP
     *
     * @return Returns an EEP identifier object
     */
    public EEPIdentifier getEEPIdentifier() {
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
    protected abstract boolean handleProfileUpdate(EEP26Telegram telegram, EnOceanDevice device);
}
