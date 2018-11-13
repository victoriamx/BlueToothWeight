package com.example.bob.weightdemo

import android.app.Application
import android.util.Log
import com.lifesense.ble.LsBleInterface
import com.lifesense.ble.LsBleManager

/**
 * Created by Bob on 2018/11/13.
 */
public class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //init LSBluetoothManager
        LsBleManager.getInstance().initialize(getApplicationContext())
        //register bluetooth broadacst receiver
        LsBleManager.getInstance().registerBluetoothBroadcastReceiver(getApplicationContext())

        Log.e("LS-BLE", "LSDevice Bluetooth SDK Version:" + LsBleInterface.BLUETOOTH_SDK_VERSION)
        //for debug mode
        LsBleManager.getInstance().enableWriteDebugMessageToFiles(true, LsBleInterface.PERMISSION_WRITE_LOG_FILE)

        //register message service if need
        LsBleManager.getInstance().registerMessageService()

    }
}