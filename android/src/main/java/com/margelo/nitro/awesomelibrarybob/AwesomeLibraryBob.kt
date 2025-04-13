package com.margelo.nitro.awesomelibrarybob

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import com.margelo.nitro.core.Promise
import com.facebook.react.bridge.BaseActivityEventListener
import android.content.pm.PackageManager
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log

@DoNotStrip
class AwesomeLibraryBob : HybridAwesomeLibraryBobSpec() {

  override val memorySize: Long
    get() = 15

  val appContext = NitroModules.applicationContext
  val packageManager: PackageManager = appContext?.packageManager!!
  val bluetoothManager: BluetoothManager = appContext?.getSystemService(BluetoothManager::class.java)!!
  val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
  //if (bluetoothAdapter == null) {
    // Device doesn't support Bluetooth
  //}

  private var devicesFound = mutableSetOf<TBluetoothDevice>()
  lateinit var onChangedScannedDevices: (devices: Array<TBluetoothDevice>) -> Unit

  override fun getScannedDevices(): Array<TBluetoothDevice> {
    return devicesFound.toTypedArray()
  }

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

  // Create a BroadcastReceiver for ACTION_DISCOVERY_STARTED.
  private val receiverStart = object : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      val action: String = intent.action!!
      when(action) {
        BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
          Log.d("DISCOVERY", "Bluetooth Scanning started...")
          /*val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
          appContext?.registerReceiver(receiver, filter)*/
        }
      }
    }
  }

  // Create a BroadcastReceiver for ACTION_FOUND.
  private val receiver = object : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      val action: String = intent.action!!
      when(action) {
        BluetoothDevice.ACTION_FOUND -> {
          // Discovery has found a device. Get the BluetoothDevice
          // object and its info from the Intent.
          val device: BluetoothDevice =
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)!!
//          val deviceName = device.name
//          val deviceHardwareAddress = device.address // MAC address

          devicesFound.add(
            TBluetoothDevice(
              name = device?.name ?: "-No name-",
              macAddress = device.address ?: "-No mac address-",
              type = device.type.toDouble(),
              alias = device.alias
            )
          )

          onChangedScannedDevices(devicesFound.toTypedArray())

//          Log.d("DEVICE_FOUND", "Name => ${deviceName} *** MacAddress => ${deviceHardwareAddress}")
        }
      }
    }
  }

  override fun startScan(fetchRemoteDevices: (devices: Array<TBluetoothDevice>) -> Unit) {
//      Log.d("SCAN_RES", "res is ==> ${bluetoothAdapter?.startDiscovery()}")
    onChangedScannedDevices = fetchRemoteDevices

    bluetoothAdapter?.startDiscovery()

    val filterStart = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
    appContext?.registerReceiver(receiverStart, filterStart)

    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    appContext?.registerReceiver(receiver, filter)

  }

  override fun stopScan() {
    bluetoothAdapter?.cancelDiscovery()
  }

//  fun onCreate(savedInstanceState: Bundle?) {
    // Register for broadcasts when a device is discovered.
    /*val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    appContext?.registerReceiver(receiver, filter)*/
//  }

/*  fun onDestroy() {
//    appContext?.onDestroy()

    // Don't forget to unregister the ACTION_FOUND receiver.
    appContext?.unregisterReceiver(receiver)
  }*/

  companion object {
    const val REQUEST_ENABLE_BT = 1
  }
}
