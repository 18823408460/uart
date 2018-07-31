package com.uurobot.serialportcompiler;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android_serialport_api.SerialPort;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FileInputStream mFileInputStream;
    private DataInputStream mDataInputStream;
    private boolean mStopThread =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        FileDescriptor temp = NativeUtils.open("/dev/ttyS1",115200,0);
        File  device = new File("/dev/ttyS3");
        if (!device.canRead() || !device.canWrite()) {
            try {
                /* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    Log.e(TAG, "not root,please check ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "not root,please check ");
            }
        }
        final NativeUtils  nativeutils = new NativeUtils(device.getAbsolutePath(), 115200, 0);
        FileDescriptor mFd = nativeutils.mFd;
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
        }
        mFileInputStream = new FileInputStream(mFd);
        final FileOutputStream mFileOutputStream = new FileOutputStream(mFd);
        Log.d(TAG, "open SerialPort success!");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mFileInputStream != null) {
                    Log.e(TAG, "run: -------------before");
                    mDataInputStream = new DataInputStream(mFileInputStream);
                    while (!mStopThread) {
                        try {
                            Log.e(TAG, "run: -------------read");
                            byte DH_Head = mDataInputStream.readByte();
                            Log.e(TAG, "run: DH_Head="+DH_Head);

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e(TAG, "run: -------------IOException");
                        }
                        //	Logger.d(TAG,"DH_Head----------="+DH_Head);
                    }
                } else {
                    Log.e(TAG, "mFileInputStream is null");
                    return;
                }
            }
        });
    }
}
