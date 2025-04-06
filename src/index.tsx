import { NitroModules } from 'react-native-nitro-modules';
import type {
  AwesomeLibraryBob,
  TBluetoothDevice,
} from './AwesomeLibraryBob.nitro';

const AwesomeLibraryBobHybridObject =
  NitroModules.createHybridObject<AwesomeLibraryBob>('AwesomeLibraryBob');

export function isBluetoothClassicFeatureAvailable(): boolean {
  return AwesomeLibraryBobHybridObject.isBluetoothClassicFeatureAvailable();
}

export function isBluetoothOn(): boolean {
  return AwesomeLibraryBobHybridObject.isBluetoothOn();
}

export function enableBluetooth(): Promise<void> {
  return AwesomeLibraryBobHybridObject.enableBluetooth();
}

export function getPairedDevices(): TBluetoothDevice[] {
  return AwesomeLibraryBobHybridObject.getPairedDevices();
}
