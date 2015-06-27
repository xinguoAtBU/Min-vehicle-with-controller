/*
 * SpotServer.java
 *
 * Created on 29 Sep, 2013 7:06:22 PM;
 */

package org.sunspotworld;

import com.sun.spot.resources.Resources;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.File;
import javax.microedition.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rui Li
 */
public class SpotServer {
    private static final int BROADCAST_CHANNEL = 15;   
    private static final String RX_PORT      = "61";
    private static final String TX_PORT      = "62";
    //private static final int PACKET_INTERVAL        = 2000;
    private static final int POWER = 16; // Start with max transmit power   
    private static final String recvPath = "F:\\apache-tomcat-7.0.57\\webapps\\myservlet\\recvFile.properties";
    private static final String traxPath = "F:\\apache-tomcat-7.0.57\\webapps\\myservlet\\cmd.properties";
    
    private static RadiogramConnection rxConnection = null;
    private static Radiogram rdg = null;
    private static RadiogramConnection txConnection = null;
    private static Radiogram xdg = null;
    
    double deviation = 0;
    int cornerNum = 0;
    
    private void setupConnection() {
        try {
            IRadioPolicyManager rpm = RadioFactory.getRadioPolicyManager();
            long ourAddr = rpm.getIEEEAddress();
            rpm.setOutputPower(POWER - 32);
            rpm.setChannelNumber(BROADCAST_CHANNEL);
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(ourAddr));
            
            rxConnection = (RadiogramConnection) Connector.open("radiogram://:" + RX_PORT);
            rdg = (Radiogram) rxConnection.newDatagram(rxConnection.getMaximumLength());
            
            txConnection = (RadiogramConnection) Connector.open("radiogram://broadcast:" + TX_PORT);
            xdg = (Radiogram) txConnection.newDatagram(txConnection.getMaximumLength()); 
        } catch (IOException ex) {
            System.err.println("Could not open radiogram broadcast connection!");
            System.err.println(ex);
        }
    }
    
    public void runProgram() throws Exception {
        new Thread() {
            public void run() {
                try {
                    recvLoop();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(SpotServer.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("file does not exist");
                } catch (IOException ex) {
                    Logger.getLogger(SpotServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
        int auto = 1;
        int speed = 0;
        int servo = 0;
        Properties prop = new Properties();
	InputStream input = null;
        while(true){           
            try {
                input = new FileInputStream(traxPath);
                // load the LED.properties file
                prop.load(input);
                // get the property value and print it out
                if(prop.getProperty("mode").equals("manual")){
                    auto = 0;
                }else{
                    auto = 1;
                }   
                speed = Integer.parseInt(prop.getProperty("speed")); 
                double serbo_double = Double.parseDouble(prop.getProperty("steering"));
                servo = (int) (serbo_double/22.5); 
                System.out.println("mode: "+auto+", speed: "+speed+", servo: "+servo);
            } catch (IOException ex) {
            } finally {
		if (input != null) {
                    try {
			input.close();
                    } catch (IOException e) {
                    }
		}
            }
            xdg.reset();
            xdg.writeUTF(Integer.toString(auto)+Integer.toString(speed+4)+Integer.toString(servo+4));            
            txConnection.send(xdg);
            Utils.sleep(100);          
        }
    }
    
    private void recvLoop() throws IOException{       
        while (true) {    
            rdg.reset();
            rxConnection.receive(rdg);
            deviation = rdg.readDouble();
            cornerNum = rdg.readInt();
            Properties proRec = new Properties();
            InputStream input = new FileInputStream(recvPath);            
            proRec.load(input);
            proRec.setProperty("deviation", Integer.toString((int)deviation));
            proRec.setProperty("cornerNum", Integer.toString(cornerNum));
            input.close(); 
            OutputStream recvFile = new FileOutputStream(recvPath); 
            proRec.store(recvFile, "");
            recvFile.flush();
            recvFile.close();
            System.out.println("deviation: "+ deviation);
        }   
    }
    
    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception{
        SpotServer app = new SpotServer();
        app.setupConnection();
        app.runProgram();
    }
}
