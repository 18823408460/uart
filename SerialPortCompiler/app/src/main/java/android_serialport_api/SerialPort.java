package android_serialport_api;

import java.io.FileDescriptor;

/**
 * Created by Administrator on 2017/5/20.
 */

public class SerialPort {
    static {
        System.loadLibrary("serial_port");
    }

    /*
     */
    public native static FileDescriptor open(String path, int baudrate,
                                             int flags);

}
