package com.example.bob.weightdemo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.lifesense.ble.LsBleManager
import com.lifesense.ble.PairCallback
import com.lifesense.ble.ReceiveDataCallback
import com.lifesense.ble.SearchCallback
import com.lifesense.ble.bean.*
import com.lifesense.ble.bean.constant.*
import java.util.ArrayList

/**
 * Created by Bob on 2018/11/12.
 */
class PairActivity : AppCompatActivity(){

    val TAG : String = "PairActivity"
    lateinit var deviceTextView: TextView
    lateinit var deviceImageView : ImageView
    lateinit var pairButton: Button
    lateinit var measureDataTextView : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pair)
        deviceTextView = findViewById(R.id.device_name)
        deviceImageView = findViewById(R.id.device_img)
        pairButton = findViewById(R.id.pair)
        measureDataTextView = findViewById(R.id.measure_data)
        LsBleManager.getInstance().searchLsDevice(mSearchCallback, getDeviceTypes(), BroadcastType.ALL)
    }

    private val mSearchCallback = object : SearchCallback() {

        override fun onSearchResults(lsDevice: LsDeviceInfo?) {
            updateScanResults(lsDevice)
        }

    }

    private val mPairCallback = object : PairCallback() {
        override fun onPairResults(lsDevice: LsDeviceInfo?, status: Int) {
            this@PairActivity.runOnUiThread({
                if (lsDevice != null) {
                    saveDeviceInfo(lsDevice)
                    pairButton.text = "接收数据"
                    //设置测量设备
                    LsBleManager.getInstance().setMeasureDevice(null)
                    LsBleManager.getInstance().addMeasureDevice(lsDevice)
                } else {
                    Log.d("Prompt", "Pairing failed, please try again")
                }
            })
        }

        override fun onWifiPasswordConfigResults(lsDevice: LsDeviceInfo?,
                                                 isSuccess: Boolean, errorCode: Int) {
                this@PairActivity.runOnUiThread({
                    var msg = "success to set device's wifi password ! "
                    if (!isSuccess) {
                        msg = "failed to set device's wifi password : $errorCode"
                    }
                })
        }

        override fun onDeviceOperationCommandUpdate(macAddress: String?,
                                                    cmd: OperationCommand?, obj: Any?) {
            Log.e("LS-BLE", "operation command update >> $cmd; from device=$macAddress")
            if (OperationCommand.CMD_DEVICE_ID == cmd) {
                //input device id
                val deviceId = macAddress!!.replace(":", "")//"ff028a0003e1";
                //register device's id for device
                LsBleManager.getInstance().registeringDeviceID(macAddress, deviceId, DeviceRegisterState.NORMAL_UNREGISTER)
            } else if (OperationCommand.CMD_PAIRED_CONFIRM == cmd) {
                val confirmInfo = PairedConfirmInfo(PairedConfirmState.PAIRING_SUCCESS)
                confirmInfo.userNumber = 0
                LsBleManager.getInstance().inputOperationCommand(macAddress, cmd, confirmInfo)
            }

        }

        override fun onDiscoverUserInfo(macAddress: String?, userList: List<*>?) {
            this@PairActivity.runOnUiThread(Runnable {
                if (userList == null || userList.isEmpty()) {
                    Toast.makeText(this@PairActivity, "failed to pairing devie,user list is null...", Toast.LENGTH_LONG).show()
                    return@Runnable
                }
            })
        }
    }

    private fun getDeviceTypes(): List<DeviceType> {
            //返回扫描所有的设备类型
        var mScanDeviceType = ArrayList<DeviceType>()
        mScanDeviceType.add(DeviceType.SPHYGMOMANOMETER)
        mScanDeviceType.add(DeviceType.FAT_SCALE)
        mScanDeviceType.add(DeviceType.WEIGHT_SCALE)
        mScanDeviceType.add(DeviceType.HEIGHT_RULER)
        mScanDeviceType.add(DeviceType.PEDOMETER)
        mScanDeviceType.add(DeviceType.KITCHEN_SCALE)

        return mScanDeviceType
    }

    private fun saveDeviceInfo(lsDevice : LsDeviceInfo){

    }

    private fun updateScanResults(lsDevice : LsDeviceInfo?){
        this@PairActivity.runOnUiThread {
            deviceTextView.visibility = View.VISIBLE
            deviceTextView.text = lsDevice?.deviceName
            deviceImageView.visibility = View.VISIBLE
            pairButton.visibility = View.VISIBLE;
            pairButton.setOnClickListener{
                if (pairButton.text == "匹配")
                    bindingDevice(lsDevice)
                else{
                    if(LsBleManager.getInstance().lsBleManagerStatus == ManagerStatus.FREE)
                        LsBleManager.getInstance().startDataReceiveService(mDataCallback)
                    else if(LsBleManager.getInstance().lsBleManagerStatus == ManagerStatus.DATA_RECEIVE)
                        LsBleManager.getInstance().stopDataReceiveService();
                }
            }
        }
    }

    /**
     * Device measurement data synchronization callback object
     */
    private val mDataCallback = object : ReceiveDataCallback() {
        override fun onDeviceConnectStateChange(connectState: DeviceConnectState?,
                                                broadcastId: String?) {
            //Device Connection Status

        }

        override fun onReceiveWeightData_A3(wData: WeightData_A3?) {
            LsBleManager.getInstance().setLogMessage("object data >> " + wData!!.toString())
                /**
                 * Weight Scale Measurement Data
                 * A3 product
                 */
                val intent = Intent()
            //获取intent对象
            intent.setClass(this@PairActivity,DataActivity::class.java)
            intent.putExtra("data", wData)
            startActivity(intent)
        }

        override fun onReceiveUserInfo(proUserInfo: WeightUserInfo?) {
            /**
             * Weight Scale Product User Info
             * A3 product
             */
            this@PairActivity.runOnUiThread {
                measureDataTextView.visibility = View.VISIBLE
                measureDataTextView.text = proUserInfo.toString()
            }
        }

        override fun onReceivePedometerMeasureData(dataObject: Any?,
                                                   packetType: PacketProfile?, sourceData: String?) {
            /*val devicePower = DeviceDataUtils.getDevicePowerPercent(dataObject, packetType)
            updateDevicePower(devicePower)
            //update new data message
            updateNewDatMessage()
            *//**
             * Pedoemter Measurement Data
             * Product：BonbonC、Mambo、MamboCall、MamboHR、Mambo Watch、MT/Gold、ZIVA
             *//*
            showDeviceMeasuringData(dataObject)
            LsBleManager.getInstance().setLogMessage("object data >> " + dataObject!!.toString())*/
            this@PairActivity.runOnUiThread {
                measureDataTextView.visibility = View.VISIBLE
                measureDataTextView.text = dataObject.toString()
            }
        }

        override fun onReceiveWeightDta_A2(wData: WeightData_A2?) {
            this@PairActivity.runOnUiThread {
                measureDataTextView.visibility = View.VISIBLE
                measureDataTextView.text = wData.toString()
            }
        }

        override fun onReceiveDeviceInfo(lsDevice: LsDeviceInfo?) {

        }
    }

    fun bindingDevice(lsDevice: LsDeviceInfo?) {
        val status = LsBleManager.getInstance().lsBleManagerStatus
        if (status == ManagerStatus.DATA_RECEIVE) {
            LsBleManager.getInstance().stopDataReceiveService()
        } else if (status == ManagerStatus.DEVICE_SEARCH) {
            LsBleManager.getInstance().stopSearch()
        } else if (status == ManagerStatus.DEVICE_PAIR) {
            LsBleManager.getInstance().cancelDevicePairing(lsDevice)
        } else if (status == ManagerStatus.UPGRADE_FIRMWARE_VERSION) {
            LsBleManager.getInstance().cancelAllUpgradeProcess()
        }
        //delay 5 seconds to binding if need or test
        val handler = Handler()
        handler.postDelayed({ LsBleManager.getInstance().pairingWithDevice(lsDevice, mPairCallback) }, 3 * 1000L)

    }

}