import { Platform } from 'react-native';
import { check, request, PERMISSIONS, RESULTS } from 'react-native-permissions';

const androidApiLevel = Platform.Version;
const isAndroid = Platform.OS === 'android';

// neccessery for Android api level 31 and above
export function handleBluetoothConnectPermission(callback: () => void) {
  if (isAndroid && +androidApiLevel >= 31) {
    check(PERMISSIONS.ANDROID.BLUETOOTH_CONNECT).then((status) => {
      switch (status) {
        case RESULTS.UNAVAILABLE:
          return console.log(
            'This feature is not available (on this device / in this context)'
          );
        case RESULTS.DENIED:
          request(PERMISSIONS.ANDROID.BLUETOOTH_CONNECT).then((status) => {
            if (status == RESULTS.GRANTED) {
              callback();
            }
          });
          return console.log(
            'The permission has not been requested / is denied but requestable'
          );
        case RESULTS.BLOCKED:
          return console.log('The permission is denied and not requestable');
        case RESULTS.GRANTED:
          callback();
          return console.log('The permission is granted');
        case RESULTS.LIMITED:
          return console.log('The permission is granted but with limitations');
      }
    });
  } else {
    callback();
  }
}

export function handleBluetoothScanPermission(callback: () => void) {
  if (isAndroid && +androidApiLevel >= 31) {
    check(PERMISSIONS.ANDROID.BLUETOOTH_SCAN).then((status) => {
      switch (status) {
        case RESULTS.UNAVAILABLE:
          return console.log(
            'This feature is not available (on this device / in this context)'
          );
        case RESULTS.DENIED:
          request(PERMISSIONS.ANDROID.BLUETOOTH_SCAN).then((status) => {
            if (status == RESULTS.GRANTED) {
              callback();
            }
          });
          return console.log(
            'The permission has not been requested / is denied but requestable'
          );
        case RESULTS.BLOCKED:
          return console.log('The permission is denied and not requestable');
        case RESULTS.GRANTED:
          callback();
          return console.log('The permission is granted');
        case RESULTS.LIMITED:
          return console.log('The permission is granted but with limitations');
      }
    });
  } else {
    callback();
  }
}

// neccessery for Android api level 31 and above
export function handleLocationPermission(callback: () => void) {
  if (isAndroid && +androidApiLevel >= 31) {
    check(PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION).then((status) => {
      switch (status) {
        case RESULTS.UNAVAILABLE:
          return console.log(
            'This feature is not available (on this device / in this context)'
          );
        case RESULTS.DENIED:
          request(PERMISSIONS.ANDROID.ACCESS_FINE_LOCATION).then((status) => {
            if (status == RESULTS.GRANTED) {
              callback();
            }
          });
          return console.log(
            'The permission has not been requested / is denied but requestable'
          );
        case RESULTS.BLOCKED:
          return console.log('The permission is denied and not requestable');
        case RESULTS.GRANTED:
          callback();
          return console.log('The permission is granted');
        case RESULTS.LIMITED:
          return console.log('The permission is granted but with limitations');
      }
    });
  } else {
    callback();
  }
}
