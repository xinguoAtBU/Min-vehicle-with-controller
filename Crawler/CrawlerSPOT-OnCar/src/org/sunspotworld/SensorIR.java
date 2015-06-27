/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sunspotworld;

import com.sun.spot.resources.transducers.IAnalogInput;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.util.Utils;
import java.io.IOException;

/**
 *
 * @author Administrator
 */
public class SensorIR {
    private static final double CAR_WIDTH = 11.3;
    private static final double CAR_LENGTH = 22.5;
    private static final double WALL_WIDTH = 180;
    private static final double HEADLONG = 34;
    int s=1;
    IAnalogInput proximity_fl, proximity_fr, proximity_bl, proximity_br;        
    double fr1,fr2,fr3,fr4,fr5,fr6,fr7,fr8,fr9,fr10;
    double br1,br2,br3,br4,br5,br6,br7,br8,br9,br10;
    double fl1,fl2,fl3,fl4,fl5,fl6,fl7,fl8,fl9,fl10;
    double bl1,bl2,bl3,bl4,bl5,bl6,bl7,bl8,bl9,bl10;
    double ave_fr,ave_br,ave_fl,ave_bl;
    double dis_fl, dis_fr, dis_bl, dis_br;
    double raw_fr,raw_br,raw_fl,raw_bl;
    double wall_dis, cosTheta;
    double a_bl = 0.017246180565328;
    double b_bl = -0.00068328271720442;
    double a_fr = 0.017323232430425;
    double b_fr = -0.00086063444140819; 
    double a_br = 0.018137601655639;
    double b_br = -0.00077063823709447;
    double a_fl = 0.017598317613722;
    double b_fl = -0.00032013800632434;
    int Threshold = 200;
    
    
    
    public SensorIR(){
        /*proximity_fl = EDemoBoard.getInstance().getAnalogInputs()[EDemoBoard.A0];
        proximity_fr = EDemoBoard.getInstance().getAnalogInputs()[EDemoBoard.A1];
        proximity_bl = EDemoBoard.getInstance().getAnalogInputs()[EDemoBoard.A2];
        proximity_br = EDemoBoard.getInstance().getAnalogInputs()[EDemoBoard.A3];*/ 
        
        proximity_br = EDemoBoard.getInstance().getAnalogInputs()[EDemoBoard.A0];
        proximity_bl = EDemoBoard.getInstance().getAnalogInputs()[EDemoBoard.A1];
        proximity_fr = EDemoBoard.getInstance().getAnalogInputs()[EDemoBoard.A2];
        proximity_fl = EDemoBoard.getInstance().getAnalogInputs()[EDemoBoard.A3];   
    }
    
    public void getSensor() throws IOException {
        
        double vol_fr = proximity_fr.getVoltage(); //if(vol_fr<0.33) vol_fr=0.33; else if(vol_fr>1.98) vol_fr=1.98;        
        double vol_br = proximity_br.getVoltage(); //if(vol_br<0.33) vol_br=0.33; else if(vol_br>1.98) vol_br=1.98;
        double vol_fl = proximity_fl.getVoltage(); //if(vol_fl<0.33) vol_fl=0.33; else if(vol_fl>1.98) vol_fl=1.98;
        double vol_bl = proximity_bl.getVoltage(); //if(vol_bl<0.33) vol_bl=0.33; else if(vol_bl>1.98) vol_bl=1.98;
        
                                          
                        if (s == 1) {fr1=vol_fr; br1=vol_br; fl1=vol_fl; bl1=vol_bl;}
                        else if (s == 2) {fr2=vol_fr; br2=vol_br; fl2=vol_fl; bl2=vol_bl;}
                        else if (s == 3) {fr3=vol_fr; br3=vol_br; fl3=vol_fl; bl3=vol_bl;}
                        else if (s == 4) {fr4=vol_fr; br4=vol_br; fl4=vol_fl; bl4=vol_bl;}
                        else if (s == 5) {fr5=vol_fr; br5=vol_br; fl5=vol_fl; bl5=vol_bl;}
                        else if (s == 6) {fr6=vol_fr; br6=vol_br; fl6=vol_fl; bl6=vol_bl;}
                        else if (s == 7) {fr7=vol_fr; br7=vol_br; fl7=vol_fl; bl7=vol_bl;}
                        else if (s == 8) {fr8=vol_fr; br8=vol_br; fl8=vol_fl; bl8=vol_bl;}
                        else if (s == 9) {fr9=vol_fr; br9=vol_br; fl9=vol_fl; bl9=vol_bl;}
                        else if (s == 10){fr10=vol_fr; br10=vol_br; fl10=vol_fl; bl10=vol_bl;s = 0;}  
                        
                        ave_fr = (fr1+fr2+fr3+fr4+fr5+fr6+fr7+fr8+fr9+fr10)/10;
                        ave_br = (br1+br2+br3+br4+br5+br6+br7+br8+br9+br10)/10;
                        ave_fl = (fl1+fl2+fl3+fl4+fl5+fl6+fl7+fl8+fl9+fl10)/10;
                        ave_bl = (bl1+bl2+bl3+bl4+bl5+bl6+bl7+bl8+bl9+bl10)/10;

        raw_fr = 1/(a_fr * ave_fr + b_fr);
        raw_bl = 1/(a_bl * ave_bl + b_bl);
        raw_fl = 1/(a_fl * ave_fl + b_fl);
        raw_br = 1/(a_br * ave_br + b_br);  
        
        if((raw_fr < Threshold) && (raw_fr > 0)) dis_fr = raw_fr;
        if((raw_bl < Threshold) && (raw_bl > 0)) dis_bl = raw_bl;
        if((raw_fl < Threshold) && (raw_fl > 0)) dis_fl = raw_fl;
        if((raw_br < Threshold) && (raw_br > 0)) dis_br = raw_br;
                                   
        s = s + 1;
        Utils.sleep(10);
        //System.out.println(s+"  "+(int)dis_fr+"  "+(int)dis_br);
        //System.out.println(s+"  "+(int)dis_fr+"  "+(int)dis_br+"  "+(int)dis_fl+"  "+(int)dis_bl);
    }
    
    public int left_right() throws IOException {
        int left_right = 0;
        if ((dis_fl+dis_bl)<(dis_fr+dis_br)) left_right = 1;
        else if((dis_fl+dis_bl)>(dis_fr+dis_br)) left_right = -1;
        return left_right;
    }
    
    private void getDistance() throws IOException {
        double tanTheta, sinTheta;
        if(dis_fl>90||dis_bl>90){
            wall_dis=-1;
        }
        else{
        if (left_right()==1){
            if((dis_fl-dis_bl)<4 && (dis_fl-dis_bl)>-4){  
            wall_dis = (dis_fl+dis_bl+CAR_WIDTH)/2;            
            }else{
            tanTheta = (dis_fl - dis_bl)/CAR_LENGTH;   
            cosTheta = 1/(Math.sqrt(tanTheta*tanTheta+1));
            sinTheta = Math.sqrt(1-cosTheta*cosTheta);
            if(dis_fl<dis_bl){
            wall_dis = (dis_fl+dis_bl+CAR_WIDTH)/2*cosTheta - HEADLONG * sinTheta;
            }else if(dis_fl>dis_bl){
                wall_dis = (dis_fl+dis_bl+CAR_WIDTH)/2*cosTheta + HEADLONG * sinTheta;
            }            
        }
        }

        else if(left_right()==-1){
            if((dis_fr-dis_br)<4 && (dis_fr-dis_br)>-4){
            wall_dis = WALL_WIDTH-(dis_fr+dis_br+CAR_WIDTH)/2;
            }else{   
            tanTheta = (dis_fr - dis_br)/CAR_LENGTH;   
            cosTheta = 1/(Math.sqrt(tanTheta*tanTheta+1));
            sinTheta = Math.sqrt(1-cosTheta*cosTheta);
            if(dis_fl<dis_bl){
            wall_dis = WALL_WIDTH-(dis_fl+dis_bl+CAR_WIDTH)/2*cosTheta - HEADLONG * sinTheta;
            }else if(dis_fl>dis_bl){
                wall_dis = WALL_WIDTH-(dis_fl+dis_bl+CAR_WIDTH)/2*cosTheta + HEADLONG * sinTheta;
                }            
            
            }            
        
        }
        
        }
    System.out.println("Wall distance is: " +(int)wall_dis+"\n");
    }

        
    public double WallDistance() throws IOException{
        getSensor();
        getDistance();
        //System.out.println(wall_dis+"\n");
        if(wall_dis < 0) wall_dis = 0;
        return wall_dis;
    }
    
}
