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
package com._4ng.enocean.enj.eep.eep26.profiles.F6.F602;

import com._4ng.enocean.enj.eep.eep26.attributes.EEP26RockerSwitch2RockerAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 */
public class F6020102RockerSwitchMessage {

    private static final Logger logger = LoggerFactory.getLogger(F6020102RockerSwitchMessage.class);

    private boolean[] buttonActions1;
    private boolean[] buttonActions2;
    private boolean energyBowPressed;
    private boolean action2Enabled;
    private boolean actionMessage;
    private int nButtonsPressed;
    private boolean valid;

    /**
     *
     */
    public F6020102RockerSwitchMessage(byte[] data, byte status) {
        valid = false;
        if (status == (byte) 0x30) {
            // decode single button states
            byte rockerAction1 = (byte) ((byte) (data[0] & (byte) 0xE0) >> 5);
            byte rockerAction2 = (byte) (data[0] & (byte) 0x0E);

            // decode energy bow status
            byte energyBowStatus = (byte) ((byte) (data[0] & (byte) 0x10) >> 4);

            // decode second action valid flag
            byte secondActionValid = (byte) (data[0] & (byte) 0x01);

            // store current actions
            buttonActions1 = decodeAction(rockerAction1);
            buttonActions2 = decodeAction(rockerAction2);

            // store the energy bow status
            energyBowPressed = energyBowStatus > 0;

            // store the action2 enabling flag
            action2Enabled = secondActionValid > 0;

            // this is an action message
            actionMessage = true;

            // this is valid
            valid = true;
        }
        else if (status == (byte) 0x20) {
            // decode the number of pressed buttons status
            byte nButtonsEnum = (byte) ((byte) (data[0] & (byte) 0xE0) >> 5);

            // decode energy bow status
            byte energyBowStatus = (byte) ((byte) (data[0] & (byte) 0x10) >> 4);

            // store the number of buttons pressed at the same time,if valid (0
            // or 3)
            nButtonsPressed = nButtonsEnum == 0 || nButtonsEnum == 3 ? nButtonsEnum : -1;

            // store the energy bow status
            energyBowPressed = energyBowStatus > 0;

            // this is not an action message
            actionMessage = false;

            // this is a valid message
            valid = true;
        }
    }

    /**
     * @return the buttonsAction1
     */
    public boolean[] getButtonActions1() {
        return buttonActions1;
    }

    /**
     * @return the buttonsAction2
     */
    public boolean[] getButtonActions2() {
        return buttonActions2;
    }

    /**
     * @return the energyBowPressed
     */
    public boolean isEnergyBowPressed() {
        return energyBowPressed;
    }

    /**
     * @return the action2Enabled
     */
    public boolean isAction2Enabled() {
        return action2Enabled;
    }

    /**
     * @return the actionMessage
     */
    public boolean isActionMessage() {
        return actionMessage;
    }

    /**
     * @return the nButtonsPressed
     */
    public int getnButtonsPressed() {
        return nButtonsPressed;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return valid;
    }

    private boolean[] decodeAction(byte action) {
        boolean[] actions = new boolean[]{false, false, false, false};
        switch (action) {
            case 0:
                actions[EEP26RockerSwitch2RockerAction.AO] = true;
                break;
            case 1:
                actions[EEP26RockerSwitch2RockerAction.AI] = true;
                break;
            case 2:
                actions[EEP26RockerSwitch2RockerAction.BO] = true;
                break;
            case 3:
                actions[EEP26RockerSwitch2RockerAction.BI] = true;
                break;
            default:
                logger.error("Unknown default action {}", action);
        }

        return actions;
    }

}
