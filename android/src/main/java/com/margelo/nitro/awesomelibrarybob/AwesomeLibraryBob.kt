package com.margelo.nitro.awesomelibrarybob
  
import com.facebook.proguard.annotations.DoNotStrip

@DoNotStrip
class AwesomeLibraryBob : HybridAwesomeLibraryBobSpec() {
  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }
}
