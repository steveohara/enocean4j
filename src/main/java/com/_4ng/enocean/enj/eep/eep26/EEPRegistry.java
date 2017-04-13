package com._4ng.enocean.enj.eep.eep26;

import com._4ng.enocean.enj.eep.EEP;
import com._4ng.enocean.enj.eep.EEPIdentifier;
import com._4ng.enocean.enj.link.PacketDelivery;
import com._4ng.enocean.enj.util.EnOceanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EEPRegistry {

    private static final Logger logger = LoggerFactory.getLogger(PacketDelivery.class);
    private static EEPRegistry theInstance;

    // the set of supported profiles
    private Map<EEPIdentifier, Class<? extends EEP>> supportedProfiles;

    // singleton pattern
    private EEPRegistry() {

        // build the profiles hashtable
        supportedProfiles = new HashMap<>();
        Set<Class<? extends EEP>> allEEPs = getStaticEEPs();
        for (Class<? extends EEP> eep : allEEPs) {
            try {
                supportedProfiles.put(eep.newInstance().getEEPIdentifier(), eep);
            }
            catch (InstantiationException | IllegalAccessException e) {
                logger.error("Cannot build EEP registry", e);
            }
        }
    }

    public synchronized static EEPRegistry getInstance() {
        if (theInstance == null) {
            theInstance = new EEPRegistry();
        }
        return theInstance;
    }

    // static method for checking if the given profile is supported by the
    // current EnJ api.
    public boolean isEEPSupported(EEPIdentifier eep) {
        return supportedProfiles.containsKey(eep);
    }

    public void addProfile(EEPIdentifier profileId, Class<? extends EEP> profile) {
        if (profileId != null && profile != null) {
            supportedProfiles.put(profileId, profile);
        }
    }

    /**
     * Returns an EEP class given the corresponding EEP identifier
     *
     * @param eepId The EEP identifier.
     * @return The EEP class.
     */
    public Class<? extends EEP> getEEP(EEPIdentifier eepId) {
        return supportedProfiles.get(eepId);
    }

    /**
     * Find all the classes through scanning
     *
     * @return Set of classes for EEP handling
     */
    @SuppressWarnings("unchecked")
    private Set<Class<? extends EEP>> getStaticEEPs() {
        HashSet<Class<? extends EEP>> eeps = new HashSet<>();
        try {
            for (Class clazz : EnOceanUtils.getClasses(EEPRegistry.class.getPackage().getName())) {
                if (EEP.class.isAssignableFrom(clazz) && clazz.getSimpleName().matches("[A-Z][0-9A-Z]{5}")) {
                    logger.debug("Found equipment profile: {}", clazz.getSimpleName());
                    eeps.add((Class<? extends EEP>) clazz);
                }
            }
        }
        catch (Exception e) {
            logger.error("Cannot find equipment profiles", e);
        }
        return eeps;
    }

    /**
     * Returna a reference to all the supported profiles
     *
     * @return Map of supported EEP profiles
     */
    public Map<EEPIdentifier, Class<? extends EEP>> getProfiles() {
        return supportedProfiles;
    }

}
