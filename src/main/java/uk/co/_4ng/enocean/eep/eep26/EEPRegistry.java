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

package uk.co._4ng.enocean.eep.eep26;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co._4ng.enocean.eep.EEP;
import uk.co._4ng.enocean.eep.EEPIdentifier;
import uk.co._4ng.enocean.eep.Rorg;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A502.*;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A504.A50401;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A504.A50402;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A504.A50403;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A507.A50701;
import uk.co._4ng.enocean.eep.eep26.profiles.A5.A509.A50904;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D20108;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D20109;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D2010A;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D232.D23200;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D232.D23201;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D232.D23202;
import uk.co._4ng.enocean.eep.eep26.profiles.D5.D500.D50001;
import uk.co._4ng.enocean.eep.eep26.profiles.F6.F602.F60201;
import uk.co._4ng.enocean.eep.eep26.profiles.F6.F602.F60202;
import uk.co._4ng.enocean.eep.eep26.profiles.F6.F610.F61000;
import uk.co._4ng.enocean.eep.eep26.profiles.F6.F610.F61001;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Static list of all supported EEP
 */
public class EEPRegistry {

    private static final Logger logger = LoggerFactory.getLogger(EEPRegistry.class);

    // the set of supported profiles
    private static final Map<EEPIdentifier, EEP>  supportedProfiles = new ConcurrentHashMap<>();

    static {

        // build the profiles cache
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
        addProfile(A50402.class);
        addProfile(A50403.class);

        addProfile(A50701.class);

        addProfile(A50904.class);

        addProfile(D20108.class);
        addProfile(D20109.class);
        addProfile(D2010A.class);

        addProfile(D23200.class);
        addProfile(D23201.class);
        addProfile(D23202.class);

        addProfile(D50001.class);

        addProfile(F60201.class);
        addProfile(F60202.class);
        addProfile(F61000.class);
        addProfile(F61001.class);
    }

    private EEPRegistry() {
    }

    // static method for checking if the given profile is supported by the
    // current EnJ api.
    public static boolean isEEPSupported(EEPIdentifier eep) {
        return supportedProfiles.containsKey(eep);
    }

    /**
     * Add a custom prfile to the library
     *
     * @param profile   Class of the profile
     */
    public static void addProfile(Class<? extends EEP> profile) {
        if (profile != null) {
            try {
                EEP eep = profile.newInstance();
                supportedProfiles.put(eep.getIdentifier(), eep);
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
    public static EEP getEEP(EEPIdentifier eepId) {
        return supportedProfiles.get(eepId);
    }

    /**
     * Returna a reference to all the supported profiles
     *
     * @return Map of supported EEP profiles
     */
    public static Map<EEPIdentifier, EEP> getProfiles() {
        return supportedProfiles;
    }

    /**
     * Returns a reference to all the supported profiles for the given RORG
     *
     * @param rorg Rorg to get profiles for
     * @return Map of supported EEP profiles
     */
    public static Map<EEPIdentifier, EEP> getProfiles(int rorg) {
        Map<EEPIdentifier, EEP> returnValue = new HashMap<>();
        for (EEP eep : getProfiles().values()) {
            if (eep.getRorg().getRorgValue() == rorg) {
                returnValue.put(eep.getIdentifier(), eep);
            }
        }
        return returnValue;
    }

    /**
     * Returns a reference to all the supported profiles for the given RORG and function
     *
     * @param rorg     Rorg to get profiles for
     * @param function Function to match
     * @return Map of supported EEP profiles
     */
    public static Map<EEPIdentifier, EEP> getProfiles(int rorg, int function) {
        Map<EEPIdentifier, EEP> returnValue = new HashMap<>();
        for (EEP eep : getProfiles().values()) {
            if (eep.getRorg().getRorgValue() == rorg && eep.getFunction() == function) {
                returnValue.put(eep.getIdentifier(), eep);
            }
        }
        return returnValue;
    }

    /**
     * Get the EEP profile for the given EEP identifier
     *
     * @param rorg     Rorg to get profiles for
     * @param function Function to match
     * @param type Type to match
     * @return EEP profile or null if not supported
     */
    public static EEP getEEP(Rorg rorg, byte function, byte type) {
        return getEEP(new EEPIdentifier(rorg, function, type));
    }


}
