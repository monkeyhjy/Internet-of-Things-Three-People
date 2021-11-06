package com.example.internetofthings

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.internetofthings.controlGroup.zigbeecontrol.Command

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import realarm.hardware.HardwareControl
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val device1 = File(Command.zigbeePort)
        val device2 = File(Command.hfRfidPort)
        val fd = HardwareControl.OpenSerialPort(
            device1.absolutePath, Command.bautrate, 0
        )
        val fd1 = HardwareControl.OpenSerialPort(
            device2.absolutePath, Command.hfBautrate, 0
        )
        print(fd.equals(fd1))
        assert(fd == fd1)
    }
}