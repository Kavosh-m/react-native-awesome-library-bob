import {
  Text,
  View,
  StyleSheet,
  Pressable,
  ScrollView,
  ActivityIndicator,
} from 'react-native';
import {
  enableBluetooth,
  isBluetoothClassicFeatureAvailable,
  getPairedDevices,
  startScan,
  stopScan,
  bluetoothStateEventListener,
} from 'react-native-awesome-library-bob';
import {
  handleBluetoothConnectPermission,
  handleBluetoothScanPermission,
} from './utils/permissions';
import { useEffect, useState } from 'react';
import type {
  TBLState,
  TBluetoothDevice,
} from '../../src/AwesomeLibraryBob.nitro';

const isBluetoothFeatureAvailable = isBluetoothClassicFeatureAvailable();

export default function App() {
  const [pairedDevices, setPairedDevices] = useState<TBluetoothDevice[]>([]);
  const [scannedDevices, setScannedDevices] = useState<TBluetoothDevice[]>([]);
  const [isDiscoveryStarted, setIsDiscoveryStarted] = useState(false);
  const [btState, setBtState] = useState<TBLState>({
    isBluetoothOn: false,
    isBluetoothTurningOff: false,
    isBluetoothTurningOn: false,
  });

  useEffect(() => {
    bluetoothStateEventListener((e) => {
      setBtState((prev) => ({ ...prev, ...e }));
      console.log('Bluetooth state ===> ', JSON.stringify(e, null, 2));
    });
  }, []);

  const handleEnablingBluetooth = () => {
    handleBluetoothConnectPermission(() => {
      enableBluetooth(
        () => {
          console.log('Bluetooth enabled successfully');
        },
        (e) => {
          console.log(
            'Error enabling Bluetooth ===> ',
            JSON.stringify(e, null, 2)
          );
        }
      );
    });
  };

  const handleGetPairedDevices = () => {
    handleBluetoothConnectPermission(() => {
      const pairedDevicess = getPairedDevices();
      setPairedDevices(() => pairedDevicess);
    });
  };

  const handleStartScanDevices = () => {
    handleBluetoothConnectPermission(() => {
      handleBluetoothScanPermission(() => {
        startScan(
          (devices) => {
            console.log('Scanned devices:', JSON.stringify(devices, null, 2));
            setScannedDevices(() => devices);
          },
          (mods) => {
            console.log('is discovery started ===> ', mods.isDiscoveryStarted);
            setIsDiscoveryStarted(mods.isDiscoveryStarted);
          }
        );
      });
    });
  };

  if (!isBluetoothFeatureAvailable) {
    return (
      <View style={styles.container}>
        <Text>Bluetooth is not available on this device</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.text}>
        {`Bluetooth is ${btState.isBluetoothOn ? 'On' : btState.isBluetoothTurningOn ? 'turning On' : btState.isBluetoothTurningOff ? 'turning Off' : 'Off'}`}
      </Text>
      <Pressable style={styles.button} onPress={handleEnablingBluetooth}>
        <Text style={styles.text}>Enable Bluetooth</Text>
      </Pressable>
      <Pressable style={styles.button} onPress={handleGetPairedDevices}>
        <Text style={styles.text}>Get Paired Devices</Text>
      </Pressable>

      {/**list of paired devices */}
      {pairedDevices.length > 0 && (
        <View style={styles.flexWrapper}>
          <ScrollView style={styles.flexWrapper}>
            <Text style={[styles.text, styles.title]}>Paired Devices:</Text>
            {pairedDevices.map((device, index) => (
              <View key={index} style={styles.deviceWrapper}>
                <Text style={styles.text}>{`Device Name: ${device.name}`}</Text>
                <Text
                  style={styles.text}
                >{`Device MacAddress: ${device.macAddress}`}</Text>
              </View>
            ))}
          </ScrollView>
        </View>
      )}

      {/**list of scanned devices */}
      {scannedDevices?.length > 0 && (
        <View style={styles.flexWrapper}>
          <ScrollView style={styles.flexWrapper}>
            <View
              style={{ flexDirection: 'row', justifyContent: 'space-between' }}
            >
              <Text style={[styles.text, styles.title]}>Scanned Devices:</Text>
              {isDiscoveryStarted && (
                <ActivityIndicator size="large" color="white" />
              )}
            </View>
            {scannedDevices.map((device, index) => (
              <View key={index} style={styles.deviceWrapper}>
                <Text style={styles.text}>{`Device Name: ${device.name}`}</Text>
                <Text
                  style={styles.text}
                >{`Device MacAddress: ${device.macAddress}`}</Text>
              </View>
            ))}
          </ScrollView>
        </View>
      )}

      <View style={styles.rowWrapper}>
        {/**start scan */}
        <Pressable
          style={[styles.button, { opacity: isDiscoveryStarted ? 0.4 : 1 }]}
          onPress={handleStartScanDevices}
          disabled={isDiscoveryStarted}
        >
          <Text style={styles.text}>Scan Devices</Text>
        </Pressable>

        <Pressable
          style={styles.button}
          onPress={() => {
            stopScan();
          }}
        >
          <Text style={styles.text}>Stop Scan</Text>
        </Pressable>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  text: {
    fontSize: 16,
    color: '#fff',
  },
  button: {
    backgroundColor: '#007AFF',
    padding: 10,
    borderRadius: 5,
    marginTop: 20,
  },
  flexWrapper: {
    flex: 1,
  },
  title: {
    marginVertical: 12,
  },
  deviceWrapper: {
    margin: 14,
  },
  rowWrapper: {
    flexDirection: 'row',
  },
});
