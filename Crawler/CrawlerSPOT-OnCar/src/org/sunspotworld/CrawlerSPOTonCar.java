/*
 * CrawlerSPOTonCar.java
 * Author: Yuting Zhang
 * Created on Jan 21, 2014 3:27:56 PM;
 */
package org.sunspotworld;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.Servo;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.Resources;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.Utils;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.sunspotworld.common.Globals; 
import org.sunspotworld.common.TwoSidedArray; 
import org.sunspotworld.lib.BlinkenLights;
import java.io.IOException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 *
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 */
//public class CrawlerSPOTonCar extends MIDlet implements ISwitchListener {
public class CrawlerSPOTonCar extends MIDlet{

    //steering servo for left/right direction
    private static final int SERVO_CENTER_VALUE = 1500;
    private static final int SERVO_MAX_VALUE = 2300;
    private static final int SERVO_MIN_VALUE = 1000;
    private static final int SERVO_STEP_HIGH = 200;
    
    //ESC for forward/backward speed control
    private static final int ESC_CENTER_VALUE = 1500;
    private static final int ESC_MAX_VALUE = 2000;
    private static final int ESC_MIN_VALUE = 1000;
    private static final int ESC_DEFAULT_VALUE = 1350;
    
    //servo value & time for turning
    private static final int SERVO_TURNING_1 = 1000;
    private static final int SERVO_TURNING_2 = 2200;
    private static final int SERVO_TURNING_3 = 2200;
    private static final int SERVO_TURNING_4 = 2200;
    private static final int TIME_TURNING_1 = 5000;
    private static final int TIME_TURNING_2 = 4800;
    private static final int TIME_TURNING_3 = 5000;
    private static final int TIME_TURNING_4 = 4800;
    
    private long myAddr;
    private final EDemoBoard eDemo = EDemoBoard.getInstance();
    private final ITriColorLED[] leds = eDemo.getLEDs();
    private static final ITriColorLEDArray myLEDs = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);    
    // steering servo for left & right direction 
    private final Servo servo = new Servo(eDemo.getOutputPins()[EDemoBoard.H1]);    
    // esc for forward & backward direction
    private final Servo esc = new Servo(eDemo.getOutputPins()[EDemoBoard.H0]); 
    
    private final SensorIR IR = new SensorIR();
    private final PID PID_servo = new PID();    
    private final BlinkenLights progBlinker = new BlinkenLights(0, 3);
    private final BlinkenLights velocityBlinker = new BlinkenLights(4, 7);
    private final Control control = new Control();
    
    TripwireThread RSSI = new TripwireThread();
    Thread th=new Thread(RSSI);
    
    //TwoSidedArray robot = new TwoSidedArray(getAppProperty("buddyAddress"), Globals.READ_TIMEOUT);        
    boolean error = false;
    int turnFirstTurn=0;
    int turnSecondTurn=0;
    int turnThirdTurn=0;
    int turnFourthTurn=0;
    //private final Turn turn = new Turn();

    /** BASIC STARTUP CODE
     * @throws javax.microedition.midlet.MIDletStateChangeException **/
    protected void startApp() throws MIDletStateChangeException {
        BootloaderListenerService.getInstance().start();
        initialize();
        progBlinker.startPsilon();
        velocityBlinker.startPsilon();  
        
        th.start();  
        
        new Thread() {
            public void run () {
                control.recv();
            }            
        }.start(); 
        
        velocityBlinker.setColor(LEDColor.BLUE);
        progBlinker.setColor(LEDColor.BLUE);
        
        long starttime=System.currentTimeMillis();
        long endtime=0;
        while(endtime-starttime<1000){
            esc.setValue(ESC_CENTER_VALUE);
            endtime=System.currentTimeMillis();
        }
        
        while (true) {      
            if(1 == control.getauto()){
                try {
                    AutoRun();
                    control.xmit(IR.wall_dis, RSSI.BeaconNum);
                } catch (IOException ex) {
                }
            }else{
                servo.setValue(control.getservo());
                esc.setValue(control.getspeed());
                try {
                    control.xmit(IR.WallDistance(), RSSI.BeaconNum);
                } catch (IOException ex) {
                }
            }             
        }   
    }

    private void AutoRun() throws IOException{
        switch(RSSI.BeaconNum){
            case 1:
                Turn(SERVO_TURNING_1,TIME_TURNING_1);
                break;
            case 2:
                Turn(SERVO_TURNING_2,TIME_TURNING_2);
                break;
            case 3:
                Turn(SERVO_TURNING_3,TIME_TURNING_3);
                break;
            case 4:
                Turn(SERVO_TURNING_4,TIME_TURNING_4);
                break;
            default:
                ServoControl(IR.WallDistance());
                esc.setValue(ESC_DEFAULT_VALUE);
                System.out.println("esc: " +esc.getValue() );
        }
    }
    
    private void initialize(){
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(myAddr));
        for (int i = 0; i < myLEDs.size(); i++) {
            myLEDs.getLED(i).setColor(LEDColor.GREEN);
            myLEDs.getLED(i).setOn();
        }
        Utils.sleep(500);
        for (int i = 0; i < myLEDs.size(); i++) {
            myLEDs.getLED(i).setOff();
        }
        servo.setValue(SERVO_CENTER_VALUE);
        esc.setValue(ESC_CENTER_VALUE);       
    }
    
    public void EscControl(int yval) {
        int speed;
        if (yval < 15 && yval > -15) {
            speed = ESC_CENTER_VALUE;
        } else {
            //speed = (int) Math.floor((-1)*yval / 9) * 50 + 1500;
            speed = 1350;//1250,1540
        }
        if (speed < ESC_MIN_VALUE) {
            speed = ESC_MIN_VALUE;
        } else if (speed > ESC_MAX_VALUE) {
            speed = ESC_MAX_VALUE;
        }
        esc.setValue(speed);
    }
        
    public void ServoControl(double wall_dis) throws IOException {                       
        int steer = 0;
        if( wall_dis == -1){
            steer=SERVO_CENTER_VALUE-100;
        }
        else{
            steer = SERVO_CENTER_VALUE - (int)PID_servo.setServo(wall_dis);
        }                      
        if (steer < SERVO_MIN_VALUE) steer = SERVO_MIN_VALUE;
        else if (steer > SERVO_MAX_VALUE) steer = SERVO_MAX_VALUE;

        servo.setValue(steer);
    }    
     
    private void Turn(int servo_val, int time){
        long starttime = System.currentTimeMillis();
        long endtime = 0;
        servo.setValue(servo_val);
                  
        while((endtime-starttime)<=time){
            esc.setValue(ESC_DEFAULT_VALUE);
            //EscControl(get_ytilt()); 
            Utils.sleep(100);
            endtime=System.currentTimeMillis();
            //EscControl(get_ytilt());
        }
        esc.setValue(ESC_CENTER_VALUE);
        //Utils.sleep(100);
    }

     
    protected void pauseApp() {
        // This will never be called by the Squawk VM
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * I.e. if startApp throws any exception other than MIDletStateChangeException,
     * if the isolate running the MIDlet is killed with Isolate.exit(), or
     * if VM.stopVM() is called.
     * 
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true when this method is called, the MIDlet must
     *    cleanup and release all resources. If false the MIDlet may throw
     *    MIDletStateChangeException  to indicate it does not want to be destroyed
     *    at this time.
     * @throws javax.microedition.midlet.MIDletStateChangeException
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
        for (int i = 0; i < myLEDs.size(); i++) {
            myLEDs.getLED(i).setOff();
        }
    }
}
