package com.example.internetofthings.controlGroup.hardwareControl;

import java.io.FileDescriptor;

/**
 * Created by Amber on 2015/3/23.
 */
public class HardwareControl {
    public native static FileDescriptor OpenSerialPort(String path, int baudrate,
                                                       int flags);

    public native static void CloseSerialPort();

    static {
        System.loadLibrary("RealarmHardwareJni");
    }

}
