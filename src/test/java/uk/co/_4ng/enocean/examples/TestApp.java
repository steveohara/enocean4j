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
package uk.co._4ng.enocean.examples;

import uk.co._4ng.enocean.communication.Connection;
import uk.co._4ng.enocean.devices.DeviceManager;
import uk.co._4ng.enocean.devices.EnOceanDevice;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D20109;
import uk.co._4ng.enocean.eep.eep26.profiles.D2.D201.D201UnitOfMeasure;
import uk.co._4ng.enocean.examples.util.Options;
import uk.co._4ng.enocean.link.LinkLayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author bonino
 */
public class TestApp {
    // supported test modes
    private static final String DEMO_MODE = "demo";
    private static final String INTERACTIVE_MODE = "interactive";
    private static final String DEMO = "demo";

    /**
     * @param args Commandline arguments
     * @throws InterruptedException If the process is interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        // a utility object for managing command line arguments...
        Options opt = new Options(new String[]{"-p port", "The serial port to which the EnOcean dongle / adapter is connected", "-m mode", "the testmode, either [interactive,demo], default is demo"}, "java TestApp", args);

        // create an instance of TestApp
        TestApp app = new TestApp();

        // check mandatory args
        if (opt.getValue('p') != null && !opt.getValue('p').isEmpty()) {
            // The EnOcean link layer
            try {
                // create the lowest link layer
                LinkLayer linkLayer = new LinkLayer(opt.getValue('p'));
                DeviceManager deviceManager = new DeviceManager();

                // create a device listener for handling device updates and value changes
                SimpleDeviceListener listener = new SimpleDeviceListener(deviceManager);
                deviceManager.addDeviceListener(listener);
                deviceManager.addDeviceValueListener(listener);

                // register a couple of known devices
                deviceManager.registerDevice("2BD5EE", "F60201");
                deviceManager.registerDevice("1A4829C", "A50205");
                deviceManager.registerDevice("0185b451", "A50904");
                deviceManager.registerDevice("0503d615", "D23202");
                deviceManager.registerDevice("002bd5f3", "F60201");
                deviceManager.registerDevice("019bd76c", "D50001");

                // available comms ports
                for (String port : LinkLayer.getCommsPorts()) {
                    System.out.printf("Available comms port: %s\n", port);
                }

                // create the connection layer
                Connection connection = new Connection(linkLayer, deviceManager);
                connection.getTeachIn().addTeachInListener(listener);

                // connect the link
                linkLayer.connect();

                // check the app-mode
                String mode = DEMO_MODE;

                // get the app-mode from options
                String optMode = opt.getValue('m');

                // change operating mode only if specified
                if (optMode != null && !optMode.isEmpty()) {
                    if (optMode.equalsIgnoreCase(INTERACTIVE_MODE)) {
                        mode = INTERACTIVE_MODE;
                    }
                }

                // handle plain old demo mode
                if (mode.equals(DEMO_MODE)) {
                    app.performDemo(connection);
                }
                else if (mode.equals(INTERACTIVE_MODE)) {
                    // handle the interactive mode
                    app.interactiveDemo(connection);

                    // disconnect from the adapter
                    linkLayer.disconnect();

                    // stop the application
                    System.exit(0);
                }

            }
            catch (Exception e) {
                System.err.println("The given port does not exist or no device is plugged in" + e);
            }
        }
        else {
            opt.usage(System.out, "TestApp");
        }
    }

    private void performDemo(Connection connection) throws InterruptedException {
        // ---------- Explicit teach-in ---------
        // the device to learn
        // System.out.println("Enabling explicit teach-in for 018a781f");
        // connection.enableTeachIn("018a781f", "A5-02-05", 10000);

        // Thread.sleep(11000);

        // 0187ae92
        // System.out.println("Enabling explicit teach-in for 0187ae92");
        // connection.enableTeachIn("0187ae92", "A5-07-01", 10000);

        // Thread.sleep(11000);

        // ---------- Smart teach-in -------------

        connection.getTeachIn().enableTeachIn(5000);
        Thread.sleep(5500);

        // ----------- actuation test ------------

        // get the device by high-level uid
        EnOceanDevice device = connection.getDeviceManager().getDevice(25673502);

        // check not null
        if (device != null) {

            // get the device eep
            D20109 eep = (D20109) device.getEEP();

            eep.actuatorSetMeasurement(connection, device.getAddress(), true, true, true, 0, 0, D201UnitOfMeasure.kW, 10, 1);

            for (int i = 0; i < 10; i++) {
                System.out.println("Sending command");

                // toggle the status
                eep.actuatorSetOuput(connection, device.getAddress(), i % 2 == 0);

                Thread.sleep(3000);
            }
        }
    }

    private void interactiveDemo(Connection connection) throws IOException, InterruptedException {
        // handle interactive mode

        // prompt:
        String prompt = "cmd > ";

        // the string holding the command line
        String cmdLine = "";

        // prepare the input reader
        BufferedReader bfr = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));

        // the commaand cycle
        while (cmdLine != null && !cmdLine.equalsIgnoreCase("exit")) {

            // print the first prompt
            System.out.print(prompt);

            // read the line
            cmdLine = bfr.readLine();
            if (cmdLine != null) {

                // switch commands
                switch (cmdLine) {
                    case DEMO:
                        // perform the demo
                        performDemo(connection);
                        break;

                    default:
                        // print the command help
                        System.out.println(TestApp.class.getSimpleName() + " command help:");

                        // help command
                        System.out.println("help - provides information about available commands");

                        // demo
                        System.out.println("demo - execute the hardcoded demo mode, for internal usage only");
                        break;
                }
            }
        }
    }

}
