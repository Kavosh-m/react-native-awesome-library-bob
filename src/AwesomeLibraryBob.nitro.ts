import type { HybridObject } from 'react-native-nitro-modules';

export type TBluetoothDevice = {
  name: string;
  macAddress: string;
  type: number;
  alias?: string;
};

export interface AwesomeLibraryBob
  extends HybridObject<{ ios: 'swift'; android: 'kotlin' }> {
  isBluetoothClassicFeatureAvailable(): boolean;
  isBluetoothOn(): boolean;
  enableBluetooth(): Promise<void>;
  getPairedDevices(): TBluetoothDevice[];
}
