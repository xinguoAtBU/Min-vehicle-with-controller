/*
 * TripwireRSSIReceiver.java
 *
 * Example RSSI Receiver code intended to be run on a free-range SPOT.
 * This code listens for beacon transmissions on the specified port, then forwards
 * the RSSI and Address information to a basestation (or other SPOTs) on a
 * different port.
 *
 * @author: Aaron Heuckroth <a.heuckroth@gmail.com>
 * Created on Oct 28, 2014
 * Modified on Oct 30, 2014 by Yuting Zhang
 */
package org.sunspotworld;
 
import javax.microedition.io.Connector;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.util.Utils;
import java.io.IOException;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
 
public class TripwireThread implements Runnable {
     
    //LEDs for status indicator blinking
    private static final ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private static final int LED_ON = 10;
    //MUST be the same as the BROADCAST_PORT from RSSIBeacon.java
    //All beacons transmit on the same port, so you shouldn't change this!
    private static final String RECEIVE_PORT = "55";
     
    private static final int BROADCAST_CHANNEL = 15;
     
    //MUST be the same as the CONFIRM_BYTE from RSSIBeacon.java
    private static final byte RSSI_CONFIRM_BYTE = 23;
     
    //MUST be the same as the CONFIRM_BYTE from RSSIReceiver.java
    //Change this to keep people from using your port and screwing up your data!
    private static final byte TRIP_CONFIRM_BYTE = 100;
     
    //static radio connection objects
    private static RadiogramConnection rxConnection = null;
    private static Radiogram rxg = null;
    
    int BeaconNum = 0;
    
     
    /* Helper method for blinking LEDs the specified color. */
    private static void blinkLED(int led0, int led1, LEDColor color) {
        leds.getLED(led0).setColor(color);
        leds.getLED(led1).setColor(color);
        leds.getLED(led0).setOn();
        leds.getLED(led1).setOn();
        Utils.sleep(LED_ON);
        leds.setOff();
    }
    private static void blinkLEDall(LEDColor color) {
        leds.setColor(color);
        leds.setOn();
        Utils.sleep(LED_ON);
        leds.setOff();
    }
     
    /* Establish RadiogramConnections on the specified ports. */
    private static void setupConnection() {
        IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setOutputPower(-16);
        rpm.setChannelNumber(BROADCAST_CHANNEL);
        try {
            long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
             
            // Connection used for receiving beacon transmissions
            rxConnection = (RadiogramConnection) Connector.open("radiogram://:" + RECEIVE_PORT);
            rxg = (Radiogram) rxConnection.newDatagram(10);
             
            // blink white to confirm successful connection setup!
            blinkLEDall(LEDColor.WHITE);
             
        } catch (IOException ex) {
            //blink red upon failure. :(
            blinkLEDall( LEDColor.RED);
             
            System.err.println("Could not open radiogram broadcast connection!");
            System.err.println(ex);
        }
    }
    /* Wait for beacon transmissions, then forward them to the basestation. */
     
    private void receiveLoop() {
         
        while (true) {
            //System.out.println("Enter the receive loop\n");
            try {
            //System.out.println("*******  1  \n");
                //reset radiograms to clear transmission data
                rxg.reset();
                 
            //System.out.println("*******  2  \n");
                //variables for holding radiogram data
                int rssiValue;
                String spotAddress;
                 
            //System.out.println("*******  3  \n");
                //waits for a new transmission on RECEIVE_PORT
                rxConnection.receive(rxg);
                 
            //System.out.println("*******  4  \n");
                //read confirmation byte data from the radiogram
                byte checkByte = rxg.readByte();
                 
                //check to see if radiogram is the right type
                if (checkByte == RSSI_CONFIRM_BYTE) {
                    //grab the RSSI and address info embedded in the radiogram
                    rssiValue = rxg.getRssi();
                    spotAddress = rxg.getAddress();
                    //System.out.println("Received RSSI packet from: " + spotAddress  + ", RSSI: " + rssiValue);
                }
                else if (checkByte == TRIP_CONFIRM_BYTE) {
                    //grab the RSSI and address info embedded in the radiogram
                    rssiValue = rxg.getRssi();
                    spotAddress = rxg.getAddress();
                    //System.out.println("Received Tripwire packet from: " + spotAddress  + ", RSSI: " + rssiValue);
                    
                    //blink color code to confirm successful send!
                    if (spotAddress.endsWith("7F48")){
                        BeaconNum=1;
                        //blinkLED(0,1,LEDColor.GREEN);
                    } else if (spotAddress.endsWith("7E5D")){
                        BeaconNum=2;
                        //blinkLED(2,3,LEDColor.MAGENTA);
                    } else if (spotAddress.endsWith("80F5")){
                        BeaconNum=3;
                        //blinkLED(4,5,LEDColor.ORANGE);
                    } else if (spotAddress.endsWith("7FEE")){
                        BeaconNum=4;
                        //blinkLED(6,7,LEDColor.TURQUOISE);
                    }
                } //else {
                    //blink red upon failure. :(
 //                   blinkLEDall(LEDColor.RED);
                    //System.out.println("Unrecognized radiogram type! Expected: " + RSSI_CONFIRM_BYTE + " or " + TRIP_CONFIRM_BYTE + ", Saw: " + checkByte);
               // }
                 
            } catch (Exception e) {
                //blinkr ed upon failure. :(
                blinkLEDall(LEDColor.RED);
                //System.err.println("Caught " + e + " while collecting/sending sensor sample.");
                System.err.println(e);
            }
        //System.out.println("Loop\n");
        }
    }
    
     
    /**
     * Called if the MIDlet is terminated by the system. I.e. if startApp throws
     * any exception other than MIDletStateChangeException, if the isolate
     * running the MIDlet is killed with Isolate.exit(), or if VM.stopVM() is
     * called.
     *
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     */
    
    public void run() {
        //System.out.println("Starting RSSI Beacon Broadcast Mote...");
        BootloaderListenerService.getInstance().start();
        setupConnection();
        //System.out.println("Start\n");
        receiveLoop();       
    }
}
