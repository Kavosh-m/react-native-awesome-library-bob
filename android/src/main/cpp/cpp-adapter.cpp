#include <jni.h>
#include "awesomelibrarybobOnLoad.hpp"

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
  return margelo::nitro::awesomelibrarybob::initialize(vm);
}
