package com.margelo.nitro.awesomelibrarybob
  
import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.NitroModules
import android.content.pm.PackageManager
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter

@DoNotStrip
class AwesomeLibraryBob : HybridAwesomeLibraryBobSpec() {

  val appContext = NitroModules.applicationContext
  val packageManager: PackageManager = appContext?.packageManager!!
  val bluetoothManager: BluetoothManager = appContext?.getSystemService(BluetoothManager::class.java)!!
  val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.getAdapter()
  //if (bluetoothAdapter == null) {
    // Device doesn't support Bluetooth
  //}

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  override fun sum(a: Double, b: Double): Double {
    return a + b
  }

  override fun isBluetoothClassicFeatureAvailable(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
  }

  override fun isBluetoothOn(): Boolean {
    return bluetoothAdapter?.isEnabled ?: false
  }
}
