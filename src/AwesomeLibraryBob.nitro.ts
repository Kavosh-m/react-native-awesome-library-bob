import type { HybridObject } from 'react-native-nitro-modules';

export type TBluetoothDevice = {
  name: string;
  macAddress: string;
  type: number;
  alias?: string;
};

export type TError = { code: string; message: string };
export type TScanMod = { isDiscoveryStarted: boolean };
export type TBLState = {
  isBluetoothOn: boolean;
  isBluetoothTurningOn: boolean;
  isBluetoothTurningOff: boolean;
};

export interface AwesomeLibraryBob
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  isBluetoothClassicFeatureAvailable(): boolean;
  isBluetoothOn(): boolean;
  enableBluetooth(
    successCallback: () => void,
    errorCallback: (e: TError) => void
  ): void;
  getPairedDevices(): TBluetoothDevice[];
  startScan(
    fetchRemoteDevices: (devices: TBluetoothDevice[]) => void,
    onChangedScanMode: (mods: TScanMod) => void
  ): void;
  stopScan(): void;
  bluetoothStateEventListener(onChanged: (e: TBLState) => void): void;
}
