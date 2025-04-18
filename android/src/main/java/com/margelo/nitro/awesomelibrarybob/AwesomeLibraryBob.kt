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
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import java.io.IOException
import java.util.UUID

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
  lateinit var onChangedScanMoode: (mods: TScanMod) -> Unit
  lateinit var btEnableSuccessCallback: () -> Unit
  lateinit var btEnableErrorCallback: (e: TError) -> Unit
  lateinit var onChangeBtState: (e: TBLState) -> Unit
  lateinit var onChangePairState: (e: TBondState) -> Unit


  private var pendingPromise: Promise<Unit>? = null
  private var appUUID = UUID.fromString("93d60870-b9f6-4ab5-8336-703280604994")
  private var a2dpProfile: BluetoothProfile? = null
  private var connectedDevice: BluetoothDevice? = null

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
          onChangedScanMoode(TScanMod(isDiscoveryStarted = true))
        }

        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
          // discovering remote devices finished...
          onChangedScanMoode(TScanMod(isDiscoveryStarted = false))
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

  override fun startScan(
    fetchRemoteDevices: (devices: Array<TBluetoothDevice>) -> Unit,
    onChangedScanMode: (mods: TScanMod) -> Unit
  ) {
    onChangedScannedDevices = fetchRemoteDevices
    onChangedScanMoode = onChangedScanMode

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

  private val btReceiver = object : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      val action: String = intent.action!!
      when (action) {
        BluetoothAdapter.ACTION_STATE_CHANGED -> {
          val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
          when (state) {
            BluetoothAdapter.STATE_ON -> {
              onChangeBtState(
                TBLState(
                  isBluetoothOn = true,
                  isBluetoothTurningOn = false,
                  isBluetoothTurningOff = false
                )
              )
            }

            BluetoothAdapter.STATE_OFF -> {
              onChangeBtState(
                TBLState(
                  isBluetoothOn = false,
                  isBluetoothTurningOn = false,
                  isBluetoothTurningOff = false
                )
              )
            }

            BluetoothAdapter.STATE_TURNING_ON -> {
              onChangeBtState(
                TBLState(
                  isBluetoothOn = false,
                  isBluetoothTurningOn = true,
                  isBluetoothTurningOff = false
                )
              )
            }

            BluetoothAdapter.STATE_TURNING_OFF -> {
              onChangeBtState(
                TBLState(
                  isBluetoothOn = false,
                  isBluetoothTurningOn = false,
                  isBluetoothTurningOff = true
                )
              )
            }
          }
        }
      }
    }
  }

  override fun bluetoothStateEventListener(onChanged: (e: TBLState) -> Unit) {
    onChangeBtState = onChanged

    val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
    appContext?.registerReceiver(btReceiver, filter)
  }

  /**
   * Initializes the A2DP profile proxy.
   * Call this before attempting to connect.
   */
  private fun initA2dpProfile() {
    // Listener for service connection callbacks
    val profileListener = object : BluetoothProfile.ServiceListener {
      override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
        if (profile == BluetoothProfile.A2DP) {
          Log.d("A2DP", "A2DP Profile Proxy Connected.")
          a2dpProfile = proxy
        }
      }

      override fun onServiceDisconnected(profile: Int) {
        if (profile == BluetoothProfile.A2DP) {
          Log.d("A2DP", "A2DP Profile Proxy Disconnected.")
          a2dpProfile = null
          connectedDevice = null
        }
      }
    }

    // Establish connection to the proxy
    bluetoothAdapter?.getProfileProxy(appContext, profileListener, BluetoothProfile.A2DP)
  }


  private val btPairReceiver = object : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
      val action: String = intent.action!!
      when (action) {
        BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
          val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1)
          when (state) {
            BluetoothDevice.BOND_BONDED -> {
              onChangePairState(
                TBondState(
                  isPaired = true,
                  isPairing = false
                )
              )
            }

            BluetoothDevice.BOND_BONDING -> {
              onChangePairState(
                TBondState(
                  isPaired = false,
                  isPairing = true
                )
              )
            }

            BluetoothDevice.BOND_NONE -> {
              onChangePairState(
                TBondState(
                  isPaired = false,
                  isPairing = false
                )
              )
            }
          }
        }
      }
    }
  }

  override fun pairDevice(macAddress: String, onChanged: (e: TBondState) -> Unit) {
    initA2dpProfile()

    onChangePairState = onChanged

    val device: BluetoothDevice? = try {
      bluetoothAdapter?.getRemoteDevice(macAddress)
    } catch (e: IllegalArgumentException) {
      Log.e("PAIR", "Invalid Bluetooth address for pairing: $macAddress", e)
      null
    }

    if (device == null) {
      Log.e("PAIR", "Device not found for pairing: $macAddress")
    }

    // Check current bond state
    when (device?.bondState) {
      BluetoothDevice.BOND_BONDED -> {
        Log.i("PAIR", "Device ${device?.name} is already paired.")
        onChangePairState(
          TBondState(
            isPaired = true,
            isPairing = false
          )
        )
      }

      BluetoothDevice.BOND_BONDING -> {
        Log.i("PAIR", "Device ${device?.name} is currently pairing.")
        onChangePairState(
          TBondState(
            isPaired = false,
            isPairing = true
          )
        )
      }

      BluetoothDevice.BOND_NONE -> {
        Log.i("PAIR", "Initiating pairing with ${device?.name}...")
        onChangePairState(
          TBondState(
            isPaired = false,
            isPairing = false
          )
        )

        val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        appContext?.registerReceiver(btPairReceiver, filter)

        val pairingInitiated = device?.createBond()!!
        if (pairingInitiated) {
          Log.i(
            "PAIR",
            "Pairing process initiated for ${device?.name}. Monitor ACTION_BOND_STATE_CHANGED."
          )
        } else {
          Log.e("PAIR", "Failed to initiate pairing for ${device?.name}.")
        }
      }

      else -> {
        Log.e("PAIR", "Unknown bond state for ${device?.name}: ${device?.bondState}")
        onChangePairState(
          TBondState(
            isPaired = false,
            isPairing = false
          )
        )
      }
    }
  }

  override fun connectToDevice(macAddress: String) {
//    initA2dpProfile()

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
    const val APP_NAME = "BluetoothExampleApp"
  }
}
