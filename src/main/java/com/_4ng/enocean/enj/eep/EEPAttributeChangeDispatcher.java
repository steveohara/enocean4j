/*
 * EnJ - EnOcean Java API
 * 
 * Copyright 2014 Andrea Biasi, Dario Bonino 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com._4ng.enocean.enj.eep;

import com._4ng.enocean.enj.eep.eep26.telegram.EEP26Telegram;
import com._4ng.enocean.enj.model.EnOceanDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bonino
 */
public class EEPAttributeChangeDispatcher implements Runnable {

    private List<EEPAttribute<?>> attributesToDispatch;
    private int channelId;
    private EEP26Telegram telegram;
    private EnOceanDevice device;

    /**
     * Creates a list of attributes that need to be notified for the specific device and channel
     * @param attributesToDispatch List of attributes that have changed
     * @param channelId The channel
     * @param telegram The originating telegram
     * @param device The target device
     */
    public EEPAttributeChangeDispatcher(List<EEPAttribute<?>> attributesToDispatch, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
        this.attributesToDispatch = new ArrayList<>(attributesToDispatch);
        this.channelId = channelId;
        this.telegram = telegram;
        this.device = device;
    }

    /**
     * Creates a list of attributes that need to be notified for the specific device and channel
     * @param attributeToDispatch Attributes that has changed
     * @param channelId The channel
     * @param telegram The originating telegram
     * @param device The target device
     */
    public EEPAttributeChangeDispatcher(EEPAttribute<?> attributeToDispatch, int channelId, EEP26Telegram telegram, EnOceanDevice device) {
        attributesToDispatch = new ArrayList<>();
        attributesToDispatch.add(attributeToDispatch);
        this.channelId = channelId;
        this.telegram = telegram;
        this.device = device;
    }

    @Override
    public void run() {
        for (EEPAttribute<?> attribute : attributesToDispatch) {
            attribute.notifyAttributeListeners(channelId, telegram, device);
        }


    }
}
