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

package com._4ng.enocean.eep.eep26;

import com._4ng.enocean.eep.EEP;
import com._4ng.enocean.eep.EEPIdentifier;
import com._4ng.enocean.link.PacketDelivery;
import com._4ng.enocean.util.EnOceanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class EEPRegistry {

    private static final Logger logger = LoggerFactory.getLogger(PacketDelivery.class);

    // the set of supported profiles
    private Map<EEPIdentifier, EEP> supportedProfiles;

    // singleton pattern
    public EEPRegistry() {

        // build the profiles hashtable
        supportedProfiles = new HashMap<>();
        try {
            for (Class clazz : EnOceanUtils.getClasses(EEPRegistry.class.getPackage().getName())) {
                if (EEP.class.isAssignableFrom(clazz) && clazz.getSimpleName().matches("[A-Z][0-9A-Z]{5}")) {
                    logger.debug("Found equipment profile: {}", clazz.getSimpleName());
                    EEP eep = (EEP) clazz.newInstance();
                    supportedProfiles.put(eep.getEEPIdentifier(), eep);
                }
            }
        }
        catch (Exception e) {
            logger.error("Cannot find equipment profiles", e);
        }
    }

    // static method for checking if the given profile is supported by the
    // current EnJ api.
    public boolean isEEPSupported(EEPIdentifier eep) {
        return supportedProfiles.containsKey(eep);
    }

    public void addProfile(EEPIdentifier profileId, Class<? extends EEP> profile) {
        if (profileId != null && profile != null) {
            try {
                supportedProfiles.put(profileId, profile.newInstance());
            }
            catch (Exception e) {
                logger.error("Cannot instantiate EEP profile {}", profile, e);
            }
        }
    }

    /**
     * Returns an EEP class given the corresponding EEP identifier
     *
     * @param eepId The EEP identifier.
     * @return The EEP class.
     */
    public EEP getEEP(EEPIdentifier eepId) {
        return supportedProfiles.get(eepId);
    }

    /**
     * Returna a reference to all the supported profiles
     *
     * @return Map of supported EEP profiles
     */
    public Map<EEPIdentifier, EEP> getProfiles() {
        return supportedProfiles;
    }

}
