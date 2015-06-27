/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.Spot;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.io.Connector;

/**
 *
 * @author Administrator
 */
public class Control {
    //private static final long BASE_ADDR = ;
    private static final int BROADCAST_CHANNEL = 15;   
    private static final String RX_PORT      = "62";
    private static final String TX_PORT      = "61";
    //private static final int PACKET_INTERVAL        = 2000;
    private static final int POWER = 16; // Start with max transmit power    
    
    private static RadiogramConnection rxConnection = null;
    private static Radiogram rdg = null;
    private static RadiogramConnection txConnection = null;
    private static Radiogram xdg = null;
    
    private int auto = 1;
    private int speed = 0;
    private int servo = 0;   

    public Control(){
        IRadioPolicyManager rpm = Spot.getInstance().getRadioPolicyManager();
        rpm.setOutputPower(POWER - 32);
        rpm.setChannelNumber(BROADCAST_CHANNEL);
        
        try {
            long leaderAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
            System.out.println("Our radio address = " + IEEEAddress.toDottedHex(leaderAddr));
            
            // Connection used for receiving beacon transmissions
            rxConnection = (RadiogramConnection) Connector.open("radiogram://:" + RX_PORT);
            rdg = (Radiogram) rxConnection.newDatagram(rxConnection.getMaximumLength());
            
            // Connection used for relaying transmission data back to base
            txConnection = (RadiogramConnection) Connector.open("radiogram://broadcast:" + TX_PORT);
            xdg = (Radiogram) txConnection.newDatagram(txConnection.getMaximumLength());            
        } catch (IOException ex) {          
            System.err.println("Could not open radiogram broadcast connection!");
            System.err.println(ex);
        }
    }
    
    public void recv() {
        while(true){
            try {                
                rdg.reset();
                rxConnection.receive(rdg);           // listen for a packet
                long getAddr = rdg.getAddressAsLong(); 
                String read = rdg.readUTF(); 
                auto = read.charAt(0) - '0';
                speed = read.charAt(1)- '0';
                servo = read.charAt(2)- '0';
                System.out.println("recvValue: " +speed+", "+servo );
            } catch (IOException ex) {
                System.err.println("Could not receive!");
                System.err.println(ex);
            }             
        }                      
    }
    
    public void xmit(double deviation, int cornerNum) {
        try {
            xdg.reset();
            xdg.writeDouble(deviation);
            xdg.writeInt(cornerNum);   
            txConnection.send(xdg);
        } catch (Exception ex) {
            System.err.println("Could not broadcast!");
            System.err.println(ex);
        } 
        Utils.sleep(100);
    }
       
    public synchronized int getauto(){
        return auto;
    } 
    
    public synchronized int getspeed(){
        if(speed == 4) return 1500;
        else if(speed > 4) return (1850 - 100*speed);
        else return (1950 - 100*speed);
    } 
    
    public synchronized int getservo(){
        if(servo >= 4) return (2000 - 125*servo);
        else return (2300 - 200*servo);
    } 
    
}
