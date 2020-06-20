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
package uk.co._4ng.enocean.link;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co._4ng.enocean.util.EnOceanUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * A class implementing the transmitter tier of the Java EnOcean Serial Protocol
 * API, EnJ. Writes packets on the serial port (connected to the external
 * EnOcean transceiver, e.g., the TCM 320) accounting for different transmission
 * priorities.
 *
 * @author <a href="mailto:dario.bonino@gmail.com">Dario Bonino</a>
 * @author <a href="mailto:biasiandrea04@gmail.com">Andrea Biasi </a>
 */

public class PacketTransmitter implements Runnable {

    // the transmission timeout, in milliseconds
    public static final int TIMEOUT_TX = 500;

    // the transmission sleep time
    // should be able to transmit 10 high priority messages within the timeout
    // of a single low priority packet
    // TODO: there was a warning on a minimum timeout of 200ms to avoid multiple
    // TODO: transmissions, check if such a minimum exists and, in case, if there are
    // TODO: references to such a value in the ESP3 documentation.
    public static final int MIN_TX_TIME = 50;
    private static final Logger logger = LoggerFactory.getLogger(PacketTransmitter.class);

    // the high priority transmission queue
    private final Queue<PacketQueueItem> highPriorityTxQueue;

    // the low priority transmission queue
    private final Queue<PacketQueueItem> lowPriorityTxQueue;

    // Serial port
    private final SerialPort serialPort;

    // the expected response semaphore
    private final Semaphore expectedResponse;

    // the run enabling flag
    private boolean runnable;

    /**
     * Creates a {@link PacketTransmitter} runnable connected to the given
     * serial port, and exploiting the given transmission queues. Exploits the
     * given semaphore to signal that a sent packet needs a response from the
     * EnOcean transceiver.
     *
     * @param highPriorityTxQueue The high priority transmission queue.
     * @param lowPriorityTxQueue  The low priority transmission queue.
     * @param serialPort          The serial port used by the transmitter to send data to the
     *                            physical transceiver.
     * @param expectedResponse    The expected response semphore.
     */
    public PacketTransmitter(Queue<PacketQueueItem> highPriorityTxQueue, Queue<PacketQueueItem> lowPriorityTxQueue, SerialPort serialPort, Semaphore expectedResponse) {

        // store a reference to the high priority transmission queue
        this.highPriorityTxQueue = highPriorityTxQueue;

        // store a reference to the low priority transmission queue
        this.lowPriorityTxQueue = lowPriorityTxQueue;

        // store a reference to the serial port used for communication
        this.serialPort = serialPort;

        // store a reference to the expected response semaphore
        this.expectedResponse = expectedResponse;

        // set the initial state at runnable
        runnable = true;
    }

    /**
     * The main transmission cycle. It looks for packets to transmit, in both
     * transmission queues. The priority handling is greedy and packets in the
     * high priority queue are always handled before packets in the low priority
     * queue, regardless of the arrival order.
     */
    @Override
    public void run() {
        // the serial port output stream
        OutputStream serialOut;

        // the timestamp to use for packet timeout
        // negative values mean that the time has not been
        // initialized.
        long time = -1;

        // try to open the serial port, if some error happens, the serial port
        // is unreachable and no data can be transmitted.
        // TODO: check if any fail safe solution can be devised
        try {
            // get the serial port output stream
            serialOut = serialPort.getOutputStream();

            // thread loop
            while (runnable) {
                // every cycle manages one packet transmission

                // manage the high priority queue first, high priority packets
                // are sent event if an already sent packet is still waiting for
                // its response.

                // the current message
                PacketQueueItem currentMessage;

                // check if packets are waiting in the high priority queue
                if (!highPriorityTxQueue.isEmpty()) {
                    // handle the high priority messages

                    // extract the first packet in the queue (FIFO queue)
                    currentMessage = highPriorityTxQueue.poll();

                    // send the packet
                    byte[] packetBytes = currentMessage.getPkt().getPacketAsBytes();
                    logger.debug("Sending: {}", EnOceanUtils.toHexString(packetBytes));
                    serialOut.write(packetBytes);

                }
                else {
                    // handle the low priority messages

                    // check if there is still some pending response to be
                    // received, in such a case do not transmit the next packet.
                    if (expectedResponse.availablePermits() > 0) {
                        // no response is waited, handle packet transmission
                        if (!lowPriorityTxQueue.isEmpty()) {
                            // check if the current HEAD of the queue has
                            // already been transmitted, in such a case, since
                            // the expected response flag the transmission was
                            // successful the the queue HEAD should be consumed.
                            currentMessage = lowPriorityTxQueue.peek();

                            // purge already sent packets
                            if (currentMessage.getRetransmissionCounter() < PacketQueueItem.MAX_RETRANSMISSION) {
                                // the packet has already been sent, remove it
                                // from the queue
                                lowPriorityTxQueue.poll();

                                // update the current message
                                currentMessage = lowPriorityTxQueue.peek();
                            }

                            // handle packet transmission
                            // in this first implementation it seems that all
                            // low-priority packets require a response from the
                            // physical transceiver, it remains to check if this
                            // is true at the ESP3 specification level or not.

                            // check if transmission is possible
                            if (currentMessage != null && currentMessage.getRetransmissionCounter() > 0) {
                                // ok can transmit
                                try {
                                    // acquire the expected response semaphore
                                    expectedResponse.acquire();

                                    // set the transmission time
                                    time = System.currentTimeMillis();

                                    // transmit the packet
                                    byte[] packetBytes = currentMessage.getPkt().getPacketAsBytes();
                                    logger.debug("Sending: {}", EnOceanUtils.toHexString(packetBytes));
                                    serialOut.write(packetBytes);

                                    // decrease the retransmission count
                                    currentMessage.decreaseRetransmissionCounter();
                                }
                                catch (InterruptedException e) {
                                    logger.error("Error while acquiring the expected response semaphore", e);
                                }
                            }
                        }
                    }
                    else {
                        // still waiting for a response

                        // check the time
                        if (System.currentTimeMillis() - time > TIMEOUT_TX) {
                            // timeout, we do not expect the response anymore,
                            // re-transmission shall be forced. the expected
                            // response semaphore cannot be used
                            // as it is meant for synchronizing the transmit and
                            // receive processes.

                            // get the current message
                            currentMessage = lowPriorityTxQueue.peek();

                            // check if re-transmission is enabled
                            if (currentMessage != null && currentMessage.getRetransmissionCounter() > 0) {
                                // set the transmission time
                                time = System.currentTimeMillis();

                                // transmit the packet
                                serialOut.write(currentMessage.getPkt().getPacketAsBytes());

                                // decrease the retransmission count
                                currentMessage.decreaseRetransmissionCounter();
                            }
                            else {
                                // log the error
                                logger.warn("Packet transmission failed after {} transmission attempts", PacketQueueItem.MAX_RETRANSMISSION);

                                // free the expected response semaphore
                                // this causes a subsequent packet drop from the
                                // queue as in the case in which the packet has
                                // been successfully sent.
                                expectedResponse.release();
                            }
                        }

                    }

                }

                // sleep
                Thread.sleep(MIN_TX_TIME);
            }
        }
        catch (IOException | InterruptedException e) {
            logger.error("Problem", e);
        }

    }

    /**
     * Return the current state of the transmitter, if true the transmitter is
     * enabled, and, if started, can trasmit packets.
     *
     * @return the runnable flag, true if the transmitter can run, false, otherwise.
     */
    public boolean isRunnable() {
        return runnable;
    }

    /**
     * Stops this packet transmitter.
     *
     * @param runnable the runnable flag, true if the transmitter can run, false, otherwise.
     */
    public void setRunnable(boolean runnable) {
        this.runnable = runnable;
    }

}