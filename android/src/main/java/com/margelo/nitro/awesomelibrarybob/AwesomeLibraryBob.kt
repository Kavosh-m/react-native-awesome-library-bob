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
import android.util.Log

@DoNotStrip
class AwesomeLibraryBob : HybridAwesomeLibraryBobSpec() {

  override val memorySize: Long
    get() = 15

  val appContext = NitroModules.applicationContext
  val packageManager: PackageManager = appContext?.packageManager!!
  val bluetoothManager: BluetoothManager =
    appContext?.getSystemService(BluetoothManager::class.java)!!
  val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()

  private var devicesFound = mutableSetOf<TBluetoothDevice>()
  lateinit var onChangedScannedDevices: (devices: Array<TBluetoothDevice>) -> Unit
  lateinit var btEnableSuccessCallback: () -> Unit
  lateinit var btEnableErrorCallback: (e: TError) -> Unit


  private var pendingPromise: Promise<Unit>? = null

  override fun isBluetoothClassicFeatureAvailable(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
  }

  override fun isBluetoothOn(): Boolean {
    return bluetoothAdapter?.isEnabled ?: false
  }

//  private var btEnablePromise = Promise

  private val activityEventListener =
    object : BaseActivityEventListener() {
      override fun onActivityResult(
        activity: Activity?,
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
      ) {
        if (requestCode == REQUEST_ENABLE_BT) {
          when (resultCode) {
            Activity.RESULT_CANCELED -> {
              btEnableErrorCallback(
                TError(
                  code = BT_ENABLE_ERR_CODE,
                  message = BT_ENABLE_ERR_MSG
                )
              )
            }

            Activity.RESULT_OK -> {
              btEnableSuccessCallback()
            }

            else -> {
              //do something
            }
          }

        } else {
          throw Error("Wrong Intent!")
        }
      }
    }

  init {
    appContext?.addActivityEventListener(activityEventListener)
  }

  override fun enableBluetooth(successCallback: () -> Unit, errorCallback: (e: TError) -> Unit) {
    btEnableSuccessCallback = successCallback
    btEnableErrorCallback = errorCallback

    val currentActivity: Activity = appContext?.getCurrentActivity()!!

    if (bluetoothAdapter?.isEnabled == false) {
      val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
      currentActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
    } else {
      btEnableSuccessCallback()
    }
  }

  override fun getPairedDevices(): Array<TBluetoothDevice> {
    val list = mutableListOf<TBluetoothDevice>()
    val pairedDevices = bluetoothAdapter?.bondedDevices
    pairedDevices?.forEach { device ->
      list.add(
        TBluetoothDevice(
          name = device?.name ?: "",
          macAddress = device.address, // MacAddress
          type = device.type.toDouble(),
          alias = device.alias
        )
      )
    }
    return list.toTypedArray()
  }

  // Create a BroadcastReceiver for ACTION_FOUND.
  private val receiver = object : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      val action: String = intent.action!!
      when (action) {
        BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
          // discovering remote devices started...
        }

        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
          // discovering remote devices finished...
        }

        BluetoothDevice.ACTION_FOUND -> {
          // Discovery has found a device. Get the BluetoothDevice
          // object and its info from the Intent.
          val device: BluetoothDevice =
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)!!

          devicesFound.add(
            TBluetoothDevice(
              name = device?.name ?: "-No name-",
              macAddress = device.address ?: "-No mac address-",
              type = device.type.toDouble(),
              alias = device.alias
            )
          )

          onChangedScannedDevices(devicesFound.toTypedArray())
        }
      }
    }
  }

  override fun startScan(fetchRemoteDevices: (devices: Array<TBluetoothDevice>) -> Unit) {
    onChangedScannedDevices = fetchRemoteDevices

    bluetoothAdapter?.startDiscovery()

    val filterStart = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
    appContext?.registerReceiver(receiver, filterStart)

    val filterFinish = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
    appContext?.registerReceiver(receiver, filterFinish)

    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    appContext?.registerReceiver(receiver, filter)

  }

  override fun stopScan() {
    bluetoothAdapter?.cancelDiscovery()
  }

  /*  fun onDestroy() {
  //    appContext?.onDestroy()

      // Don't forget to unregister the ACTION_FOUND receiver.
      appContext?.unregisterReceiver(receiver)
    }*/

  companion object {
    const val REQUEST_ENABLE_BT = 1
    const val BT_ENABLE_ERR_CODE = "USER_CANCELED"
    const val BT_ENABLE_ERR_MSG = "User cancel or denied enabling bluetooth"

  }
}
