/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld;

import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class PID {
    //Kp = 20;Ki = 2;Kd = 10; INT_MAX = 10;??? 
    //Kp = 30;Ki = 2;Kd = 10; INT_MAX = 10; ?????
    //INT_MAX = 15, CenterValue Check. 1550-1600?
    private static final double Kp = 30;
    private static final double Ki = 2;
    private static final double Kd = 10;
    private static final double INT_MAX = 10;
    private static final double INT_MIN = -10;       
    double servo, dis_last, dis_integral;
    
    public PID(){
        servo = 0;        
        dis_last = 0;
        dis_integral = 0;
    }
    
    public double setServo(double dis_feedback) throws IOException{
        double setpoint = 60;
        double dis_error = setpoint - dis_feedback;
        
        dis_integral = dis_integral + dis_error;
        if(dis_integral>INT_MAX) dis_integral = INT_MAX;
        else if(dis_integral<INT_MIN) dis_integral = INT_MIN;
        
        servo = Kp * dis_error + Kd * (dis_error-dis_last) + Ki * dis_integral;
        dis_last = dis_error;
        
        return servo;
        
    }
    
    
}
