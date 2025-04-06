package com.margelo.nitro.awesomelibrarybob

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.facebook.react.bridge.BaseActivityEventListener
import android.content.pm.PackageManager
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.app.Activity
import android.content.Intent
import android.util.Log
import okhttp3.internal.toImmutableList

@DoNotStrip
class AwesomeLibraryBob : HybridAwesomeLibraryBobSpec() {

  val appContext = NitroModules.applicationContext
  val packageManager: PackageManager = appContext?.packageManager!!
  val bluetoothManager: BluetoothManager = appContext?.getSystemService(BluetoothManager::class.java)!!
  val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
  //if (bluetoothAdapter == null) {
    // Device doesn't support Bluetooth
  //}

  override fun isBluetoothClassicFeatureAvailable(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
  }

  override fun isBluetoothOn(): Boolean {
    return bluetoothAdapter?.isEnabled ?: false
  }

  private var pickerPromise: Promise<Unit>? = null

  private val activityEventListener =
      object : BaseActivityEventListener() {
          override fun onActivityResult(
              activity: Activity?,
              requestCode: Int,
              resultCode: Int,
              intent: Intent?
          ) {
              if (requestCode == REQUEST_ENABLE_BT) {
                  pickerPromise?.let { promise: Promise<Unit> ->
                      when (resultCode) {
                          Activity.RESULT_CANCELED ->
                              throw Error("Enabling bluetooth was cancelled")
                          Activity.RESULT_OK -> {
                              promise.resolve(Unit)
                          }
                      }

                      pickerPromise = null
                  }
              } else {
                throw Error("Wrong Intent!")
              }
          }
      }

  init {
      appContext?.addActivityEventListener(activityEventListener)
  }

  override fun enableBluetooth(): Promise<Unit> {
    return Promise.async {
      val currentActivity: Activity = appContext?.getCurrentActivity()!!

      if (bluetoothAdapter?.isEnabled == false) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        currentActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
      }
    }
  }

  override fun getPairedDevices(): Array<TBluetoothDevice> {
    val list = mutableListOf<TBluetoothDevice>()
    val pairedDevices = bluetoothAdapter?.bondedDevices
    pairedDevices?.forEach { device ->
      //Log.d("DEVICE_NAME","${device.name}")
      list.add( TBluetoothDevice(
        name = device.name,
        macAddress = device.address, // MacAddress
        type = device.type.toDouble(),
        alias = device.alias
      ))
    }
    return list.toTypedArray()
  }

  companion object {
    const val REQUEST_ENABLE_BT = 1
  }
}
