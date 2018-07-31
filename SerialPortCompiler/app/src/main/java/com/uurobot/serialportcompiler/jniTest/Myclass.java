package com.uurobot.serialportcompiler.jniTest;

import android.util.Log;

/**
 * Created by Administrator on 2018/1/19.
 */

public class Myclass {
        private static String name = "staticName";
        private String pName = "MyclassPName" ;

        private int count = 1 ;

        public void printName(){
                Log.e("serialport","name="+pName) ;
        }

        public void setName(String name){
                this.pName = name ;
        }
}
