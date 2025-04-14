import { NitroModules } from 'react-native-nitro-modules';
import type {
  AwesomeLibraryBob,
  TBluetoothDevice,
  TError,
} from './AwesomeLibraryBob.nitro';

const AwesomeLibraryBobHybridObject =
  NitroModules.createHybridObject<AwesomeLibraryBob>('AwesomeLibraryBob');

export function isBluetoothClassicFeatureAvailable(): boolean {
  return AwesomeLibraryBobHybridObject.isBluetoothClassicFeatureAvailable();
}

export function isBluetoothOn(): boolean {
  return AwesomeLibraryBobHybridObject.isBluetoothOn();
}

export function enableBluetooth(
  successCallback: () => void,
  errorCallback: (e: TError) => void
): void {
  return AwesomeLibraryBobHybridObject.enableBluetooth(
    successCallback,
    errorCallback
  );
}

export function getPairedDevices(): TBluetoothDevice[] {
  return AwesomeLibraryBobHybridObject.getPairedDevices();
}

export function startScan(
  fetchRemoteDevices: (devices: TBluetoothDevice[]) => void
): void {
  return AwesomeLibraryBobHybridObject.startScan(fetchRemoteDevices);
}

export function stopScan(): void {
  return AwesomeLibraryBobHybridObject.stopScan();
}
