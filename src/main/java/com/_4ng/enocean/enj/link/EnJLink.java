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
package com._4ng.enocean.enj.link;

import com._4ng.enocean.enj.link.serial.SerialPortFactory;
import com._4ng.enocean.protocol.serial.v3.network.packet.ESP3Packet;
import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

/**
 * The link-level EnOcean connection, handles packet transmission and reception.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * @author <a href="mailto:biasiandrea04@gmail.com">Andrea Biasi </a>
 */
public class EnJLink {

    private static final Logger logger = LoggerFactory.getLogger(EnJLink.class);

    // The default serial port timeout
    private static final int SERIAL_TIMEOUT = 1000;

    // The serial port to which the link layer is attached
    private SerialPort serialPort;

    // The high priority RX and TX queues used by the link layer
    private ConcurrentLinkedQueue<PacketQueueItem> highPriorityTxQueue;
    private ConcurrentLinkedQueue<PacketQueueItem> highPriorityRxQueue;

    // The low priority RX and TX queues used by the link layer
    private ConcurrentLinkedQueue<PacketQueueItem> lowPriorityTxQueue;
    private ConcurrentLinkedQueue<PacketQueueItem> lowPriorityRxQueue;

    // the transmitter
    private PacketTransmitter transmitter;

    // the receiver
    private PacketReceiver receiver;

    // the packet delivery process
    private PacketDelivery pktDeliveryProcess;

    /**
     * Builds a new instance of the EnJ link layer, which handles low-level
     * communication with any physical transceiver connected to the serial port
     * identified by given serial port id.
     */
    public EnJLink(String serialPortId) throws Exception {

        // build transmission and reception queues
        // TODO: check if it is better to adopt circular buffers in order to
        // avoid possible memory leaks.
        highPriorityRxQueue = new ConcurrentLinkedQueue<>();
        highPriorityTxQueue = new ConcurrentLinkedQueue<>();
        lowPriorityRxQueue = new ConcurrentLinkedQueue<>();
        lowPriorityTxQueue = new ConcurrentLinkedQueue<>();

        // build the response semaphore
        Semaphore expectedResponse = new Semaphore(1);

        // get the serial port
        serialPort = SerialPortFactory.getPort(serialPortId, SERIAL_TIMEOUT);

        // check not null
        if (serialPort != null) {
            // build the packet transmitter
            transmitter = new PacketTransmitter(highPriorityTxQueue, lowPriorityTxQueue, serialPort, expectedResponse);

            // build (and start) the packet receiver
            receiver = new PacketReceiver(highPriorityRxQueue, lowPriorityRxQueue, serialPort, expectedResponse);

            // build the packet delivery process
            pktDeliveryProcess = new PacketDelivery(lowPriorityRxQueue);
        }
    }

    /**
     * Establishes the connection to the EnOcean physical transceiver. Starts
     * the processes responsible for receiving and sending messages to the
     * transceiver, and then to the EnOcean network.
     */
    public void connect() {
        if (transmitter != null && receiver != null && serialPort != null) {
            // Open the serial port if it exists
            serialPort.openPort();

            // --------------- TX ------------------

            // create the TX thread
            Thread transmitterThread = new Thread(transmitter);

            // enable the TX thread
            transmitter.setRunnable(true);

            // start the Thread
            transmitterThread.start();

            // add the receiver as a listener
            serialPort.addDataListener(receiver);

            // create the packet delivery process thread
            Thread packetDeliveryThread = new Thread(pktDeliveryProcess);

            // enable the packet delivery process
            pktDeliveryProcess.setRunnable(true);

            // start packet delivery
            packetDeliveryThread.start();
        }
    }

    /**
     * Disconnects from the physical transceiver. Stops the transmission
     * processes and removes the asynchronous handling of RX data.
     */
    public void disconnect() {
        if (transmitter != null && receiver != null) {
            // remove the event listener
            serialPort.removeDataListener();

            // stop the transmission thread
            transmitter.setRunnable(false);
        }

        // Close the serial port if it exists and is open
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    /**
     * Adds a packet listener to the set of listeners notified of new packets in
     * the RX queue (low priority)
     *
     * @param listener The listener to add.
     */
    public void addPacketListener(PacketListener listener) {
        if (pktDeliveryProcess != null) {
            pktDeliveryProcess.addPacketListener(listener);
        }
    }

    /**
     * Removes a packet listener from the set of listeners notified of new
     * packets in the RX queue (low priority)
     *
     * @param listener The listener to remove
     */
    public void removePacketListener(PacketListener listener) {
        if (pktDeliveryProcess != null) {
            pktDeliveryProcess.removePacketListener(listener);
        }
    }

    /**
     * Sends a single {@link ESP3Packet} to the transceiver
     *
     * @param pkt The packet to send.
     */
    public void send(ESP3Packet pkt) {
        send(pkt, false);
    }

    /**
     * Sends a single {@link ESP3Packet} to the transceiver using normal or high
     * priority
     *
     * @param pkt            the packet to send
     * @param isHighPriority true if the packet should be sent with high priority (typical
     *                       for responses), false otherwise.
     */
    public void send(ESP3Packet pkt, boolean isHighPriority) {
        if (!isHighPriority) {
            lowPriorityTxQueue.add(new PacketQueueItem(pkt));
        }
        else {
            highPriorityTxQueue.add(new PacketQueueItem(pkt));
        }
    }

    /**
     * Return the first unread packet from the reception queue. Might not be
     * exactly the last if the packet delivery process is running and listeners
     * are registered.
     *
     * @return The first unread {@link ESP3Packet}
     */
    public ESP3Packet receive() {
        ESP3Packet pkt = null;

        // get the current RX queue size
        int size = lowPriorityRxQueue.size();
        logger.debug("Packets in queue: {}", size);

        // check that the queue is not empty
        if (!lowPriorityRxQueue.isEmpty()) {
            pkt = lowPriorityRxQueue.poll().getPkt();
        }

        // return the read packet or null;
        return pkt;
    }
}