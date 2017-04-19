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

package uk.co._4ng.enocean.eep.eep26;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co._4ng.enocean.eep.EEP;
import uk.co._4ng.enocean.eep.EEPIdentifier;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A502.*;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A504.A50401;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A507.A50701;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D20108;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D20109;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D2010A;
import uk.co._4ng.enocean.eep.eep26.profiles.D5.D500.D50001;
import uk.co._4ng.enocean.eep.eep26.profiles.F6.F602.F60201;
import uk.co._4ng.enocean.eep.eep26.profiles.F6.F602.F60202;
import uk.co._4ng.enocean.eep.eep26.profiles.F6.F610.F61000;
import uk.co._4ng.enocean.eep.eep26.profiles.F6.F610.F61001;
import uk.co._4ng.enocean.link.PacketDelivery;

import java.util.HashMap;
import java.util.Map;

public class EEPRegistry {

    private static final Logger logger = LoggerFactory.getLogger(PacketDelivery.class);

    // the set of supported profiles
    private Map<EEPIdentifier, EEP> supportedProfiles;

    // singleton pattern
    public EEPRegistry() {

        // build the profiles cache
        supportedProfiles = new HashMap<>();
        addProfile(A50201.class);
        addProfile(A50202.class);
        addProfile(A50203.class);
        addProfile(A50204.class);
        addProfile(A50205.class);
        addProfile(A50206.class);
        addProfile(A50207.class);
        addProfile(A50208.class);
        addProfile(A50209.class);
        addProfile(A5020A.class);
        addProfile(A5020B.class);
        addProfile(A50210.class);
        addProfile(A50211.class);
        addProfile(A50212.class);
        addProfile(A50213.class);
        addProfile(A50214.class);
        addProfile(A50215.class);
        addProfile(A50216.class);
        addProfile(A50217.class);
        addProfile(A50218.class);
        addProfile(A50219.class);
        addProfile(A5021A.class);
        addProfile(A5021B.class);
        addProfile(A50220.class);
        addProfile(A50230.class);

        addProfile(A50401.class);

        addProfile(A50701.class);

        addProfile(D20108.class);
        addProfile(D20109.class);
        addProfile(D2010A.class);

        addProfile(D50001.class);

        addProfile(F60201.class);
        addProfile(F60202.class);
        addProfile(F61000.class);
        addProfile(F61001.class);
    }

    // static method for checking if the given profile is supported by the
    // current EnJ api.
    public boolean isEEPSupported(EEPIdentifier eep) {
        return supportedProfiles.containsKey(eep);
    }

    /**
     * Add a custom prfile to the library
     *
     * @param profile   Class of the profile
     */
    public void addProfile(Class<? extends EEP> profile) {
        if (profile != null) {
            try {
                EEP eep = profile.newInstance();
                supportedProfiles.put(eep.getEEPIdentifier(), eep);
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
